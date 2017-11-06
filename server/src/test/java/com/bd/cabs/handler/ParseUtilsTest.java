package com.bd.cabs.handler;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.Test;

public class ParseUtilsTest {
  private ParseUtils parseUtils = new ParseUtils();

  @Test
  public void extraCommasAreIgnoredInCsv() {
    String input = ",,,,a,,,,b,,";
    List<String> parsed = parseUtils.readCsv(input);
    assertThat(parsed, hasSize(2));
    assertThat(parsed, hasItems("a", "b"));
  }

  @Test
  public void numbersAreParsedToDate() {
    Date now = new Date();
    Optional<Date> parsed = parseUtils.readAsDate(String.valueOf(now.getTime()));
    assertThat(parsed, equalTo(Optional.of(now)));
  }

  @Test
  public void negativeAndZeroAreNotParsedToDate() {
    Optional<Date> neg = parseUtils.readAsDate("-1000");
    Optional<Date> zero = parseUtils.readAsDate("0");
    assertThat(neg, equalTo(Optional.empty()));
    assertThat(zero, equalTo(Optional.empty()));
  }
}
