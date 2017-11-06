package com.bd.cabs;

import com.bd.cabs.handler.ClearCache;
import com.bd.cabs.handler.GetTripCountsOnDate;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import ratpack.error.ClientErrorHandler;
import ratpack.error.ServerErrorHandler;
import ratpack.registry.Registry;
import ratpack.server.RatpackServer;
import ratpack.spring.Spring;

public class Server {
  public static final String PATH_TOKEN_DATE = "pickupEpochMillis";
  public static final String PATH_TOKEN_CAB = "medallions";
  public static final String PATH_BASE = "api/cabs/trip_counts";
  public static final String PATH_TRIP_COUNTS_FORMAT = "%s/cab/%s";
  public static final String RATPACK_PATH_CLEAR_CACHE = "cache";
  public static final String RATPACK_PATH_TRIP_COUNTS = String.format(PATH_TRIP_COUNTS_FORMAT,
      ":" + PATH_TOKEN_DATE,
      ":" + PATH_TOKEN_CAB);
  private static final Logger logger = LoggerFactory.getLogger(Server.class);

  private static final ServerErrorHandler serverErrorHandler = (context, throwable) -> {
    logger.warn("ServerError: req {}", context.getRequest().getRawUri(), throwable);
    context.getResponse().status(500).send();
  };

  private static final ClientErrorHandler clientErrorHandler = (context, statusCode) -> {
    logger.info("ClientError: status {}, req: {}", statusCode, context.getRequest().getRawUri());
    context.getResponse().status(statusCode).send();
  };

  public static void main(String[] args) throws Exception {
    final Registry spring = Spring.spring(new SpringApplicationBuilder(SpringConfig.class));
    final RatpackServer server = RatpackServer.of(spec ->
        spec.registry(spring
            .join(Registry.builder()
                .add(serverErrorHandler)
                .add(clientErrorHandler)
                .build()))
            .serverConfig(cfg -> cfg.port(8999))
            .handlers(chain ->
                chain.prefix(PATH_BASE, cabApi -> cabApi
                    .all(ctx -> {
                      ctx.getResponse().getHeaders().add("Access-Control-Allow-Origin", "*")
                          .add("'Access-Control-Allow-Methods'", "GET,DELETE,OPTIONS,HEAD")
                          .add("'Access-Control-Allow-Headers'", "Content-Type,X-Requested-With");
                      ctx.next();
                    })
                    .get(RATPACK_PATH_TRIP_COUNTS, GetTripCountsOnDate.class)
                    .delete(RATPACK_PATH_CLEAR_CACHE, ClearCache.class)
                )
            )
    );
    server.start();
    logger.info("Server Started with paths: {}", Arrays.asList(RATPACK_PATH_TRIP_COUNTS, RATPACK_PATH_CLEAR_CACHE));
  }

}
