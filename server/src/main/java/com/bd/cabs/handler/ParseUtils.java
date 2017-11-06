package com.bd.cabs.handler;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

@Component
public class ParseUtils {

  public Optional<String> readNonEmptyText(String input) {
    return Optional.ofNullable(input)
        .filter(StringUtils::hasText)
        .map(String::trim);
  }

  public List<String> readCsv(String input) {
    return readNonEmptyText(input)
        .map(s -> s.split(","))
        .map(Arrays::asList)
        .map(l -> l.stream()
            .map(this::readNonEmptyText)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList()))
        .orElse(Collections.emptyList());
  }

  public Optional<Date> readAsDate(String input) {
    return Optional.ofNullable(input)
        .filter(StringUtils::hasText)
        .flatMap(this::readAsLong)
        .filter(n -> n > 0)
        .map(Date::new);
  }

  public Optional<Long> readAsLong(String input) {
    return readAsNumber(input, Long.class);
  }

  private <T extends Number> Optional<T> readAsNumber(String input, Class<T> outputType) {
    try {
      return Optional.ofNullable(NumberUtils.parseNumber(input, outputType));
    } catch (NumberFormatException nfe) {
      return Optional.empty();
    }
  }


}
