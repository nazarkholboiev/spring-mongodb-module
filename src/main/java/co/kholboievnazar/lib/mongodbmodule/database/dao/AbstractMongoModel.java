package co.kholboievnazar.lib.mongodbmodule.database.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

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
public abstract class AbstractMongoModel {
    @Id
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected ObjectId id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected long lastChangeTime;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    protected boolean isDeleted;

    @JsonIgnore
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getLastChangeTime() {
        return lastChangeTime;
    }

    public void setLastChangeTime(long lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String _id() {
        return id.toString();
    }
}
