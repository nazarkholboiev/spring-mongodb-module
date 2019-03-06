package co.kholboievnazar.lib.mongodbmodule.controller;

import co.kholboievnazar.lib.mongodbmodule.database.search.FilterDTO;
import co.kholboievnazar.lib.mongodbmodule.impl.dao.AccountDAO;
import co.kholboievnazar.lib.mongodbmodule.impl.dao.CarDAO;
import co.kholboievnazar.lib.mongodbmodule.impl.dao.PlantDAO;
import co.kholboievnazar.lib.mongodbmodule.impl.models.Car;
import co.kholboievnazar.lib.mongodbmodule.impl.models.Plant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
public class PlantController {

    @Autowired
    private AccountDAO accountDAO;
    @Autowired
    private CarDAO carDAO;
    @Autowired
    private PlantDAO plantDAO;

    @RequestMapping(value = "/api/plant/search", method = RequestMethod.POST)
    @ResponseBody
    public List<Plant> searchPlants(@RequestBody FilterDTO body,
                                    @RequestParam(name = "last", required = false) boolean last,
                                    @RequestParam(name = "limit", required = false) Integer limit) {
        return plantDAO.search(body, limit == null ? 10 : limit, true, last);
    }

    @RequestMapping(value = "/api/plant/create", method = RequestMethod.POST)
    @ResponseBody
    public Plant createPlant(@RequestBody Plant plant) {
        plantDAO.add(plant);
        return plant;
    }

    @RequestMapping(value = "/api/plant/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Plant getPlantById(@PathVariable String id) {
        return plantDAO.getByMongoId(id);
    }

    @RequestMapping(value = "/api/plant/{id}/update", method = RequestMethod.POST)
    @ResponseBody
    public Plant updatePlantById(@PathVariable String id, @RequestBody Map<String, Object> body, HttpServletResponse response) {
        Plant plant = plantDAO.getByMongoId(id);
        if (plant == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        plantDAO.update(plant, body);
        return plantDAO.getByMongoId(plant._id());
    }

    @RequestMapping(value = "/api/plant/{id}/update_cars", method = RequestMethod.POST)
    @ResponseBody
    public Plant getPlantsCarsById(@PathVariable String id, @RequestBody Map<String, List<String>> body, HttpServletResponse response) {
        Plant plant = plantDAO.getByMongoId(id);
        if (plant == null || !body.containsKey("car_ids")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("cars", carDAO.getByMongoId(body.get("car_ids")));
        plantDAO.update(plant, params);
        return plantDAO.getByMongoId(plant._id());
    }

    @RequestMapping(value = "/api/plant/{id}/add_car/{car_id}", method = RequestMethod.POST)
    @ResponseBody
    public Plant addCarToPlantId(@PathVariable String id, @PathVariable String car_id, HttpServletResponse response) {
        Plant plant = plantDAO.getByMongoId(id);
        Car car = carDAO.getByMongoId(car_id);
        if (plant == null || car == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        List<String> carIds = new ArrayList<>();
        for (Car c : plant.getCars()) {
            carIds.add(c._id());
        }
        if (!carIds.contains(car._id())) {
            plant.addCar(car);
            HashMap<String, Object> params = new HashMap<>();
            params.put("cars", plant.getCars());
            plantDAO.update(plant, params);
        } else {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }
        return plant;
    }

    @RequestMapping(value = "/api/plant/{id}/remove_car/{car_id}", method = RequestMethod.POST)
    @ResponseBody
    public Plant removeCarToPlantId(@PathVariable String id, @PathVariable String car_id, HttpServletResponse response) {
        Plant plant = plantDAO.getByMongoId(id);
        Car car = carDAO.getByMongoId(car_id);
        if (plant == null || car == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        if (!plant.getCars().contains(car)) {
            plant.removeCar(car._id());
            HashMap<String, Object> params = new HashMap<>();
            params.put("cars", plant.getCars());
            plantDAO.update(plant, params);
        }
        return plant;
    }

    @RequestMapping(value = "/api/plant/{id}/delete", method = RequestMethod.POST)
    @ResponseBody
    public Plant deletePlantById(@PathVariable String id, HttpServletResponse response) {
        Plant plant = plantDAO.getByMongoId(id);
        if (plant == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("isDeleted", true);
        plantDAO.update(plant, params);
        return plant;
    }

}
