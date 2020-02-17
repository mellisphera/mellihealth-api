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



package com.mellisphera.security.service;

import com.google.gson.Gson;
import com.mellisphera.entities.*;
import com.mellisphera.entities.bm.*;
import com.mellisphera.entities.bm.changeLog.BmApiaryUpdated;
import com.mellisphera.entities.bm.changeLog.BmHiveUpdated;
import com.mellisphera.entities.bm.changeLog.BmNoteUpdated;
import com.mellisphera.entities.bm.changeLog.BmSensorUpdated;
import com.mellisphera.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.mellisphera.security.entities.BmAuth;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BmServiceImpl implements BmService {
	
    @Value("${apiwatch.app.geoip.timeout}")
    private int timeout;
    @Value("${mellisphera.app.bmAuth.url}")
    private String bmUrl;
    @Value("${mellisphera.app.bmAuth.licenceKey}")
    private String licenceKey;
    
    private HttpEntity<MultiValueMap<String, String>> requestEntity;
    private HttpEntity<String> notePostRequestEntity;
    private HttpHeaders header;

    private String userId;
    
    @Autowired private ApiaryRepository apiaryRepository;
    @Autowired private HivesRepository hiveRepository;
    @Autowired private SensorRepository sensorRepository;
    @Autowired private NoteRepository noteRepository;
    @Autowired private UserRepository userRepository;

    @Autowired private BmDataToMellispheraData 	bmToMellispheraData;
    @Autowired private BmChangeLogService changeLogService;

	@Override
	public BmAuth getBmAuth(String username, String password) {
		String urlRequest = this.bmUrl + "user/data";
		this.header = new HttpHeaders();
		this.header.add("Content-Type", "application/x-www-form-urlencoded");
		this.header.add("license_key", this.licenceKey);
		MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<String, String>();
		bodyMap.add("username", username);
		bodyMap.add("password", password);
		this.requestEntity = new HttpEntity<>(bodyMap, this.header);
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		return restTemplate.postForObject(urlRequest, requestEntity, BmAuth.class);
	}


	@Override
	public BmAuth getUserData(String userId) {
		this.header = new HttpHeaders();
		this.header.add("license_key", this.licenceKey);
		String urlRequest = this.bmUrl + "user/data";
		HttpEntity entity = new HttpEntity(this.header);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlRequest)
				.queryParam("userId", userId);
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		HttpEntity<BmAuth> response  = restTemplate.exchange(builder.toUriString(),
				HttpMethod.GET,
				entity,
				BmAuth.class);
		return response.getBody();
	}

	@Override
	public void saveBmData(BmAuth bmData, String username, String countryCode) {
		System.out.println(bmData);
		try{
			this.userId = bmData.getPayload().getUserId();
			for(BmApiary bmApiary: bmData.getPayload().getApiaries()) {
				try {
					this.apiaryRepository.insert(this.bmToMellispheraData.getNewApiary(bmApiary, username, countryCode, false));
				}
				catch (Exception e) {
					System.out.println("error key apiary");
					this.apiaryRepository.deleteById(bmApiary.getApiaryId());
					this.apiaryRepository.insert(this.bmToMellispheraData.getNewApiary(bmApiary, username, countryCode, false));
				}
				for(BmHive bmHive: Arrays.stream(bmApiary.getHives()).sorted(Comparator.comparing(BmHive::getName)).collect(Collectors.toList())) {
					try {
						this.hiveRepository.insert(this.bmToMellispheraData.getNewHive(bmHive, username, this.userId));
					}
					catch (Exception e) {
						this.hiveRepository.deleteById(bmHive.getHiveId());
						this.hiveRepository.insert(this.bmToMellispheraData.getNewHive(bmHive, username, this.userId));

					}
					if (bmHive.getDevices() != null) {
						for(BmSensor bmSensor : bmHive.getDevices()) {
							try {
								this.sensorRepository.insert(this.bmToMellispheraData.getNewSensorFromFirstConnection(bmSensor, this.userId, bmHive));
							} catch (Exception e){
								this.sensorRepository.deleteById(bmSensor.getDevice().getDeviceId());
								this.sensorRepository.insert(this.bmToMellispheraData.getNewSensorFromFirstConnection(bmSensor, this.userId, bmHive));
							}
						}
					}
					if (bmHive.getNotes() != null) {
						for (BmNote bmNote: bmHive.getNotes()) {
							try {
								this.noteRepository.insert(this.bmToMellispheraData.getNewNote(bmNote, this.userId));
							}
							catch (Exception e)  {
								this.noteRepository.deleteById(bmNote.getNoteId());
								this.noteRepository.insert(this.bmToMellispheraData.getNewNote(bmNote, this.userId));
							}
						}
					}
				}
				this.bmToMellispheraData.resetPos();
				if (bmApiary.getNotes() != null) {
					for (BmNote bmNote: bmApiary.getNotes()) {
						try {
							this.noteRepository.insert(this.bmToMellispheraData.getNewNote(bmNote, this.userId));
						}
						catch (Exception e)  {
							this.noteRepository.deleteById(bmNote.getNoteId());
							this.noteRepository.insert(this.bmToMellispheraData.getNewNote(bmNote, this.userId));
						}
					}
				}
			}
			this.bmToMellispheraData.resetPos();
		}catch (NullPointerException e){
			e.printStackTrace();
		}
	}

	@Override
	public Object putNote(BmNote bmNote){
		String urlRequest = this.bmUrl + "notes";
		this.header = new HttpHeaders();
        //this.header.add("Content-Type", "application/json");
        this.header.add("license_key", this.licenceKey);
		Gson gson = new Gson();
		String noteJson = gson.toJson(bmNote);
		this.notePostRequestEntity = new HttpEntity<>(noteJson, this.header);
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
        //restTemplate.put(urlRequest, this.notePostRequestEntity);
       return restTemplate.exchange(urlRequest, HttpMethod.PUT, this.notePostRequestEntity, Object.class);
    }


	@Override
	public void getChangeLog(String userId, String username, String countryCode) {
		this.header = new HttpHeaders();
		this.header.add("license_key", this.licenceKey);
		String urlRequest = this.bmUrl + "user/changeLog";
		HttpEntity entity = new HttpEntity(this.header);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlRequest)
				.queryParam("userId", userId);
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		HttpEntity<BmAuth> response  = restTemplate.exchange(builder.toUriString(),
				HttpMethod.GET,
				entity,
				BmAuth.class);
		System.out.println(response.getBody());
		if (response.getBody() != null) {
			this.saveChangeLog(response.getBody(), username, userId, countryCode);
		} else {
			System.err.println("Aucune mise à jours");
		}
	}

	@Override
	public void deleteChangeLog(long modified, String userId) {
		String urlRequest = this.bmUrl +  "user/changeLog";
		HttpHeaders header = new HttpHeaders();
		header.add("license_key", this.licenceKey);
		HttpEntity<String> requestEntity = new HttpEntity<>(header);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlRequest)
			.queryParam("userId", userId)
			.queryParam("modified", modified);
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<BmAuth> response  = restTemplate.exchange(builder.toUriString(),
				HttpMethod.DELETE,
				requestEntity,
				BmAuth.class);
	}

	public String getUserId() {
		return this.userId;
	}

	@Override
	public BmNoteCreate postNote(BmNote bmNote){
		String urlRequest = this.bmUrl +  "notes";
		this.header = new HttpHeaders();
		this.header.add("Content-Type", "application/json");
		this.header.add("license_key", this.licenceKey);
		Gson gson = new Gson();
		String noteJson = gson.toJson(bmNote);
		this.notePostRequestEntity = new HttpEntity<>(noteJson, this.header);
		RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
		BmNoteCreate note = restTemplate.postForObject(urlRequest, this.notePostRequestEntity, BmNoteCreate.class);
		return note;
	}


	public void saveSensorFromBmSensor(BmSensor[] bmSensors, String userId, BmHive _bmHive) {
		Arrays.stream(bmSensors).map(_sensor -> this.bmToMellispheraData.getNewSensorFromFirstConnection(_sensor, userId, _bmHive)).collect(Collectors.toList()).forEach(_newSensor -> {
			boolean hiveExist = this.sensorRepository.findById(_newSensor.get_id()).isPresent();
			if (hiveExist) {
				this.sensorRepository.save(_newSensor);
			} else {
				this.sensorRepository.insert(_newSensor);
			}
		});
	}

	private void saveChangeLog(BmAuth change, String username, String userId, String countryCode) {
		try{
			if (change.getPayload().getApiaries() != null) {
				this.changeLogService.saveApiaryFromBmApiary(change.getPayload().getApiaries(), username, countryCode);
			}
			if (change.getPayload().getBmNoteCreate() != null) {
				this.changeLogService.saveNoteFromBmNote(change.getPayload().getBmNoteCreate(), userId);
			}
			if (change.getPayload().getBmHiveCreate() != null) {
				this.changeLogService.saveHiveFromBmHive(change.getPayload().getBmHiveCreate(), username, userId);
			}
			if (change.getPayload().getDevicesCreate() != null) {
				this.changeLogService.saveSensorFromBmDevice(change.getPayload().getDevicesCreate(), userId);
			}

			if (change.getPayload().getApiaryDelete() != null) {
				for (String id: change.getPayload().getApiaryDelete()) {
					this.apiaryRepository.deleteById(id);
					this.hiveRepository.deleteHiveByApiaryId(id);
				}
			}
			if (change.getPayload().getHiveDelete() != null) {
				for (String id: change.getPayload().getHiveDelete()) {
					this.hiveRepository.deleteById(id);
				}
			}
			if (change.getPayload().getDeviceDelete() != null) {
				for (String id: change.getPayload().getDeviceDelete()) {
					this.sensorRepository.deleteById(id);
				}
			}
			if (change.getPayload().getNoteDelete() != null) {
				for (String id: change.getPayload().getNoteDelete()) {
					this.noteRepository.deleteById(id);
				}
			}
			if (change.getPayload().getApiaryUpdate() != null) {
				for (BmApiaryUpdated apiaryUpdate: change.getPayload().getApiaryUpdate()) {
					this.apiaryRepository.save(this.bmToMellispheraData.getNewApiary(apiaryUpdate.getUpdatedData(), username, countryCode, true));
				}
			}
			if (change.getPayload().getHiveUpdate() != null) {
				for (BmHiveUpdated hiveUpdated: change.getPayload().getHiveUpdate()) {
					Hive oldHive = this.hiveRepository.findById(hiveUpdated.getOldData().getHiveId()).get();
					this.hiveRepository.save(this.bmToMellispheraData.updateHiveChangeLog(oldHive, hiveUpdated.getUpdatedData()));
				}
			}
			if (change.getPayload().getDeviceUpdate() != null) {
				for (BmSensorUpdated sensorUpdated: change.getPayload().getDeviceUpdate()) {
					this.sensorRepository.save(this.bmToMellispheraData.getNewSensorFromChangeLog(sensorUpdated.getUpdatedData(), userId));
				}
			}
			if (change.getPayload().getNoteUpdate() != null) {
				for (BmNoteUpdated noteUpdated: change.getPayload().getNoteUpdate()) {
					this.noteRepository.save(this.bmToMellispheraData.getNewNote(noteUpdated.getUpdatedData(), userId));
				}
			}
			this.deleteChangeLog(change.getPayload().getModified(), change.getPayload().getUserId());
		}catch (NullPointerException e) {
			e.printStackTrace();
		}
	}


	
    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }
}
