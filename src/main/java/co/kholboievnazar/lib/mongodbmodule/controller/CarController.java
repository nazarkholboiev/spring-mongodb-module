package co.kholboievnazar.lib.mongodbmodule.controller;

import co.kholboievnazar.lib.mongodbmodule.database.search.FilterDTO;
import co.kholboievnazar.lib.mongodbmodule.impl.dao.CarDAO;
import co.kholboievnazar.lib.mongodbmodule.impl.models.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
public class CarController {

    @Autowired
    private CarDAO carDAO;

    @RequestMapping(value = "/api/cars/search", method = RequestMethod.POST)
    @ResponseBody
    public List<Car> searchPlants(@RequestBody FilterDTO body,
                                  @RequestParam(name = "last", required = false) boolean last,
                                  @RequestParam(name = "limit", required = false) Integer limit) {
        return carDAO.search(body, limit == null ? 10 : limit, true, last);
    }

    @RequestMapping(value = "/api/cars/count", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Long> searchLength(@RequestBody FilterDTO body) {
        HashMap<String, Long> result = new HashMap<>();
        result.put("count", carDAO.count(body));
        return result;
    }

    @RequestMapping(value = "/api/cars/create", method = RequestMethod.POST)
    @ResponseBody
    public Car createPlant(@RequestBody Car car) {
        carDAO.add(car);
        return car;
    }

    @RequestMapping(value = "/api/cars/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Car getPlantById(@PathVariable String id) {
        return carDAO.getByMongoId(id);
    }

    @RequestMapping(value = "/api/cars/{id}/update", method = RequestMethod.POST)
    @ResponseBody
    public Car updatePlantById(@PathVariable String id, @RequestBody Map<String, Object> body, HttpServletResponse response) {
        Car car = carDAO.getByMongoId(id);
        if (car == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        carDAO.update(car, body);
        return carDAO.getByMongoId(car._id());
    }

    @RequestMapping(value = "/api/cars/{id}/delete", method = RequestMethod.POST)
    @ResponseBody
    public Car deletePlantById(@PathVariable String id, @RequestBody Map<String, Object> body, HttpServletResponse response) {
        Car car = carDAO.getByMongoId(id);
        if (car == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("isDeleted", true);
        carDAO.update(car, params);
        return car;
    }
}
