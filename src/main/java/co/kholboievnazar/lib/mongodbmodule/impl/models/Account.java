package co.kholboievnazar.lib.mongodbmodule.impl.models;

import co.kholboievnazar.lib.mongodbmodule.database.dao.AbstractMongoModel;
import org.mongodb.morphia.annotations.Entity;

import java.util.List;

/**
 * Created by Nazar Kholboiev on 5/27/2017.
 */
@Entity(value = "accounts")
public class Account extends AbstractMongoModel {
    private String email;
    private String password;
    private List<String> roles;
    private String name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
