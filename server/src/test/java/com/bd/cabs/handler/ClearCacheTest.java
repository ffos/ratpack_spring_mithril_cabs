package com.bd.cabs.handler;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bd.cabs.service.SimpleCabService;
import com.bd.cabs.service.SimpleCabServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ratpack.func.Action;
import ratpack.http.client.ReceivedResponse;
import ratpack.registry.RegistrySpec;
import ratpack.test.embed.EmbeddedApp;


public class ClearCacheTest {
  private ResponseUtils responseUtils = new ResponseUtils();
  private ClearCache handler;
  private Action<RegistrySpec> registry;
  private SimpleCabService cabService;

  @Before
  public void before() throws Exception {
    cabService = Mockito.spy(new SimpleCabServiceImpl(null, null));
    registry = (RegistrySpec rs) -> rs
        .add(responseUtils)
        .add(cabService);
    handler = new ClearCache();
    doNothing().when(cabService).clearMedallionsTripsByDateCache();
  }

  private EmbeddedApp startApp() throws Exception {
    return EmbeddedApp.of(spec -> spec.registryOf(registry)
        .handlers(chain -> chain.delete("clearCache", handler)));
  }

  @Test
  public void cacheClearIsCalled() throws Exception {
    try (EmbeddedApp app = startApp()) {
      app.test(c -> {
        ReceivedResponse resp = c.delete("clearCache");
        assertThat(resp.getStatus().getCode(), equalTo(204));
        verify(cabService, times(1)).clearMedallionsTripsByDateCache();
      });
    }
  }
}
