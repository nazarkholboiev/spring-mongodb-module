package co.kholboievnazar.lib.mongodbmodule.database.search;

import co.kholboievnazar.lib.mongodbmodule.database.dao.AbstractMongoModel;

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
public class ObjectResolveResult<T extends AbstractMongoModel> {
    private final Class<T> typeParameterClass;
    private T object;

    public ObjectResolveResult(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    public Class<T> getTypeParameterClass() {
        return typeParameterClass;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }
}
