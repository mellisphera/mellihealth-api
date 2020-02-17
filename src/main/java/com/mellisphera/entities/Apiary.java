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

import java.io.File;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Apiary")
public class Apiary {

	@Id
	private String _id;
	private String name;
	private String description;
	private String zipCode;
	private Boolean privateApiary;
	private String city;
	private Boolean hidden;
	private String userId;
	private Date createDate;
	private String countryCode;
	private String photo;
	private Date dataLastReceived;
	private String username;
	private List<User> sharedWith;
	
	
	public Apiary() {
		super();
	}

	public Apiary(String _id, String name, String description, String zipCode, Boolean privateApiary, String city, String userId, Date createDate, String countryCode, String photo, Date dataLastReceived, String username, List<User> sharedWith, Boolean hidden) {
		this._id = _id;
		this.name = name;
		this.hidden = hidden;
		this.description = description;
		this.zipCode = zipCode;
		this.privateApiary = privateApiary;
		this.city = city;
		this.userId = userId;
		this.createDate = createDate;
		this.countryCode = countryCode;
		this.photo = photo;
		this.dataLastReceived = dataLastReceived;
		this.username = username;
		this.sharedWith = sharedWith;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public Boolean getPrivateApiary() {
		return privateApiary;
	}

	public void setPrivateApiary(Boolean privateApiary) {
		this.privateApiary = privateApiary;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Date getDataLastReceived() {
		return dataLastReceived;
	}

	public void setDataLastReceived(Date dataLastReceived) {
		this.dataLastReceived = dataLastReceived;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<User> getSharedWith() {
		return sharedWith;
	}

	public void setSharedWith(List<User> sharedWith) {
		this.sharedWith = sharedWith;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}
}
