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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.mellisphera.repositories.SensorRepository;
import com.mellisphera.service.Unit;
import com.mellisphera.service.UnitService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mellisphera.entities.Record;
import com.mellisphera.entities.SimpleSeries;
import com.mellisphera.repositories.RecordRepository;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/records")
public class RecordController {
		
	@Autowired private RecordRepository recordRepository;
	@Autowired private SensorRepository sensorRepository;
    private final MongoTemplate mongoTemplate;

	Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired private UnitService unitService;
    public RecordController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    @RequestMapping(value = "", method = RequestMethod.POST, produces={"application/json"})
    public void insert(@RequestBody Record record){
        this.recordRepository.insert(record);
    }
    
    @GetMapping("/all")
    public List<Record> getAllSensors(){
    List<Record> records=this.recordRepository.findAll();
    	return records;
    }
    
    @GetMapping("/hive/{hiveId}/{start}/{end}")
    public List<DBObject> getByHiveId(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end){
        Criteria filter = Criteria.where("recordDate").gte(new Date(start)).lt(new Date(end));
        Aggregation aggregate;
        aggregate = Aggregation.newAggregation(
                Aggregation.match(filter),
                Aggregation.match(Criteria.where("hiveId").is(hiveId)),
                Aggregation.group("sensorRef").addToSet(new BasicDBObject(){
                    {
                        put("recordDate", "$recordDate");
                        put("temp_int", "$temp_int");
                        put("weight", "$weight");
                        put("humidity_int", "$humidity_int");
                        put("humidity_ext", "$humidity_ext");
                        put("temp_text", "$temp_ext");
                        put("sensorRef", "$sensorRef");
                        //put("battery_int", "$battery_int");
                    }
                }).as("values")
        );
        AggregationResults<DBObject> aggregateRes = this.mongoTemplate.aggregate(aggregate, "Record", DBObject.class);
        return aggregateRes.getMappedResults();
    }

    @GetMapping("/weight/{hiveId}/{start}/{end}")
    public List<DBObject> getWeightByHive(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end){
        Criteria filter = Criteria.where("recordDate").gte(new Date(start)).lt(new Date(end));
        Aggregation aggregate;
        aggregate = Aggregation.newAggregation(
                Aggregation.match(filter),
                Aggregation.match(Criteria.where("recordType").is("WEIGHT")),
                Aggregation.match(Criteria.where("hiveId").is(hiveId)),
                Aggregation.group("sensorRef").addToSet(new BasicDBObject(){
                    {
                        put("recordDate", "$recordDate");
                        put("weight", "$weight");
                        put("sensorRef", "$sensorRef");
                        //put("battery_int", "$battery_int");
                    }
                }).as("values")
        );
        AggregationResults<DBObject> aggregateRes = this.mongoTemplate.aggregate(aggregate, "Record", DBObject.class);
        return aggregateRes.getMappedResults();

    }

    @GetMapping("/temp_int/{hiveId}/{start}/{end}/{unit}")
    public ResponseEntity<?> getTempByHive(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end, @PathVariable Unit unit){
        Sort sort = new Sort(Direction.DESC, "timestamp");
        List<SimpleSeries> data = new ArrayList<SimpleSeries>();
        data = this.recordRepository.findByHiveIdAndRecordDateBetween(hiveId, new Date(start),new Date(end), sort).stream()
        		.filter(_filter  -> _filter.getSensorRef().contains("42") || _filter.getSensorRef().contains("41") || _filter.getSensorRef().contains("39") || _filter.getSensorRef().contains("B5")).map(record -> {
        	return new SimpleSeries(record.getRecordDate(), this.unitService.convertTempFromUsePref(record.getTemp_int(), unit), record.getSensorRef());
        }).collect(Collectors.toList());
        if(data != null) {
        	return new ResponseEntity<>(data, HttpStatus.OK);
        }
        else {
        	return new ResponseEntity<>("Aucune donnée", HttpStatus.NOT_FOUND);
        }
        
    }
    
