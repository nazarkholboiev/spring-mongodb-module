package co.kholboievnazar.lib.mongodbmodule.impl.db;

import co.kholboievnazar.lib.mongodbmodule.database.IMorphiaProvider;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

/**
 * Created by Nazar Kholboiev on 5/27/2017.
 */
public class MorphiaProvider implements IMorphiaProvider {
    private final Morphia morphia = new Morphia().mapPackage("co.kholboievnazar.lib.mongodbmodule");

    private final Datastore datastore =
            morphia.createDatastore(new MongoClient(new MongoClientURI("mongodb://localhost:27017")), "mongodbmodule");

    public MorphiaProvider() {

    }

    public Morphia getMorphia() {
        return morphia;
    }

    public Datastore getDatastore() {
        return datastore;
    }
}
