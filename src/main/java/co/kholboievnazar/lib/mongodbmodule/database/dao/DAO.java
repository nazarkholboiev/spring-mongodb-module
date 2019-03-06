package co.kholboievnazar.lib.mongodbmodule.database.dao;

import co.kholboievnazar.lib.mongodbmodule.database.DBContext;
import co.kholboievnazar.lib.mongodbmodule.database.IMorphiaProvider;
import co.kholboievnazar.lib.mongodbmodule.database.search.Filter;
import co.kholboievnazar.lib.mongodbmodule.database.search.FilterDTO;
import co.kholboievnazar.lib.mongodbmodule.database.search.QueryFilterBuilder;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import co.kholboievnazar.lib.mongodbmodule.database.search.Comparator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

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
public class DAO<T extends AbstractMongoModel> {

    @Autowired
    protected IMorphiaProvider iMorphiaProvider;

    final Class<T> typeParameterClass;

    public DAO(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    public List<T> getAll() {
        return DBContext.getMorphiaSingletonProvider().getDatastore().createQuery(typeParameterClass).asList();
    }

    public void add(T object) {
        object.setLastChangeTime(System.currentTimeMillis());
        DBContext.getMorphiaSingletonProvider().getDatastore().save(object);
    }

    public T getByMongoId(String mongoId) {
        return mongoId == null ? null : DBContext.getMorphiaSingletonProvider().getDatastore()
                .createQuery(typeParameterClass).field("_id").equal(new ObjectId(mongoId)).get();
    }

    public List<T> getByMongoId(List<String> mongoIds) {
        if (mongoIds == null) {
            mongoIds = new ArrayList<>();
        }
        List<ObjectId> ids = new ArrayList<>();
        for (String id : mongoIds) {
            ids.add(new ObjectId(id));
        }
        return DBContext.getMorphiaSingletonProvider().getDatastore()
                .createQuery(typeParameterClass).field("_id").hasAnyOf(ids).asList();
    }

    public void update(T object, Map<String, Object> fields) {
        final Query<T> query = DBContext.getMorphiaSingletonProvider().getDatastore().createQuery(typeParameterClass).
                field("_id").equal(object.getId());
        UpdateOperations<T> operations = DBContext.getMorphiaSingletonProvider().getDatastore()
                .createUpdateOperations(typeParameterClass);
        for (String key : fields.keySet()) {
            operations = operations.set(key, fields.get(key));
        }
        operations = operations.set("lastChangeTime", System.currentTimeMillis());
        final UpdateOperations<T> finalOperation = operations;
        DBContext.getMorphiaSingletonProvider().getDatastore().update(query, finalOperation);
    }

    public void delete(T object) {
        DBContext.getMorphiaSingletonProvider().getDatastore().delete(object);
    }

    public void delete(String mongoId) {
        delete(getByMongoId(mongoId));
    }

    public List<T> search(Map<String, List<Filter>> filters, int limit, boolean withoutDeleted) {
        if (withoutDeleted && !filters.containsKey("isDeleted")) {
            Filter<Boolean> delFilter = new Filter<>();
            delFilter.setComparator(Comparator.notEqual);
            delFilter.setValue(true);
            filters.put("isDeleted", Arrays.asList(delFilter));
        }
        Query<T> query = DBContext.getMorphiaSingletonProvider().getDatastore().createQuery(typeParameterClass);
        QueryFilterBuilder.addFilters(query, filters).limit(limit);
        return query.asList();
    }

    public List<T> search(Map<String, List<Filter>> filters, int limit) {
        return search(filters, limit, true);
    }

    public List<T> search(Map<String, List<Filter>> filters) {
        return search(filters, 10);
    }

    public List<T> search(FilterDTO filters) {
        return search(filters, 10, true, false);
    }

    public List<T> search(FilterDTO filters, boolean last) {
        return search(filters, 10, true, last);
    }

    public List<T> search(FilterDTO filters, int limit, boolean withoutDeleted, boolean last) {
        if (withoutDeleted) {
            Filter<Boolean> delFilter = new Filter<>();
            delFilter.setComparator(Comparator.notEqual);
            delFilter.setValue(true);
            delFilter.setFieldName("isDeleted");
            if (filters.getFilters() == null) {
                filters.setFilters(new ArrayList<>());
            }
            filters.getFilters().add(delFilter);
        }
        Query<T> query = DBContext.getMorphiaSingletonProvider().getDatastore().createQuery(typeParameterClass);
        if (last) {
            List<T> list = (List<T>) QueryFilterBuilder.addFilters(query, filters.getFilters()).order("-_id").limit(limit).asList();
            Collections.reverse(list);
            return list;
        } else {
            QueryFilterBuilder.addFilters(query, filters.getFilters()).limit(limit);
        }
        return query.asList();
    }

    public long count(Map<String, List<Filter>> filters) {
        Query<T> query = DBContext.getMorphiaSingletonProvider().getDatastore().createQuery(typeParameterClass);
        return DBContext.getMorphiaSingletonProvider().getDatastore().getCount(QueryFilterBuilder.addFilters(query, filters));
    }

    public long count(FilterDTO filters) {
        Query<T> query = DBContext.getMorphiaSingletonProvider().getDatastore().createQuery(typeParameterClass);
        return DBContext.getMorphiaSingletonProvider().getDatastore().getCount(QueryFilterBuilder.addFilters(query, filters.getFilters()));
    }

    public long countAll() {
        return DBContext.getMorphiaSingletonProvider().getDatastore().createQuery(typeParameterClass).countAll();
    }
}
