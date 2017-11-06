package com.bd.cabs.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import com.bd.cabs.SpringConfig;
import com.bd.cabs.models.TripsByDate;
import com.bd.cabs.repository.SimpleCabRepository;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringConfig.class)
public class SimpleCabServiceIntegTest {
  @Autowired
  private SimpleCabRepository repository;

  @Autowired
  private SimpleCabService cabService;

  @Autowired
  private CacheManager cacheManager;

  @Before
  public void init() {
    getCache().clear();
  }

  private Cache getCache() {
    return cacheManager.getCache(SimpleCabRepository.CACHE_NAME_TRIP_COUNTS);
  }

  private Integer getCachedValue(String cab, Date date) {
    SimpleKey key = new SimpleKey(cab, date);
    return getCache().get(key, Integer.class);
  }

  @Test
  public void cacheClearOccurs() {
    String cab = "A";
    Date date = new Date();
    repository.getCountByMedallionAndPickupDatetime(cab, date);
    assertThat(getCachedValue(cab, date), notNullValue());
    cabService.clearMedallionsTripsByDateCache();
    assertThat(getCachedValue(cab, date), nullValue());
  }

  @Test
  public void getCachedValue() {
    String cab = "A";
    Date date = new Date();
    List<TripsByDate> firstCallResult = cabService.getMedallionsTripsByDateFromCache(Arrays.asList(cab), date);
    TripsByDate expected = new TripsByDate(cab, 0);
    assertThat(firstCallResult.get(0).getMedallion(), equalTo(expected.getMedallion()));
    assertThat(firstCallResult.get(0).getTrips(), equalTo(expected.getTrips()));
  }

  @Test
  public void getValueIgnoringCache() {
    String cab = "A";
    Date date = new Date();
    List<TripsByDate> firstCallResult = cabService.getMedallionsTripsByDateIgnoringCache(Arrays.asList(cab), date);
    TripsByDate expected = new TripsByDate(cab, 0);
    assertThat(firstCallResult.get(0).getMedallion(), equalTo(expected.getMedallion()));
    assertThat(firstCallResult.get(0).getTrips(), equalTo(expected.getTrips()));
  }

}
