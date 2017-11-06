package com.bd.cabs.service;

import static org.hamcrest.Matchers.equalTo;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringConfig.class)
public class SimpleCabServiceTest {
  private final Integer mockedCachedValue = 1;
  @Autowired
  private SimpleCabRepository repository;
  @Autowired
  private SimpleCabService cabService;
  @Autowired
  private CacheManager cacheManager;
  @MockBean
  private JdbcTemplate jdbcTemplate;

  @Before
  public void init() {
    getCache().clear();
    Mockito.doReturn(mockedCachedValue).when(jdbcTemplate)
        .queryForObject(Mockito.anyString(),
            Mockito.any(Object[].class),
            Mockito.any(int[].class),
            Mockito.eq(Integer.class));
  }

  private Cache getCache() {
    return cacheManager.getCache(SimpleCabRepository.CACHE_NAME_TRIP_COUNTS);
  }

  private Integer getCachedValue(String cab, Date date) {
    SimpleKey key = new SimpleKey(cab, date);
    return getCache().get(key, Integer.class);
  }

  @Test
  public void usesCacheInSubsequentRequests() {
    final String cab = "A";
    final Date date = new Date();
    final int expectedNumberOfDbCalls = 1;
    List<TripsByDate> firstCallResult = cabService.getMedallionsTripsByDateFromCache(Arrays.asList(cab), date);
    Mockito.verify(jdbcTemplate, Mockito.times(expectedNumberOfDbCalls)).queryForObject(Mockito.anyString(),
        Mockito.any(Object[].class),
        Mockito.any(),
        Mockito.eq(Integer.class));
    //calling again should not invoke db call
    List<TripsByDate> secondCallResult = cabService.getMedallionsTripsByDateFromCache(Arrays.asList(cab), date);
    Mockito.verify(jdbcTemplate, Mockito.times(expectedNumberOfDbCalls)).queryForObject(Mockito.anyString(),
        Mockito.any(Object[].class),
        Mockito.any(),
        Mockito.eq(Integer.class));
    assertThat(firstCallResult, equalTo(secondCallResult));
    assertThat(getCachedValue(cab, date), equalTo(mockedCachedValue));
  }

}
