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

import com.mellisphera.entities.Apiary;
import com.mellisphera.entities.Hive;
import com.mellisphera.entities.Note;
import com.mellisphera.entities.Sensor;
import com.mellisphera.entities.bm.*;
import com.mellisphera.entities.bm.changeLog.BmHiveUpdated;
import com.mellisphera.repositories.ApiaryRepository;
import com.mellisphera.repositories.HivesRepository;
import com.mellisphera.repositories.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class BmDataToMellispheraData {

    @Autowired private SensorRepository sensorRepository;
    @Autowired private HivesRepository hiveRepository;
    @Autowired private ApiaryRepository apiaryRepository;
    private static int xPos = 0;
    private static int yPos = 15;
    private static  String PREFIX_BACKGROUND_DIRECTORY = "./assets/imageClient/";
    private static final String[] BACKGROUND_APIARY_EN = {
            PREFIX_BACKGROUND_DIRECTORY + "apiary_picture_default.png",
            PREFIX_BACKGROUND_DIRECTORY + "apiary_picture_default_EN_BLUE.png",
            PREFIX_BACKGROUND_DIRECTORY + "apiary_picture_default_EN_FUSCHIA.png",
            PREFIX_BACKGROUND_DIRECTORY + "apiary_picture_default_EN_GREEN.png",
            PREFIX_BACKGROUND_DIRECTORY + "apiary_picture_default_EN_RED.png"
    };

    private static final String[] BACKGROUND_APIARY_FR = {
            PREFIX_BACKGROUND_DIRECTORY + "apiary_picture_default_FR.png",
            PREFIX_BACKGROUND_DIRECTORY + "apiary_picture_default_FR_BLUE.png",
            PREFIX_BACKGROUND_DIRECTORY + "apiary_picture_default_FR_FUSCHIA.png",
            PREFIX_BACKGROUND_DIRECTORY + "apiary_picture_default_FR_GREEN.png",
            PREFIX_BACKGROUND_DIRECTORY + "apiary_picture_default_FR_RED.png"
    };
    public BmDataToMellispheraData() {
    }

    Note getNewNote(BmNote bmNote, String userId) {
        byte[] byteStr = bmNote.getDescription().getBytes();
        System.out.println(bmNote.getDescription());
        Note newNote =  new Note(bmNote.getNoteId(),
                this.convertTimestampToDate(bmNote.getCreateDate()),
                bmNote.getType(),
                bmNote.getTags(),
                "",
                bmNote.getHiveId(),
                bmNote.getApiaryId(),
                this.checkObsHiveOrApiary(bmNote),
                this.convertTimestampToDate(bmNote.getOpsDate()),
                userId);
        try{
            newNote.setDescription(new String(byteStr, "UTF-8").replaceAll ("<.*?>", ""));
        }
        catch (UnsupportedEncodingException e) {
            System.out.println("erreur encoding");
            newNote.setDescription(bmNote.getDescription().replaceAll ("<.*?>", ""));
        }
        return newNote;
    }

    Hive getNewHive(BmHive bmHive, String username, String userId) {
        Hive newHive = new Hive();
        newHive.set_id(bmHive.getHiveId());
       if (xPos >= 90) {
           yPos += 25;
           xPos = 0;
       }
        newHive.setHivePosY(yPos);
        newHive.setHivePosX(xPos);
        newHive.setApiaryId(bmHive.getApiaryId());
        newHive.setUserId(userId);
        newHive.setCreateDate(this.convertTimestampToDate(bmHive.getCreateDate()));
        newHive.setHidden(bmHive.getHidden());
        newHive.setDataLastReceived(this.convertTimestampToDate(bmHive.getDataLastReceived()));
        newHive.setName(bmHive.getName());
        newHive.setUsername(username);
        newHive.setName(bmHive.getName());
        xPos += 12;
        return newHive;
    }

    public void resetPos() {
        yPos = 15;
        xPos = 0;
    }
    private String checkObsHiveOrApiary(BmNote note) {
        if (note.getHiveId() != null) {
            return "HiveObs";
        } else {
            return "ApiaryObs";
        }
    }

    public Hive updateHiveChangeLog(Hive hive, BmHive bmHive) {
        hive.setName(bmHive.getName());
        hive.setApiaryId(bmHive.getApiaryId());
        hive.setHidden(bmHive.getHidden());

        return hive;
    }

    Sensor getNewSensorFromFirstConnection(BmSensor bmSensor, String userId, BmHive bmHive) {
        Sensor sensor = new Sensor();
        sensor.set_id(bmSensor.getDevice().getDeviceId());
        sensor.setHiveId(bmHive.getHiveId());
        sensor.setCreateDate(this.convertTimestampToDate(bmSensor.getDevice().getCreateDate()));
        sensor.setDataLastReceived(this.convertTimestampToDate(bmSensor.getDevice().getDataLastReceived()));
        sensor.setSensorRef(bmSensor.getDevice().getDeviceAddress());
        sensor.setModel(bmSensor.getDevice().getModel());
        sensor.setName(bmSensor.getDevice().getName());
        sensor.setUserId(userId);
        sensor.setDeviceLocation(
                new DeviceLocation(bmSensor.getDeviceLocationId(),
                bmSensor.getDevice().getDeviceId(),
                bmHive.getHiveId(),
                bmSensor.getHivePositionId(),
                bmSensor.getStart()));
        sensor.setApiaryId(bmHive.getApiaryId());
        sensor.setType(this.getTypeByRef(bmSensor.getDevice().getDeviceAddress()));

        return sensor;
    }

    Sensor getNewSensorFromChangeLog(BmDevice bmDevice, String userId) {
        Hive hive = null;
        Sensor sensor = new Sensor();
        sensor.set_id(bmDevice.getDeviceId());
        try {
            if (bmDevice.getCurrentLocation() != null) {
                hive = this.hiveRepository.findById(bmDevice.getCurrentLocation().getHiveId()).get();
                sensor.setHiveId(hive.get_id());
                sensor.setApiaryId(hive.getApiaryId());
                sensor.setDeviceLocation(
                        new DeviceLocation(bmDevice.getCurrentLocation().getDeviceLocationId(),
                                bmDevice.getDeviceId(),
                                hive.get_id(),
                                bmDevice.getCurrentLocation().getHivePositionId(),
                                bmDevice.getCurrentLocation().getStart()));
                System.out.println(sensor);
            }
            else{
                sensor.setHiveId(null);
                sensor.setApiaryId(null);
            }
        } catch (NullPointerException e) {
            try {
                Sensor lastSensor = this.sensorRepository.findById(bmDevice.getDeviceId()).get();
                hive = this.hiveRepository.findById(lastSensor.getHiveId()).get();
                sensor.setHiveId(hive.get_id());
                sensor.setApiaryId(hive.getApiaryId());
            }
            catch (NoSuchElementException except) {
                sensor.setHiveId(null);
                sensor.setApiaryId(null);
            }
            catch (IllegalArgumentException ex) {
                sensor.setHiveId(null);
                sensor.setApiaryId(null);
                System.out.println(sensor);
            }
        }
        sensor.setCreateDate(this.convertTimestampToDate(bmDevice.getCreateDate()));
        sensor.setDataLastReceived(this.convertTimestampToDate(bmDevice.getDataLastReceived()));
        sensor.setSensorRef(bmDevice.getDeviceAddress());
        sensor.setModel(bmDevice.getModel());
        sensor.setName(bmDevice.getName());
        sensor.setUserId(userId);
        sensor.setType(this.getTypeByRef(bmDevice.getDeviceAddress()));
        return sensor;
    }

    private String getTypeByRef(String ref) {
        String prefix = ref.split(":")[0];
        if (prefix.equals("41") || prefix.equals("47")) {
            return "T2";
        } else if (prefix.equals("42")) {
            return "T_HR";
        } else if (prefix.equals("43")) {
            return "WEIGHT";
        } else {
            return "ALIEN";
        }
    }


    Apiary getNewApiary(BmApiary bmApiary, String username, String countryCode, Boolean update) {
        Apiary newApiary = new Apiary();
        newApiary.set_id(bmApiary.getApiaryId());
        newApiary.setZipCode(bmApiary.getZipCode());
        newApiary.setName(bmApiary.getName());
        newApiary.setUserId(bmApiary.getUserId());
        newApiary.setHidden(bmApiary.getHidden());
        newApiary.setCreateDate(this.convertTimestampToDate(bmApiary.getCreateDate()));
        newApiary.setDataLastReceived(this.convertTimestampToDate(bmApiary.getDataLastReceived()));
        newApiary.setPrivateApiary(bmApiary.getPrivateApiary());
        newApiary.setCountryCode(bmApiary.getCountryCode());
        newApiary.setUsername(username);
        if (!update) {
            String photos;
            if (countryCode.equals("FR")) {
                photos = BACKGROUND_APIARY_FR[this.getRandomValue(BACKGROUND_APIARY_FR.length)];
            } else {
                photos = BACKGROUND_APIARY_EN[this.getRandomValue(BACKGROUND_APIARY_EN.length)];
            }
            newApiary.setPhoto(photos);
        } else {
            try {
                Apiary oldApiary = this.apiaryRepository.findById(bmApiary.getApiaryId()).get();
                newApiary.setPhoto(oldApiary.getPhoto());
            } catch (NoSuchElementException e) {
                newApiary.setPhoto(BACKGROUND_APIARY_EN[this.getRandomValue((BACKGROUND_APIARY_EN.length))]);
                this.apiaryRepository.insert(newApiary);
            }
        }
        return newApiary;
    }

    private int getRandomValue(int max) {
        return new Random().nextInt(max);
    }
    private Date convertTimestampToDate(long time){
        return new Date(time*1000);
    }


}
