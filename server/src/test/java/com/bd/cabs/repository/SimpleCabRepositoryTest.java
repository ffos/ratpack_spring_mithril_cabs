package com.bd.cabs.repository;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.bd.cabs.SpringConfig;
import java.util.Date;
import java.util.UUID;
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
public class SimpleCabRepositoryTest {
  @Autowired
  private SimpleCabRepository repository;

  @Autowired
  private CacheManager cacheManager;

  @Before
  public void init() {
    getCache().clear();
  }

  private Cache getCache() {
    return cacheManager.getCache(SimpleCabRepository.CACHE_NAME_TRIP_COUNTS);
  }

  @Test
  public void sanityCheckExpectingZeroForNonExistingData() {
    Integer retVal = repository.getCountByMedallionAndPickupDatetime(UUID.randomUUID().toString(), new Date());
    assertThat(retVal, equalTo(0));
  }

  @Test
  public void cachePutOccursOnMethodCall() {
    String m = UUID.randomUUID().toString();
    Date d = new Date();
    repository.getCountByMedallionAndPickupDatetime(m, d);
    SimpleKey key = new SimpleKey(m, d);
    Integer cachedValue = getCache().get(key, Integer.class);
    assertThat(cachedValue, equalTo(0));
  }
}