    @GetMapping("/temp_ext/{hiveId}/{start}/{end}/{unit}")
    public ResponseEntity<?> getTempExByHive(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end, @PathVariable Unit unit){
        Sort sort = new Sort(Direction.DESC, "timestamp");
        List<SimpleSeries> data = new ArrayList<SimpleSeries>();
        data = this.recordRepository.findByHiveIdAndRecordDateBetween(hiveId, new Date(start),new Date(end), sort).stream()
        		.filter(_filter  -> _filter.getSensorRef().contains("43")).map(record -> {
        	return new SimpleSeries(record.getRecordDate(), this.unitService.convertTempFromUsePref(record.getTemp_ext(), unit), record.getSensorRef());
        }).collect(Collectors.toList());
        if(data != null) {
        	return new ResponseEntity<>(data, HttpStatus.OK);
        }
        else {
        	return new ResponseEntity<>("Aucune donnée", HttpStatus.NOT_FOUND);
        }

    }
    @GetMapping("/hint/{hiveId}/{start}/{end}")
    public ResponseEntity<?> getHUmidityByHive(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end){
        Sort sort = new Sort(Direction.DESC, "timestamp");
        List<SimpleSeries> data = new ArrayList<SimpleSeries>();
        data = this.recordRepository.findByHiveIdAndRecordDateBetween(hiveId, new Date(start), new Date(end), sort).stream()
        		.filter(_filter  -> _filter.getSensorRef().contains("42")).map(record -> {
        	return new SimpleSeries(record.getRecordDate(), record.getHumidity_int(), record.getSensorRef());
        }).collect(Collectors.toList());
        if(data != null) {
        	return new ResponseEntity<>(data, HttpStatus.OK);
        }
        else {
        	return new ResponseEntity<>("Aucune donnée", HttpStatus.NOT_FOUND);
        }
        
    }
    
    @GetMapping("/batExt/{hiveId}/{start}/{end}")
    public ResponseEntity<?> getBatteryExtByHive(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end){
        Sort sort = new Sort(Direction.DESC, "timestamp");
        List<SimpleSeries> data = new ArrayList<SimpleSeries>();
        data = this.recordRepository.findByHiveIdAndRecordDateBetween(hiveId, new Date(start), new Date(end), sort).stream()
        		.filter(_filter  -> _filter.getSensorRef().contains("43")).map(record -> {
        	return new SimpleSeries(record.getRecordDate(), record.getBattery_ext(), record.getSensorRef());
        }).collect(Collectors.toList());
        if(data != null) {
        	return new ResponseEntity<>(data, HttpStatus.OK);
        }
        else {
        	return new ResponseEntity<>("Aucunetemp_ext donnée", HttpStatus.NOT_FOUND);
        }
        
    }
    
    @GetMapping("/batInt/{hiveId}/{start}/{end}")
    public ResponseEntity<?> getBatteryIntByHive(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end){
        Sort sort = new Sort(Direction.DESC, "timestamp");
        List<SimpleSeries> data = new ArrayList<SimpleSeries>();
        data = this.recordRepository.findByHiveIdAndRecordDateBetween(hiveId, new Date(start), new Date(end), sort).stream()
        		.filter(_filter  -> _filter.getSensorRef().contains("42") || _filter.getSensorRef().contains("41") || _filter.getSensorRef().contains("39") ||  _filter.getSensorRef().contains("B5")).map(record -> {
        	return new SimpleSeries(record.getRecordDate(), record.getBattery_int(), record.getSensorRef());
        }).collect(Collectors.toList());
        if(data != null) {
        	return new ResponseEntity<>(data, HttpStatus.OK);
        }
        else {
        	return new ResponseEntity<>("Aucune donnée", HttpStatus.NOT_FOUND);
        }
        
    }
}
