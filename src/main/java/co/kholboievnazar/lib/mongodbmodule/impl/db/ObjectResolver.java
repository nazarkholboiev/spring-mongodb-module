package co.kholboievnazar.lib.mongodbmodule.impl.db;

import co.kholboievnazar.lib.mongodbmodule.database.dao.AbstractMongoModel;
import co.kholboievnazar.lib.mongodbmodule.database.search.IObjectResolver;
import co.kholboievnazar.lib.mongodbmodule.database.search.ObjectResolveResult;
import co.kholboievnazar.lib.mongodbmodule.impl.models.Account;
import co.kholboievnazar.lib.mongodbmodule.impl.models.Car;
import co.kholboievnazar.lib.mongodbmodule.impl.models.Plant;
import org.bson.types.ObjectId;

/**
 * Created by Nazar Kholboiev on 5/27/2017.
 */
public class ObjectResolver implements IObjectResolver {
    public ObjectResolveResult getObjectForDBRef(String collectionName, String mongoId) {
        ObjectResolveResult result = null;
        if (collectionName.equals("accounts")) {
            result = new ObjectResolveResult<>(Account.class);
            AbstractMongoModel model = new Account();
            model.setId(new ObjectId(mongoId));
            result.setObject(model);
        } else if (collectionName.equals("cars")) {
            result = new ObjectResolveResult<>(Car.class);
            AbstractMongoModel model = new Car();
            model.setId(new ObjectId(mongoId));
            result.setObject(model);
        } else if (collectionName.equals("plants")) {
            result = new ObjectResolveResult<>(Plant.class);
            AbstractMongoModel model = new Car();
            model.setId(new ObjectId(mongoId));
            result.setObject(model);
        } //...
        return result;
    }
}
