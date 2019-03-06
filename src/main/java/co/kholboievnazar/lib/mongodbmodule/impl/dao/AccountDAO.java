package co.kholboievnazar.lib.mongodbmodule.impl.dao;

import co.kholboievnazar.lib.mongodbmodule.database.dao.DAO;
import co.kholboievnazar.lib.mongodbmodule.impl.models.Account;
import org.springframework.stereotype.Component;

/**
 * Created by Nazar Kholboiev on 5/27/2017.
 */
@Component
public class AccountDAO extends DAO<Account> {

    public AccountDAO() {
        super(Account.class);
    }

    public Account getAccountByEmail(String email) {
        return iMorphiaProvider.getDatastore()
                .createQuery(Account.class).filter("email", email).get();
    }
}
