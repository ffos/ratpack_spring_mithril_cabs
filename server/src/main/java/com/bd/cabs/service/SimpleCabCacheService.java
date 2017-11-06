package com.bd.cabs.service;

import java.util.Date;

public interface SimpleCabCacheService {
  Integer getSingleMedallionTripsByDate(String medallion, Date pickupDate);

  void clearCache();
}
