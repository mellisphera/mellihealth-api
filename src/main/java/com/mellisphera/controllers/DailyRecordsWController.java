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

import java.util.*;
import java.util.stream.Collectors;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mellisphera.entities.DailyRecordsTH;
import com.mellisphera.entities.DailyRecordsW;
import com.mellisphera.entities.Hive;
import com.mellisphera.entities.SimpleSeries;
import com.mellisphera.repositories.DailyRecordsWRepository;

@Service
@RestController
@RequestMapping("/dailyRecordsW")
public class DailyRecordsWController {

	@Autowired
	private DailyRecordsWRepository dailyRecordsWRepository;
	@Autowired HiveController hiveController;
	private MongoTemplate mongoTemplate;

	public DailyRecordsWController(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces={"application/json"})
	public List<DailyRecordsW> getAll(){
		return this.dailyRecordsWRepository.findAll();
	}
	
	@RequestMapping(value="/{id}", method = RequestMethod.GET, produces={"application/json"})
	public Optional<DailyRecordsW> getById(@PathVariable("id") String id){
		return this.dailyRecordsWRepository.findById(id);
	}
	
	@GetMapping(value="/hive/{hiveId}")
	public List<DailyRecordsW>getByhiveId(@PathVariable("hiveId") String hiveId){
		return this.dailyRecordsWRepository.findDailyRecordsWByHiveId(hiveId);
	}
	
	@GetMapping("/apiary/{idApiary}/{start}/{end}")
	public Map<String, List<DBObject>> getDailyRecordsWByApiary(@PathVariable String idApiary, @PathVariable long start, @PathVariable long end) {
        Sort sort = new Sort(Direction.DESC, "timestamp");
		Map<String, List<DBObject>> mapRes = new HashMap<>();
        this.hiveController.getAllUserHives(idApiary).forEach(_hive -> {
			mapRes.put(_hive.get_id(), this.getWeightByHive(_hive.get_id(), start, end));
		});
        return mapRes;
	}
	
	@GetMapping("/weightIncome/{hiveId}/{start}/{end}")
	public List<DBObject> getWeightIcomeWByHive(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end) {
		Sort sort = new Sort(Direction.DESC, "timestamp");
		Criteria filter = Criteria.where("recordDate").gte(new Date(start)).lt(new Date(end));
		Aggregation aggregate;
		aggregate = Aggregation.newAggregation(
				Aggregation.match(filter),
				Aggregation.match(Criteria.where("hiveId").is(hiveId)),
				Aggregation.group("sensorRef").addToSet(new BasicDBObject(){
					{
						put("recordDate", "$recordDate");
						put("weight_income_gain", "$weight_income_gain");
						put("sensorRef", "$sensorRef");
					}
				}).as("values")
		);
		AggregationResults<DBObject> aggregateRes = this.mongoTemplate.aggregate(aggregate, "DailyRecordsW", DBObject.class);
		return aggregateRes.getMappedResults();

	}
	
	
	@GetMapping("/tMin/{hiveId}/{start}/{end}")
	public List<DBObject> getTminByHive(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end){
		Sort sort = new Sort(Direction.DESC, "timestamp");
		Criteria filter = Criteria.where("recordDate").gte(new Date(start)).lt(new Date(end));
		Aggregation aggregate;
		aggregate = Aggregation.newAggregation(
				Aggregation.match(filter),
				Aggregation.match(Criteria.where("hiveId").is(hiveId)),
				Aggregation.group("sensorRef").addToSet(new BasicDBObject(){
					{
						put("recordDate", "$recordDate");
						put("temp_ext_min", "$temp_ext_min");
						put("sensorRef", "$sensorRef");
					}
				}).as("values")
		);
		AggregationResults<DBObject> aggregateRes = this.mongoTemplate.aggregate(aggregate, "DailyRecordsW", DBObject.class);
		return aggregateRes.getMappedResults();
	}

	@GetMapping("/tMax/{hiveId}/{start}/{end}")
	public List<DBObject> getTmaxByHive(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end){
		Sort sort = new Sort(Direction.DESC, "timestamp");
		Criteria filter = Criteria.where("recordDate").gte(new Date(start)).lt(new Date(end));
		Aggregation aggregate;
		aggregate = Aggregation.newAggregation(
				Aggregation.match(filter),
				Aggregation.match(Criteria.where("hiveId").is(hiveId)),
				Aggregation.group("sensorRef").addToSet(new BasicDBObject(){
					{
						put("recordDate", "$recordDate");
						put("temp_ext_max", "$temp_ext_max");
						put("sensorRef", "$sensorRef");
					}
				}).as("values")
		);
		AggregationResults<DBObject> aggregateRes = this.mongoTemplate.aggregate(aggregate, "DailyRecordsW", DBObject.class);
		return aggregateRes.getMappedResults();
	}
	

	@GetMapping("/weightMax/{hiveId}/{start}/{end}")
	public List<DBObject> getWeightByHive(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end){
		Criteria filter = Criteria.where("recordDate").gte(new Date(start)).lt(new Date(end));
		Aggregation aggregate;
		aggregate = Aggregation.newAggregation(
				Aggregation.match(filter),
				Aggregation.match(Criteria.where("hiveId").is(hiveId)),
				Aggregation.group("sensorRef").addToSet(new BasicDBObject(){
					{
						put("recordDate", "$recordDate");
						put("weight_max", "$weight_max");
						put("sensorRef", "$sensorRef");
					}
				}).as("values")
		);
		AggregationResults<DBObject> aggregateRes = this.mongoTemplate.aggregate(aggregate, "DailyRecordsW", DBObject.class);
		return aggregateRes.getMappedResults();
	}
	
}
