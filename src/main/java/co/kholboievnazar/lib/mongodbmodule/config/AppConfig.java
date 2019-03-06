package co.kholboievnazar.lib.mongodbmodule.config;

import co.kholboievnazar.lib.mongodbmodule.database.IMorphiaProvider;
import co.kholboievnazar.lib.mongodbmodule.database.search.IObjectResolver;
import co.kholboievnazar.lib.mongodbmodule.impl.db.MorphiaProvider;
import co.kholboievnazar.lib.mongodbmodule.impl.db.ObjectResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

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
@Configuration
@ComponentScan(basePackages = "co.kholboievnazar.lib.mongodbmodule")
@EnableWebMvc
@EnableScheduling
public class AppConfig extends WebMvcConfigurerAdapter {
    @Bean
    public ViewResolver getViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/views/");
        return resolver;
    }

    @Bean
    public IMorphiaProvider getMorphiaProvider() {
        IMorphiaProvider iMorphiaProvider = new MorphiaProvider();
        return iMorphiaProvider;
    }

    @Bean
    public IObjectResolver getObjectResolver() {
        IObjectResolver iObjectResolver = new ObjectResolver();
        return iObjectResolver;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }
}

