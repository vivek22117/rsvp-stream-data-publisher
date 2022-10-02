package com.dd.position.simulator.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JsonUtility {

    private final ObjectMapper objectMapper;

    public JsonUtility() {
        this(new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
    }

    public JsonUtility(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> String convertToString(T object) throws JsonProcessingException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            log.error("Unable to convert into string.." + ex.getMessage());
        }
        return null;
    }

    public <T> T convertFromJson(String json, Class<T> type) throws IOException {
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException ex) {
            log.error("Unable to covert from json.." + ex.getMessage());
        }
        return null;
    }

    public <T> T convertCollectionFromJson(String json, Class<T> type) throws IOException {
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        try {
            return objectMapper.readValue(json, typeFactory.constructCollectionType(List.class, type));
        } catch (IOException ex) {
            log.error("Unable to convert into list..." + ex.getMessage());
        }
        return null;
    }
}
