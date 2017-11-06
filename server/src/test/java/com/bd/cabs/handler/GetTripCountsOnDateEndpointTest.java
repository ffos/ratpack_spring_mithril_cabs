package com.bd.cabs.handler;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bd.cabs.Server;
import com.bd.cabs.service.SimpleCabService;
import com.bd.cabs.service.SimpleCabServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ratpack.func.Action;
import ratpack.http.client.ReceivedResponse;
import ratpack.registry.RegistrySpec;
import ratpack.test.embed.EmbeddedApp;
import ratpack.test.handling.HandlingResult;
import ratpack.test.handling.RequestFixture;


public class GetTripCountsOnDateEndpointTest {
  private JsonUtils jsonUtils = new JsonUtils(new ObjectMapper());
  private ResponseUtils responseUtils = new ResponseUtils();
  private ParseUtils parseUtils = new ParseUtils();
  private GetTripCountsOnDate handler;
  private Action<RegistrySpec> registry;
  private SimpleCabService cabService;

  @Before
  public void before() throws Exception {
    cabService = Mockito.spy(new SimpleCabServiceImpl(null, null));
    registry = (RegistrySpec rs) -> rs
        .add(responseUtils)
        .add(parseUtils)
        .add(jsonUtils)
        .add(cabService);
    handler = new GetTripCountsOnDate();
    doReturn(Collections.emptyList()).when(cabService)
        .getMedallionsTripsByDateFromCache(any(List.class), any(Date.class));
    doReturn(Collections.emptyList()).when(cabService)
        .getMedallionsTripsByDateIgnoringCache(any(List.class), any(Date.class));
  }

  private EmbeddedApp startApp() throws Exception {
    return EmbeddedApp.of(spec -> spec.registryOf(registry)
        .handlers(chain -> chain.prefix(Server.PATH_BASE, cabApi -> cabApi.get(Server.RATPACK_PATH_TRIP_COUNTS, handler))));
  }

  @Test
  public void invalidDateReturns400() throws Exception {
    Map<String, String> pathTokens = new HashMap<>();
    pathTokens.put(Server.PATH_TOKEN_CAB, "a,b");
    pathTokens.put(Server.PATH_TOKEN_DATE, "0");
    HandlingResult result = RequestFixture.handle(handler, fixture -> fixture
        .pathBinding(pathTokens)
        .registry(registry));
    assertThat(result.getStatus().getCode(), equalTo(400));
    assertThat(result.getBodyText(), containsString("invalid pickup date"));
  }

  @Test
  public void serviceCallDoesNotIgnoreCache() throws Exception {
    try (EmbeddedApp app = startApp()) {
      app.test(c -> {
        ReceivedResponse resp = c.get(String.format(Server.PATH_BASE + Server.PATH_TRIP_COUNTS_FORMAT, "/1", "a,b"));
        assertThat(resp.getStatus().getCode(), equalTo(200));
        verify(cabService, times(0))
            .getMedallionsTripsByDateIgnoringCache(any(List.class), any(Date.class));
        verify(cabService, times(1))
            .getMedallionsTripsByDateFromCache(any(List.class), any(Date.class));
      });
    }
  }

  @Test
  public void serviceCallIgnoresCache() throws Exception {
    try (EmbeddedApp app = startApp()) {
      app.test(c -> {
        ReceivedResponse resp = c.requestSpec(r -> r.getHeaders()
            .add(GetTripCountsOnDate.IGNORE_CACHE_FLAG, "1"))
            .get(String.format(Server.PATH_BASE + Server.PATH_TRIP_COUNTS_FORMAT, "/1", "a,b?"+ GetTripCountsOnDate.IGNORE_CACHE_FLAG));
        assertThat(resp.getStatus().getCode(), equalTo(200));
        verify(cabService, times(1))
            .getMedallionsTripsByDateIgnoringCache(any(List.class), any(Date.class));
        verify(cabService, times(0))
            .getMedallionsTripsByDateFromCache(any(List.class), any(Date.class));
      });
    }
  }

}
