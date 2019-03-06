package co.kholboievnazar.lib.mongodbmodule.impl.dao;

import co.kholboievnazar.lib.mongodbmodule.database.dao.DAO;
import co.kholboievnazar.lib.mongodbmodule.impl.models.Car;
import org.springframework.stereotype.Component;

/**
 * Created by Nazar Kholboiev on 5/27/2017.
 */
@Component
public class CarDAO extends DAO<Car> {
    public CarDAO() {
        super(Car.class);
    }
}
