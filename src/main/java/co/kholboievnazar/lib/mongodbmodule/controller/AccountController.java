package co.kholboievnazar.lib.mongodbmodule.controller;

import co.kholboievnazar.lib.mongodbmodule.database.search.FilterDTO;
import co.kholboievnazar.lib.mongodbmodule.impl.dao.AccountDAO;
import co.kholboievnazar.lib.mongodbmodule.impl.dao.PlantDAO;
import co.kholboievnazar.lib.mongodbmodule.impl.models.Account;
import co.kholboievnazar.lib.mongodbmodule.impl.models.Plant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Controller
public class AccountController {
    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private PlantDAO plantDAO;

    @RequestMapping(value = "/api/account/create", method = RequestMethod.POST)
    @ResponseBody
    public Account createUser(@RequestBody Account user, HttpServletResponse response) {
        user.setEmail(user.getEmail().toLowerCase().trim());
        if (accountDAO.getAccountByEmail(user.getEmail()) != null) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }
        accountDAO.add(user);
        return user;
    }

    @RequestMapping(value = "/api/account/search", method = RequestMethod.POST)
    @ResponseBody
    public List<Account> searchAccount(@RequestBody FilterDTO body,
                                       @RequestParam(name = "last", required = false) boolean last,
                                       @RequestParam(name = "limit", required = false) Integer limit) {
        return accountDAO.search(body, limit == null ? 10 : limit, true, last);
    }

    @RequestMapping(value = "/api/account/count", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Long> searchLength(@RequestBody FilterDTO body) {
        HashMap<String, Long> result = new HashMap<>();
        result.put("count", accountDAO.count(body));
        return result;
    }

    @RequestMapping(value = "/api/account/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Account getAccountById(@PathVariable String id) {
        return accountDAO.getByMongoId(id);
    }

    @RequestMapping(value = "/api/account/{id}/delete", method = RequestMethod.POST)
    @ResponseBody
    public Account deleteAccountById(@PathVariable String id, HttpServletResponse response) {
        Account account = accountDAO.getByMongoId(id);
        if (account == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("isDeleted", true);
        params.put("roles", new ArrayList<String>());
        accountDAO.update(account, params);
        return accountDAO.getByMongoId(account._id());
    }

    @RequestMapping(value = "/api/account/{id}/update", method = RequestMethod.POST)
    @ResponseBody
    public Account updateAccountById(@PathVariable String id, @RequestBody Map<String, Object> body, HttpServletResponse response) {
        Account account = accountDAO.getByMongoId(id);
        if (account == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        accountDAO.update(account, body);
        return accountDAO.getByMongoId(account._id());
    }

    @RequestMapping(value = "/api/account/{account_id}/set_plant/{plant_id}", method = RequestMethod.POST)
    @ResponseBody
    public Account getAccountsPlantById(@PathVariable String account_id, @PathVariable String plant_id, HttpServletResponse response) {
        Account account = accountDAO.getByMongoId(account_id);
        Plant plant = plantDAO.getByMongoId(plant_id);
        if (account == null || plant == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("plant", plant);
        accountDAO.update(account, params);
        return accountDAO.getByMongoId(account._id());
    }

}
