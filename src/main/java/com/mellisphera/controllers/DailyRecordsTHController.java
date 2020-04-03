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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import com.mellisphera.entities.DailyRecordsTH;
import com.mellisphera.entities.Hive;
import com.mellisphera.entities.SimpleSeries;
import com.mellisphera.repositories.DailyRecordsTHRepository;
import com.mellisphera.repositories.HivesRepository;

@Service
@RestController
@RequestMapping("/dailyRecordsTH")
public class DailyRecordsTHController {


	@Autowired private HiveController hiveController;
	@Autowired private DailyRecordsTHRepository dailyRecordsTHRepository;
	@Autowired private HivesRepository hivesRepository;
	private MongoTemplate mongoTemplate;
	public DailyRecordsTHController(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@RequestMapping(value="/all", method = RequestMethod.GET, produces={"application/json"})
	public List<DailyRecordsTH> getAll(){
		return this.dailyRecordsTHRepository.findAll();
	}


	@RequestMapping(value="/hive/{hiveId}", method = RequestMethod.GET, produces={"application/json"})
	public List<DailyRecordsTH> getByhiveId(@PathVariable("hiveId") String hiveId){
		return this.dailyRecordsTHRepository.findDailyRecordsTHByHiveId(hiveId);
	}

	@DeleteMapping("/{id}")
	public void deleteById(@PathVariable("id") String id) {
		this.dailyRecordsTHRepository.deleteById(id);
	}
        
    /*@RequestMapping(value = "/last/{hiveId}" , method = RequestMethod.POST, produces={"application/json"})
	@GetMapping("/last/{hiveId}/{start}/{end}" )
    public List<DailyRecordsTH> getLastDailyRecord(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end){
        return this.dailyRecordsTHRepository.findByHiveIdAndRecordDateBetween(hiveId, new Date(start), new Date(end), new Sort(Direction.DESC, "timestamp"));
	}*/

	@GetMapping("/apiary/{idApiary}/{start}/{end}")
	public Map<String, List<DBObject>> getByApiary(@PathVariable String idApiary, @PathVariable long start, @PathVariable long end){

		List<Hive> hives = this.hiveController.getAllUserHives(idApiary);
		List<DailyRecordsTH> dailyRecTh = new ArrayList<>();
		Map<String, List<DBObject>> resByHive = new HashMap<>();
		for(Hive h : hives) {
			resByHive.put(h.get_id(), this.getBroodByHive(h.get_id(), start, end));

		}
		return resByHive;
	}
	
	/*@PostMapping("tMax/{hiveId}")
	public List<SimpleSeries> getTmaxByHive(@RequestBody Date[] range, @PathVariable String hiveId) {
		Sort sort = new Sort(Direction.DESC, "timestamp");
		return this.dailyRecordsTHRepository.findByHiveIdAndRecordDateBetween(hiveId, range[0], range[1], sort).stream().map(_daily -> new SimpleSeries(_daily
				.getRecordDate(), _daily.getTemp_int_max(), _daily.getSensorRef())).collect(Collectors.toList());
	}*/

	@GetMapping("/tMax/{hiveId}/{start}/{end}")
	public List<DBObject> getTempMax(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end) {
		Sort sort = new Sort(Direction.DESC, "timestamp");
		Criteria filter = Criteria.where("recordDate").gte(new Date(start)).lt(new Date(end));
		Aggregation aggregate;
		aggregate = Aggregation.newAggregation(
				Aggregation.match(filter),
				Aggregation.match(Criteria.where("hiveId").is(hiveId)),
				Aggregation.group("sensorRef").addToSet(new BasicDBObject(){
					{
						put("recordDate", "$recordDate");
						put("temp_int_max", "$temp_int_max");
						put("sensorRef", "$sensorRef");
					}
				}).as("values")
		);
		AggregationResults<DBObject> aggregateRes = this.mongoTemplate.aggregate(aggregate, "DailyRecordsTH", DBObject.class);
		return aggregateRes.getMappedResults();
	}
	@GetMapping("/hInt/{hiveId}/{start}/{end}")
	public List<DBObject> getHintByHive(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end){
		Sort sort = new Sort(Direction.DESC, "timestamp");
		Criteria filter = Criteria.where("recordDate").gte(new Date(start)).lt(new Date(end));
		Aggregation aggregate;
		aggregate = Aggregation.newAggregation(
				Aggregation.match(filter),
				Aggregation.match(Criteria.where("humidity_int_max").exists(true)),
				Aggregation.match(Criteria.where("hiveId").is(hiveId)),
				Aggregation.group("sensorRef").addToSet(new BasicDBObject(){
					{
						put("recordDate", "$recordDate");
						put("humidity_int_max", "$humidity_int_max");
						put("sensorRef", "$sensorRef");
					}
				}).as("values")
		);
		AggregationResults<DBObject> aggregateRes = this.mongoTemplate.aggregate(aggregate, "DailyRecordsTH", DBObject.class);
		return aggregateRes.getMappedResults();
	}

	@GetMapping("/brood/{hiveId}/{start}/{end}")
	public List<DBObject> getBroodByHive(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end){
		Sort sort = new Sort(Direction.DESC, "timestamp");
		Criteria filter = Criteria.where("recordDate").gte(new Date(start)).lt(new Date(end));
		Aggregation aggregate;
		aggregate = Aggregation.newAggregation(
				Aggregation.match(filter),
				Aggregation.sort(Direction.DESC, "timestamp"),
				Aggregation.match(Criteria.where("hiveId").is(hiveId)),
				Aggregation.group("sensorRef").addToSet(new BasicDBObject(){
					{
						put("recordDate", "$recordDate");
						put("brood", "$brood");
						put("timestamp", "$timestamp");
						put("sensorRef", "$sensorRef");
					}
				}).as("values")
		);
		AggregationResults<DBObject> aggregateRes = this.mongoTemplate.aggregate(aggregate, "DailyRecordsTH", DBObject.class);
		return aggregateRes.getMappedResults();
	}

	@PostMapping("brood/old/{hiveId}")
	public List<SimpleSeries> getBroodByHive(@RequestBody Date[] range, @PathVariable String hiveId){
		Sort sort = new Sort(Direction.DESC, "timestamp");
		return this.dailyRecordsTHRepository.findByHiveIdAndRecordDateBetween(hiveId, range[0], range[1], sort).stream().map(_daily -> new SimpleSeries(_daily
				.getRecordDate(), _daily.getBrood(), _daily.getSensorRef())).collect(Collectors.toList());
	}

	@GetMapping("/tMin/{hiveId}/{start}/{end}")
	public List<DBObject> getTMinByHive(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end){
		Sort sort = new Sort(Direction.DESC, "timestamp");
		Criteria filter = Criteria.where("recordDate").gte(new Date(start)).lt(new Date(end));
		Aggregation aggregate;
		aggregate = Aggregation.newAggregation(
				Aggregation.match(filter),
				Aggregation.match(Criteria.where("hiveId").is(hiveId)),
				Aggregation.group("sensorRef").addToSet(new BasicDBObject(){
					{
						put("recordDate", "$recordDate");
						put("temp_int_min", "$temp_int_min");
						put("sensorRef", "$sensorRef");
					}
				}).as("values")
		);
		AggregationResults<DBObject> aggregateRes = this.mongoTemplate.aggregate(aggregate, "DailyRecordsTH", DBObject.class);
		return aggregateRes.getMappedResults();
	}
	
	

}
