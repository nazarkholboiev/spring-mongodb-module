package co.kholboievnazar.lib.mongodbmodule.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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
public class HomeController {
    @RequestMapping(value = "/")
    public String createUser() {
        return "redirect:/info";
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) {
        Resource resource = new ClassPathResource("info.txt");
        if (!resource.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            try {
                File file = resource.getFile();
                response.setCharacterEncoding("utf-8");
                response.setContentType(Files.probeContentType(file.toPath()));
                Files.copy(file.toPath(), response.getOutputStream());
                response.getOutputStream().flush();
            } catch (IOException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}
