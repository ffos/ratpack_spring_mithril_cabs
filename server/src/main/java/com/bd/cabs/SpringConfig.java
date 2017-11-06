package com.bd.cabs;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@EnableCaching
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.bd.cabs")
public class SpringConfig {
  private static final Logger logger = LoggerFactory.getLogger(SpringConfig.class);
  private static final String DEV_PROFILE = "dev";

  @Bean
  public ObjectMapper objectMapper(Environment env) {
    ObjectMapper om = new ObjectMapper()
        .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    if (env.acceptsProfiles(DEV_PROFILE)) {
      logger.info("Configuring object mapper for {} profile to indent output", DEV_PROFILE);
      om.enable(SerializationFeature.INDENT_OUTPUT);
    }
    return om;
  }

  @Bean
  public CacheManager cacheManager() {
    logger.info("Configuring cache manager");
    CaffeineCacheManager mgr = new CaffeineCacheManager();
    mgr.setCaffeine(
        Caffeine.newBuilder()
            .maximumSize(20_000)
            .expireAfterWrite(10, TimeUnit.MINUTES));
    return mgr;
  }
}
