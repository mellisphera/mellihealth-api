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



package com.mellisphera.entities.bm;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mellisphera.entities.Note;

public class BmHive implements Serializable{

	/*
	 * "uuid": "bBsXpy30jFq6ghQdr1vI85aP9ilz2tSu",
		"created": "2018-10-12 17:31:37",
		"name": "R1",
		"data_last_received": "2019-04-10 12:35:22",
		"devices": []
	 */
	@JsonProperty("hiveId")
	private String hiveId;
	@JsonProperty("apiaryId")
	private String apiaryId;
	@JsonProperty("createDate")
	private long createDate;
	@JsonProperty("name")
	private String name;
	@JsonProperty("hidden")
	private Boolean hidden;
	@JsonProperty("dataLastReceived")
	private long dataLastReceived;
	@JsonProperty("devices")
	private BmSensor[] devices;
	@JsonProperty("notes")
	private BmNote[] notes;
	@JsonProperty("apiaryLocations")
	private ApiaryLocation[] apiaryLocation;

	public BmHive() {

	}


	public BmHive(String hiveId, String apiaryId, long createDate, String name, Boolean hidden, long dataLastReceived, BmSensor[] devices, BmNote[] notes, ApiaryLocation[] apiaryLocation) {
		this.hiveId = hiveId;
		this.apiaryId = apiaryId;
		this.createDate = createDate;
		this.name = name;
		this.hidden = hidden;
		this.dataLastReceived = dataLastReceived;
		this.devices = devices;
		this.notes = notes;
		this.apiaryLocation = apiaryLocation;
	}

	public String getHiveId() {
		return hiveId;
	}

	public void setHiveId(String hiveId) {
		this.hiveId = hiveId;
	}

	public String getApiaryId() {
		return apiaryId;
	}

	public void setApiaryId(String apiaryId) {
		this.apiaryId = apiaryId;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public long getDataLastReceived() {
		return dataLastReceived;
	}

	public void setDataLastReceived(long dataLastReceived) {
		this.dataLastReceived = dataLastReceived;
	}

	public BmSensor[] getDevices() {
		return devices;
	}

	public void setDevices(BmSensor[] devices) {
		this.devices = devices;
	}

	public BmNote[] getNotes() {
		return notes;
	}

	public void setNotes(BmNote[] notes) {
		this.notes = notes;
	}

	public ApiaryLocation[] getApiaryLocation() {
		return apiaryLocation;
	}

	public void setApiaryLocation(ApiaryLocation[] apiaryLocation) {
		this.apiaryLocation = apiaryLocation;
	}

	@Override
	public String toString() {
		return "BmHive{" +
				"hiveId='" + hiveId + '\'' +
				", apiaryId='" + apiaryId + '\'' +
				", createDate=" + createDate +
				", name='" + name + '\'' +
				", hidden=" + hidden +
				", dataLastReceived=" + dataLastReceived +
				", devices=" + Arrays.toString(devices) +
				", notes=" + Arrays.toString(notes) +
				", apiaryLocation=" + apiaryLocation +
				'}';
	}
}
