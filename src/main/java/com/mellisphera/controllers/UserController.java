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

import com.mellisphera.security.entities.BmAuth;
import com.mellisphera.security.message.response.JwtResponse;
import com.mellisphera.security.service.BmServiceImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import com.mellisphera.HttpsGetRequest;
import com.mellisphera.entities.Connection;
import com.mellisphera.entities.Location;
import com.mellisphera.entities.User;
import com.mellisphera.entities.UserPref;
import com.mellisphera.repositories.ConnectionRepository;
import com.mellisphera.repositories.UserRepository;
import com.mongodb.BasicDBObject;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


//@TODO enum { "ROLE_ADMIN", "ROLE_STANDARD", "ROLE_PREMIUM" }
@Service
@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
	public static final String[] SET_INITIAL_ROLE = new String[] { "ROLE_STANDARD" };
    @Autowired
    private UserRepository userRepository;

    @Autowired
    BmServiceImpl bmAuthService;
	
    @Autowired
    private ConnectionRepository connectionRepository;

    public UserController() {

    }


    public UserController(UserRepository userRepository, ConnectionRepository connectionRepository) {
        this.userRepository = userRepository;
        //this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public List<User> getAll() {
      return this.userRepository.findAll();
    }
    
    
    @GetMapping("/usernames")
    public List<String> getAllRuchers() {
        List<User> Users = this.userRepository.findAll();
        List<String> names = new ArrayList<>();
        for (User e : Users) {
            names.add(e.getUsername());
        }

        return names;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("adminLogin/{userId}")
    public JwtResponse loginFromAdmin(@PathVariable String userId) {
        User user = this.userRepository.findById(userId).get();
        return new JwtResponse(user.getId(), null, user.getConnexions(), user.getUsername(),user.getEmail(), null,null, user.getUserPref().getLang());
    }

    @GetMapping("/update/{userId}")
    public void getChangeLogByUserId(@PathVariable String userId) {
        User user = this.userRepository.findById(userId).get();
        this.bmAuthService.getChangeLog(user.getId(), user.getUsername(), user.getUserPref().getLang().toUpperCase());
    }
    
    @GetMapping("/createdAt")
    public List<User> findByCreatedDate() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); // Quoted "Z" to indicate UTC, no timezone offset
        List<User> Users = this.userRepository.findAll();
        List<User> todayUsers = new ArrayList<>();
        //System.out.println("Today date : "+ nowAsISO );

        for (User e : Users) {
            //System.out.println("e.getCreatedAt.getDate(): " + df.format(e.getCreatedAt()));
            //System.out.println("USERNAME : " + e.getUsername());
            if (df.format(e.getCreatedAt()).equals(df.format(new Date()))) {
                todayUsers.add(e);
            }

        }
        return todayUsers;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        this.userRepository.deleteById(id);
    }

    @GetMapping("/{id}")
    public Optional<User> getById(@PathVariable("id") String id) {
        Optional<User> User = this.userRepository.findById(id);
        return User;
    }
    @GetMapping("/check/{userId}")
    public void checkDataForUser(@PathVariable String userId){
        BmAuth userData = this.bmAuthService.getUserData(userId);


    }
}
