package co.kholboievnazar.lib.mongodbmodule.database.search;

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
public class Filter<T> {
    private String fieldName;
    private Comparator comparator;
    private T value;
    private FilterGroup filterGroup;

    public Comparator getComparator() {
        return comparator;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public FilterGroup getFilterGroup() {
        return filterGroup;
    }

    public void setFilterGroup(FilterGroup filterGroup) {
        this.filterGroup = filterGroup;
    }

    public boolean isFilterGroup() {
        return filterGroup != null;
    }
}
