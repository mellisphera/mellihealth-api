package com.mellisphera.entities.bm;

import java.io.Serializable;
import java.sql.Timestamp;
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
	private Timestamp createDate;
	@JsonProperty("name")
	private String name;
	@JsonProperty("hidden")
	private Boolean hidden;
	@JsonProperty("dataLastReceived")
	private Timestamp dataLastReceived;
	@JsonProperty("devices")
	private BmSensor[] devices;
	@JsonProperty("notes")
	private BmNote[] notes;


	public BmHive() {
		
	}

	public BmHive(String hiveId, String apiaryId, Timestamp createDate, String name, Boolean hidden, Timestamp dataLastReceived, BmSensor[] devices) {
		this.hiveId = hiveId;
		this.apiaryId = apiaryId;
		this.createDate = createDate;
		this.name = name;
		this.hidden = hidden;
		this.dataLastReceived = dataLastReceived;
		this.devices = devices;
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
}
