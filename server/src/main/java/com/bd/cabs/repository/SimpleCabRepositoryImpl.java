package com.bd.cabs.repository;

import java.sql.Types;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SimpleCabRepositoryImpl implements SimpleCabRepository {
  private static final Logger logger = LoggerFactory.getLogger(SimpleCabRepositoryImpl.class);

  private final JdbcTemplate jdbcTemplate;

  @Inject
  public SimpleCabRepositoryImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  @CachePut(CACHE_NAME_TRIP_COUNTS)
  public Integer getCountByMedallionAndPickupDatetime(String medallionId, Date pickupDate) {
    logger.debug("getCountByMedallionAndPickupDatetime({}, {})", medallionId, pickupDate);
    final String countQuery = "select count(1) from cab_trip_data "
        + " where medallion = ? "
        + " and pickup_datetime >= ? and pickup_datetime < ? ";
    LocalDate localDate = pickupDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    java.sql.Date startRange = java.sql.Date.valueOf(localDate);
    java.sql.Date endRange = java.sql.Date.valueOf(localDate.plusDays(1));
    Object[] params = new Object[] {
        medallionId,
        startRange,
        endRange
    };
    int[] types = new int[] {
        Types.VARCHAR,
        Types.JAVA_OBJECT,
        Types.JAVA_OBJECT
    };
    logger.info("Executing SQL: {}, params: {}", countQuery, params);
    return jdbcTemplate.queryForObject(countQuery, params, types, Integer.class);
  }
}
