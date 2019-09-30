package com.mellisphera.entities.bm;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mellisphera.entities.Note;

public class BmApiary implements Serializable{

	@JsonProperty("apiaryId")
	private String apiaryId;
	@JsonProperty("createDate")
	private Timestamp createDate;
	@JsonProperty("name")
	private String name;
	@JsonProperty("userId")
	private String userId;
	@JsonProperty("zipCode")
	private String zipCode;
	@JsonProperty("countryCode")
	private String countryCode;
	@JsonProperty("private")
	private Boolean privateApiary;
	@JsonProperty("hidden")
	private Boolean hidden;
	@JsonProperty("dataLastReceived")
	private Timestamp dataLastReceived;
	@JsonProperty("hives")
	private BmHive[] hives;
	@JsonProperty("notes")
	private BmNote[] notes;
	
	public BmApiary() {
		
	}

	public BmApiary(String apiaryId, String userId, Timestamp createDate, String name, String zipCode, String countryCode, Boolean privateApiary, Boolean hidden, Timestamp dataLastReceived, BmHive[] hives, BmNote[] notes) {
		this.apiaryId = apiaryId;
		this.createDate = createDate;
		this.name = name;
		this.zipCode = zipCode;
		this.countryCode = countryCode;
		this.privateApiary = privateApiary;
		this.hidden = hidden;
		this.userId = userId;
		this.dataLastReceived = dataLastReceived;
		this.hives = hives;
		this.notes = notes;
	}

	public String getApiaryId() {
		return apiaryId;
	}

	public void setApiaryId(String apiaryId) {
		this.apiaryId = apiaryId;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public Boolean getPrivateApiary() {
		return privateApiary;
	}

	public void setPrivateApiary(Boolean privateApiary) {
		this.privateApiary = privateApiary;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public Timestamp getDataLastReceived() {
		return dataLastReceived;
	}

	public void setDataLastReceived(Timestamp dataLastReceived) {
		this.dataLastReceived = dataLastReceived;
	}

	public BmHive[] getHives() {
		return hives;
	}

	public void setHives(BmHive[] hives) {
		this.hives = hives;
	}

	public BmNote[] getNotes() {
		return notes;
	}

	public void setNotes(BmNote[] notes) {
		this.notes = notes;
	}
}


/*

{
"code": "200",
"message": "Apiary tree for username 'lorenzo.pons@free.fr'",
"output_format": "json",
"payload_record_count": 7,
"client_ip": "83.173.67.13",
"served_by_node": "bio-web-instance-group-2-8rch",
"payload": [
  {
"uuid": "UjpRybCZ2IXFOf8hx4dJGo9PnH0Qs5Bc",
"created": "2017-11-18 21:50:39",
"name": "ZGELOS",
"postal_code": "64110",
"country_code": "FR",
"data_last_received": "2019-04-10 12:45:22",
"hives": [
  {
"uuid": "bBsXpy30jFq6ghQdr1vI85aP9ilz2tSu",
"created": "2018-10-12 17:31:37",
"name": "R1",
"data_last_received": "2019-04-10 12:35:22",
"devices": [
  {
"uuid": "7nm4q6cUkY9gzoK5L3MatRvAbQJlxyGu",
"created": "2018-10-12 17:31:42",
"device_id": "42:15:0C",
"data_last_received": "2019-04-10 12:25:22",
"is_api_linked": "1",
"name": "42:15:0C"
},
  {
"uuid": "FARGdHeKZfclQmwLOUyzkTDN0XEbvVt8",
"created": "2019-01-24 09:10:37",
"device_id": "43:0A:C7",
"data_last_received": "2019-04-10 12:35:22",
"is_api_linked": "1",
"name": "43:0A:C7"
}
],
},*/