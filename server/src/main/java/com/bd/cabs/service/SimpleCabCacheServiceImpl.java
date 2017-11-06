package com.bd.cabs.service;

import com.bd.cabs.repository.SimpleCabRepository;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SimpleCabCacheServiceImpl implements SimpleCabCacheService {
  private static final Logger logger = LoggerFactory.getLogger(SimpleCabServiceImpl.class);

  private final SimpleCabRepository repository;
  private final CacheManager cacheManager;

  public SimpleCabCacheServiceImpl(SimpleCabRepository repository, CacheManager cacheManager) {
    this.repository = repository;
    this.cacheManager = cacheManager;
  }

  @Override
  @Cacheable(SimpleCabRepository.CACHE_NAME_TRIP_COUNTS)
  public Integer getSingleMedallionTripsByDate(String medallion, Date pickupDate) {
    logger.info("Cache Miss:  trip counts for {} on {}", medallion, pickupDate);
    return repository.getCountByMedallionAndPickupDatetime(medallion, pickupDate);
  }

  @Override
  public void clearCache() {
    cacheManager.getCache(SimpleCabRepository.CACHE_NAME_TRIP_COUNTS).clear();
  }
}
