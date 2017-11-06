package com.bd.cabs.repository;

import java.util.Date;

public interface SimpleCabRepository {
  String CACHE_NAME_TRIP_COUNTS = "tripCounts";

  Integer getCountByMedallionAndPickupDatetime(String medallionId, Date pickupDate);
}
