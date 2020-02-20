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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import com.mellisphera.entities.User;
import com.mellisphera.entities.UserPref;
import com.mellisphera.repositories.UserRepository;

@Service
@RestController
@RequestMapping("/userPref")
@PreAuthorize("hasRole('STANDARD') or hasRole('PREMIUM') or hasRole('ADMIN')")
public class UserPrefController {

	@Autowired UserRepository userRepository;
	@Autowired PasswordEncoder encoder;
	
	public UserPrefController() {
		super();
	}
	
	@PutMapping("/update/{userId}")
	public void updateUserPref(@PathVariable String userId, @RequestBody UserPref userPref) {
		User user = this.userRepository.findById(userId).get();
		user.setUserPref(userPref);
		this.userRepository.save(user);
	}

	@GetMapping("/user/{userId}")
	public UserPref getUserPrefByUser(@PathVariable String userId) {
		return this.userRepository.findById(userId).get().getUserPref();
	}
	
    @PutMapping("/updatePassword/{idUser}")
    public void changePassword(@PathVariable String idUser, @RequestBody String newPassword) {
    	User user = this.userRepository.findById(idUser).get();
    	user.setPassword(this.encoder.encode(newPassword));
    	this.userRepository.save(user);
    }

}
