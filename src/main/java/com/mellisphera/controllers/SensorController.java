package com.mellisphera.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mellisphera.entities.Apiary;
import com.mellisphera.entities.Hive;
import com.mellisphera.entities.Sensor;
import com.mellisphera.entities.User;
import com.mellisphera.repositories.ApiaryRepository;
import com.mellisphera.repositories.HivesRepository;
import com.mellisphera.repositories.SensorRepository;

@RestController
@RequestMapping("/sensors")
public class SensorController {


	@Autowired private SensorRepository sensorRepository;
	@Autowired private HivesRepository hivesRepository;
	@Autowired private ApiaryRepository apiaryRepository;
	public SensorController() {
	}

	public SensorController(SensorRepository sensorRepository) {
		this.sensorRepository = sensorRepository;
		//this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	
	@RequestMapping(value = "", method = RequestMethod.POST, produces={"application/json"})
	public Sensor insert(@RequestBody Sensor sensor){
		if (sensor.getHiveId() != null) {
			Hive hive = this.hivesRepository.findById(sensor.getHiveId()).get();
			List<Sensor> sensorHive = this.sensorRepository.findSensorByHiveId(hive.get_id());
			this.hivesRepository.save(hive);
		}
		return this.sensorRepository.insert(sensor);
	}

    @PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/all")
	public List<Sensor> getAllSensors(){
		List<Sensor> sensors=this.sensorRepository.findAll();
		return sensors;
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") String id){
		try {
			Hive hive = this.hivesRepository.findById(this.sensorRepository.findById(id).get().getHiveId()).get();
			System.err.println(hive);
			this.hivesRepository.save(hive);
		}
		catch(NoSuchElementException e) {}
		catch(NullPointerException e) {}
		this.sensorRepository.deleteById(id);
	}

	@PutMapping
	public void update(@RequestBody Sensor sensor){
		this.sensorRepository.save(sensor);
	}

	@GetMapping(value="/check/{reference}")
	public Sensor checkSensor(@PathVariable String reference) {
		return this.sensorRepository.findSensorsBySensorRef(reference);
	}
	@RequestMapping(value = "/{username}", method = RequestMethod.GET, produces={"application/json"})
	public List<Sensor> getUserSensors(@PathVariable String username){
		// liste les capteurs pour un user
		return this.sensorRepository.findSensorByUsername(username);
	}
	
	private Boolean checkIfHiveHaveSensor(Hive hive) {
		return !this.sensorRepository.findSensorByHiveId(hive.get_id()).isEmpty();
	}
}
