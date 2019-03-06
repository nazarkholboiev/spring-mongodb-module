package co.kholboievnazar.lib.mongodbmodule.impl.models;

import co.kholboievnazar.lib.mongodbmodule.database.dao.AbstractMongoModel;
import org.mongodb.morphia.annotations.Entity;

/**
 * Created by Nazar Kholboiev on 5/27/2017.
 */
@Entity("cars")
public class Car extends AbstractMongoModel {
    private String model;
    private String vehicleCode;
    private String image;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVehicleCode() {
        return vehicleCode;
    }

    public void setVehicleCode(String vehicleCode) {
        this.vehicleCode = vehicleCode;
    }
}
