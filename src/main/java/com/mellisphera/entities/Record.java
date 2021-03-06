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



package com.mellisphera.entities;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Record")
public class Record {
	
	private String id;
	private Float battery_ext ;
	private Float battery_int;
	private Float humidity_ext;
	private Float humidity_int;
	private Date recordDate;
	private Float weight_icome;
	private String recordsType;
	private String sensorRef;
	private Float temp_ext;
	private Float temp_int;
	private Float weight;
	private String hiveId;
	private Long timestamp;
	
	
	public Record() {
		super();
	}

	public Record(String id, Float battery_ext, Float battery_int, Float humidity_ext, Float humidity_int,
			Date recordDate, Float weight_icome, String recordsType, String sensorRef, Float temp_ext, Float temp_int,
			Float weight, String hiveId, Long timestamp) {
		super();
		this.id = id;
		this.battery_ext = battery_ext;
		this.battery_int = battery_int;
		this.humidity_ext = humidity_ext;
		this.humidity_int = humidity_int;
		this.recordDate = recordDate;
		this.weight_icome = weight_icome;
		this.recordsType = recordsType;
		this.sensorRef = sensorRef;	
		this.temp_ext = temp_ext;
		this.temp_int = temp_int;
		this.weight = weight;
		this.hiveId = hiveId;
                this.timestamp = timestamp;
	}

	public Record(Date recordDate, Float weight) {
		this.recordDate = recordDate;
		this.weight = weight;
	}
	
	public String getId() {
		return id;
	}

        public Long getTimestamp(){
            return this.timestamp;
        }
        
        public void setTimestamp(Long Timestamp){
            this.timestamp = Timestamp;
        }
        
	public void setId(String id) {
		this.id = id;
	}


	public Float getBattery_ext() {
		return battery_ext;
	}


	public void setBattery_ext(Float battery_ext) {
		this.battery_ext = battery_ext;
	}


	public Float getBattery_int() {
		return battery_int;
	}


	public void setBattery_int(Float battery_int) {
		this.battery_int = battery_int;
	}


	public Float getHumidity_ext() {
		return humidity_ext;
	}


	public void setHumidity_ext(Float humidity_ext) {
		this.humidity_ext = humidity_ext;
	}


	public Float getHumidity_int() {
		return humidity_int;
	}


	public void setHumidity_int(Float humidity_int) {
		this.humidity_int = humidity_int;
	}


	public Date getRecordDate() {
		return recordDate;
	}


	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}


	public Float getWeight_icome() {
		return weight_icome;
	}


	public void setWeight_icome(Float weight_icome) {
		this.weight_icome = weight_icome;
	}


	public String getRecordsType() {
		return recordsType;
	}


	public void setRecordsType(String recordsType) {
		this.recordsType = recordsType;
	}


	public String getSensorRef() {
		return sensorRef;
	}


	public void setSensorRef(String sensorRef) {
		this.sensorRef = sensorRef;
	}


	public Float getTemp_ext() {
		return temp_ext;
	}


	public void setTemp_ext(Float temp_ext) {
		this.temp_ext = temp_ext;
	}


	public Float getTemp_int() {
		return temp_int;
	}


	public void setTemp_int(Float temp_int) {
		this.temp_int = temp_int;
	}


	public Float getWeight() {
		return weight;
	}


	public void setWeight(Float weight) {
		this.weight = weight;
	}


	public String gethiveId() {
		return hiveId;
	}


	public void sethiveId(String hiveId) {
		this.hiveId = hiveId;
	}
	
	


	
	
	
	
}
