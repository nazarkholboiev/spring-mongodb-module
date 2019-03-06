package co.kholboievnazar.lib.mongodbmodule.impl.models;

import co.kholboievnazar.lib.mongodbmodule.database.dao.AbstractMongoModel;
import co.kholboievnazar.lib.mongodbmodule.impl.models.component.Location;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nazar Kholboiev on 5/27/2017.
 */
@Entity("plants")
public class Plant extends AbstractMongoModel {
    private String name;
    private String plantCode;
    @Embedded
    private Location location;
    @Reference
    private List<Car> cars;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public void addCar(Car car) {
        if (cars == null) {
            cars = new ArrayList<>();
        }
        cars.add(car);
    }

    public void removeCar(String carId) {
        if (cars != null) {
            Car[] carArray = new Car[cars.size()];
            carArray = cars.toArray(carArray);
            for (Car car : carArray) {
                if (car._id().equals(carId)) {
                    cars.remove(car);
                }
            }
        }
    }

    public String getPlantCode() {
        return plantCode;
    }

    public void setPlantCode(String plantCode) {
        this.plantCode = plantCode;
    }
}
