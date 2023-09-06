package com.redhat.appeng.escalation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ApplicationScoped
public class TimerService {
    private final static Logger logger = LoggerFactory.getLogger(TimerService.class);
    private ObjectMapper mapper = new ObjectMapper();

    public JsonNode initTimer(JsonNode workflowData) {
        logger.info("Called initTimer with {}", workflowData);
        Timer timer = new Timer();
        timer.creationMillis = System.currentTimeMillis();
        timer.elapsedSeconds = 0;
        timer.triggered = false;
        ((ObjectNode) workflowData).set("timer", mapper.valueToTree(timer));

        logger.info("Returning from initTimer with {}", workflowData);
        return workflowData;
    }

    public JsonNode updateTimer(JsonNode workflowData) {
        logger.info("Called updateTimer with {}", workflowData);
        try {
            Timer timer = mapper.treeToValue(((ObjectNode) workflowData).get("timer"), Timer.class);
            timer.elapsedSeconds = (System.currentTimeMillis() - timer.creationMillis) / 1000;
            logger.info("Updating elapsedSeconds to {}", timer.elapsedSeconds);

            ((ObjectNode) workflowData).set("timer", mapper.valueToTree(timer));
        } catch (JsonProcessingException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        logger.info("Returning from updateTimer with {}", workflowData);
        return workflowData;
    }

    public JsonNode stopTimer(JsonNode workflowData) {
        logger.info("Called stopTimer with {}", workflowData);
        try {
            Timer timer = mapper.treeToValue(((ObjectNode) workflowData).get("timer"), Timer.class);
            timer.triggered = true;

            ((ObjectNode) workflowData).set("timer", mapper.valueToTree(timer));
        } catch (JsonProcessingException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        logger.info("Returning from stopTimer with {}", workflowData);
        return workflowData;
    }
}
