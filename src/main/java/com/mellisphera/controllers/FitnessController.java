package com.mellisphera.controllers;

import com.mellisphera.entities.Fitness;
import com.mellisphera.repositories.FitnessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/fitness")
public class FitnessController {

    @Autowired private FitnessRepository fitnessRepository;
    private MongoTemplate mongoTemplate;

    public FitnessController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/daily/{userId}/{start}/{end}")
    public List<Fitness> getDailyFitnessByUserId(@PathVariable String userId, @PathVariable long start, @PathVariable long end){
        return this.fitnessRepository.findByUserIdAndDateBetween(userId, new Date(start), new Date(end));
    }

    @GetMapping("hive/{hiveId}/{start}/{end}")
    public List<Fitness> getFitnessByHive(@PathVariable String hiveId, @PathVariable long start, @PathVariable long end) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("date").gte(new Date(start)).lt(new Date(end))),
                Aggregation.match(Criteria.where("hiveId").is(hiveId))
        );
        return this.mongoTemplate.aggregate(aggregation, "Fitness", Fitness.class).getMappedResults();
    }
}
