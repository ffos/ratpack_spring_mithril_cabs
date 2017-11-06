package com.bd.cabs.models;

import java.io.Serializable;
import java.util.Objects;

public class TripsByDate implements Serializable {
  private String medallion;
  private Integer trips;

  public TripsByDate() {
  }

  public TripsByDate(String medallion, Integer trips) {
    this.medallion = medallion;
    this.trips = trips;
  }

  public String getMedallion() {
    return medallion;
  }

  public void setMedallion(String medallion) {
    this.medallion = medallion;
  }

  public Integer getTrips() {
    return trips;
  }

  public void setTrips(Integer trips) {
    this.trips = trips;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TripsByDate that = (TripsByDate) o;
    return Objects.equals(medallion, that.medallion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(medallion);
  }
}
