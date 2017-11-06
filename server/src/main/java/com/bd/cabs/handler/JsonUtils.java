package com.bd.cabs.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonUtils {
  private final ObjectMapper om;

  public JsonUtils(ObjectMapper om) {
    this.om = om;
  }

  public String toJson(Object o) {
    if (o == null) {
      return null;
    }
    try {
      return om.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e.getCause());
    }
  }
}
