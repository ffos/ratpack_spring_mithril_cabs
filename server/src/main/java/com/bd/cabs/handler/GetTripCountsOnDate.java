package com.bd.cabs.handler;

import com.bd.cabs.Server;
import com.bd.cabs.models.TripsByDate;
import com.bd.cabs.service.SimpleCabService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

@Component
public class GetTripCountsOnDate implements Handler {
  public static final String IGNORE_CACHE_FLAG = "ignoreCache";

  @Override
  public void handle(Context ctx) throws Exception {
    final ResponseUtils responseUtils = ctx.get(ResponseUtils.class);
    final ParseUtils parseUtils = ctx.get(ParseUtils.class);

    final List<String> medallions = parseUtils.readCsv(ctx.getPathTokens().get(Server.PATH_TOKEN_CAB));
    if (medallions.isEmpty()) {
      responseUtils.sendBadRequest(ctx, "empty medallions");
      return;
    }
    final Optional<Date> pickupDate = parseUtils.readAsDate(ctx.getPathTokens().get(Server.PATH_TOKEN_DATE));
    if (!pickupDate.isPresent()) {
      responseUtils.sendBadRequest(ctx, "invalid pickup date");
      return;
    }

    final SimpleCabService cabService = ctx.get(SimpleCabService.class);
    final JsonUtils jsonUtils = ctx.get(JsonUtils.class);

    boolean ignoreCache = ctx.getRequest().getQueryParams().containsKey(IGNORE_CACHE_FLAG);
    Supplier<List<TripsByDate>> serviceResult = () -> {
      if (ignoreCache) {
        return cabService.getMedallionsTripsByDateIgnoringCache(medallions, pickupDate.get());
      } else {
        return cabService.getMedallionsTripsByDateFromCache(medallions, pickupDate.get());
      }
    };
    Blocking.get(() -> serviceResult.get())
        .blockingMap(jsonUtils::toJson)
        .then(json -> responseUtils.sendJson(ctx, json));
  }
}
