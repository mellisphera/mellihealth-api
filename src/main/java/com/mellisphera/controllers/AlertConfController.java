/* Copyright 2018-present Mellisphera
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */ 



package com.mellisphera.controllers;

import com.mellisphera.entities.AlertUser;
import com.mellisphera.entities.AlertsCat;
import com.mellisphera.repositories.AlertUserRepository;
import com.mellisphera.repositories.AlertsCatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alertsConf")
public class AlertConfController {

    @Autowired private AlertUserRepository alertUserRepository;
    @Autowired private AlertsCatRepository alertsCatRepository;

    @GetMapping("/{userId}")
    public AlertUser getConfByUser(@PathVariable String userId) {
        return this.alertUserRepository.findByUserId(userId);
    }

    @GetMapping("/all")
    public List<AlertUser> getAll() {
        return this.alertUserRepository.findAll();
    }

    @GetMapping("rez")
    public void rezConf() {
        List<AlertUser> alerts = this.alertUserRepository.findAll();
        alerts.forEach(_al -> {
            _al.getAlertConf().forEach((key, value) -> {
                AlertsCat origin = this.alertsCatRepository.findById(key).get();
                value.setValueImp(origin.getBasicValueImp());
                value.setValueMet(origin.getBasicValueMet());
            });
            this.alertUserRepository.save(_al);
        });
    }

    @PutMapping("/update")
    public AlertUser updateConf(@RequestBody AlertUser alertUser) {
        return this.alertUserRepository.save(alertUser);
    }
}
