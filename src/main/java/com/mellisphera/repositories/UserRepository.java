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



package com.mellisphera.repositories;

//import com.apiwatch.entities.Login;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.mellisphera.entities.User;

@Service
@Repository
public interface UserRepository extends MongoRepository<User,String>{
	
	//User findUserByLogin(Login login);
	User findUserByUsername(String username);
	
	User findUserByEmail(String email);

	//
	boolean existsByUsername(String username);
	
	//
	boolean existsByEmail(String email);
	//
	List<User> findByCreatedAtLike(String todayDate);
}


