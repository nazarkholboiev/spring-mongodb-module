package co.kholboievnazar.lib.mongodbmodule.database.search;

import co.kholboievnazar.lib.mongodbmodule.database.DBContext;
import co.kholboievnazar.lib.mongodbmodule.database.dao.AbstractMongoModel;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Nazar Kholboiev on 5/27/2017.
 */
 /*
    Copyright 2017 Nazar Kholboiev

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
public abstract class QueryFilterBuilder {

    public static Query<?> addFilters(Query<?> query, List<Filter> filters) {
        for (Filter f : filters) {
            if (f.isFilterGroup()) {
                if (f.getFilterGroup().getConnector() == Connector.$and) {
                    query.and(getCriteriaFromFilterGroup(query, f.getFilterGroup()));
                } else if (f.getFilterGroup().getConnector() == Connector.$or) {
                    query.or(getCriteriaFromFilterGroup(query, f.getFilterGroup()));
                }
            } else {
                recognizeFilter(query, f.getFieldName(), f);
            }
        }
        return query;
    }

    public static Criteria[] getCriteriaFromFilterGroup(Query<?> query, FilterGroup filterGroup) {
        List<Criteria> criteriaList = new ArrayList<>();
        for (Filter f : filterGroup.getFilters()) {
            Criteria c = getCriteriaByFilter(query, f);
            if (c != null) {
                criteriaList.add(c);
            }
        }
        return criteriaList.toArray(new Criteria[criteriaList.size()]);
    }

    public static Criteria getCriteriaByFilter(Query<?> query, Filter filter) {
        String fieldName = filter.getFieldName();
        filter = transformFilter(fieldName, filter);
        if (fieldName.contains("#in")) {
            if (filter.getValue() instanceof AbstractMongoModel) {
                return query.criteria(fieldName.split("#")[0]).hasAnyOf(new ArrayList<>(Arrays.asList(((AbstractMongoModel) filter.getValue()).getId())));
            } else {
                return query.criteria(fieldName.split("#")[0]).hasAnyOf(new ArrayList<>(Arrays.asList(filter.getValue())));
            }
        }
        if (filter.getValue() instanceof AbstractMongoModel) {
            return query.disableValidation().criteria(fieldName).equal(((AbstractMongoModel) filter.getValue()).getId());
        } else {
            switch (filter.getComparator()) {
                case equal:
                    return query.criteria(fieldName).equal(filter.getValue());
                case notEqual:
                    return query.criteria(fieldName).notEqual(filter.getValue());
                case greater:
                    return query.criteria(fieldName).greaterThan(filter.getValue());
                case greaterOrEqual:
                    return query.criteria(fieldName).greaterThanOrEq(filter.getValue());
                case less:
                    return query.criteria(fieldName).lessThan(filter.getValue());
                case lessOrEqual:
                    return query.criteria(fieldName).lessThanOrEq(filter.getValue());
                case startWith:
                    return query.criteria(fieldName).equal(Pattern.compile("^" + filter.getValue() + ".*", Pattern.CASE_INSENSITIVE));
            }
        }
        return null;
    }

    public static Query<?> addFilters(Query<?> query, Map<String, List<Filter>> filterMap) {
        for (String s : filterMap.keySet())
            for (Filter f : filterMap.get(s))
                recognizeFilter(query, s, f);
        return query;
    }

    public static void recognizeFilter(Query<?> query, String fieldName, Filter filter) {
        filter = transformFilter(fieldName, filter);
        if (fieldName.contains("#in")) {
            if (filter.getValue() instanceof AbstractMongoModel) {
                query.field(fieldName.split("#")[0]).hasAnyOf(new ArrayList<>(Arrays.asList(((AbstractMongoModel) filter.getValue()).getId())));
            } else {
                query.field(fieldName.split("#")[0]).hasAnyOf(new ArrayList<>(Arrays.asList(filter.getValue())));
            }
            return;
        }
        if (fieldName.contains("#nin")) {
            if (filter.getValue() instanceof AbstractMongoModel) {
                query.field(fieldName.split("#")[0]).hasNoneOf(new ArrayList<>(Arrays.asList(((AbstractMongoModel) filter.getValue()).getId())));
            } else {
                query.field(fieldName.split("#")[0]).hasNoneOf(new ArrayList<>(Arrays.asList(filter.getValue())));
            }
            return;
        }
        if (filter.getValue() instanceof AbstractMongoModel) {
            query.disableValidation().field(fieldName).equal(((AbstractMongoModel) filter.getValue()).getId());
        } else {
            switch (filter.getComparator()) {
                case equal:
                    query.field(fieldName).equal(filter.getValue());
                    break;
                case notEqual:
                    query.field(fieldName).notEqual(filter.getValue());
                    break;
                case greater:
                    query.field(fieldName).greaterThan(filter.getValue());
                    break;
                case greaterOrEqual:
                    query.field(fieldName).greaterThanOrEq(filter.getValue());
                    break;
                case less:
                    query.field(fieldName).lessThan(filter.getValue());
                    break;
                case lessOrEqual:
                    query.field(fieldName).lessThanOrEq(filter.getValue());
                    break;
                case startWith:
                    query.field(fieldName).equal(Pattern.compile("^" + filter.getValue() + ".*", Pattern.CASE_INSENSITIVE));
                    break;
            }
        }
    }

    private static Filter transformFilter(String fieldName, Filter filter) {
        if (fieldName.contains("_id")) {
            Filter<ObjectId> changedFilter = new Filter<>();
            changedFilter.setValue(new ObjectId(filter.getValue().toString()));
            changedFilter.setComparator(filter.getComparator());
            filter = changedFilter;
        } else if (fieldName.contains("$id")) {
            Filter<AbstractMongoModel> changedFilter = new Filter<>();
            String[] parts = fieldName.split("\\.");
            String collectionName = fieldName.split("\\.")[parts.length - 2] + "s";
            ObjectResolveResult resolveResult = DBContext.getObjectResolver().getObjectForDBRef(collectionName, filter.getValue().toString());
            changedFilter.setValue(resolveResult.getObject());
            changedFilter.setComparator(filter.getComparator());
            filter = changedFilter;
        }
        return filter;
    }
}
