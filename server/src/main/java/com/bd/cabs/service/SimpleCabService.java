package com.bd.cabs.service;

import com.bd.cabs.models.TripsByDate;
import java.util.Date;
import java.util.List;

public interface SimpleCabService {
  void clearMedallionsTripsByDateCache();

  List<TripsByDate> getMedallionsTripsByDateFromCache(List<String> medallions, Date pickupDate);

  List<TripsByDate> getMedallionsTripsByDateIgnoringCache(List<String> medallions, Date pickupDate);
}
