package com.bd.cabs.service;

import com.bd.cabs.models.TripsByDate;
import com.bd.cabs.repository.SimpleCabRepository;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SimpleCabServiceImpl implements SimpleCabService {
  private static final Logger logger = LoggerFactory.getLogger(SimpleCabServiceImpl.class);

  private final SimpleCabRepository repository;
  private final SimpleCabCacheService cacheService;

  @Inject
  public SimpleCabServiceImpl(SimpleCabRepository repository, SimpleCabCacheService cacheService) {
    this.repository = repository;
    this.cacheService = cacheService;
  }

  @Override
  public void clearMedallionsTripsByDateCache() {
    cacheService.clearCache();
  }

  @Override
  public List<TripsByDate> getMedallionsTripsByDateFromCache(List<String> medallions, Date pickupDate) {
    Objects.requireNonNull(medallions);
    Objects.requireNonNull(pickupDate);
    return medallions.stream()
        .map(m -> new TripsByDate(m, cacheService.getSingleMedallionTripsByDate(m, pickupDate)))
        .collect(Collectors.toList());
  }

  @Override
  public List<TripsByDate> getMedallionsTripsByDateIgnoringCache(List<String> medallions, Date pickupDate) {
    Objects.requireNonNull(medallions);
    Objects.requireNonNull(pickupDate);
    return medallions.stream()
        .map(m -> new TripsByDate(m, this.getSingleMedallionTripsByDate(m, pickupDate)))
        .collect(Collectors.toList());
  }

  private Integer getSingleMedallionTripsByDate(String medallion, Date pickupDate) {
    return repository.getCountByMedallionAndPickupDatetime(medallion, pickupDate);
  }
}
