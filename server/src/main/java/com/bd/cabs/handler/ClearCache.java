package com.bd.cabs.handler;

import com.bd.cabs.service.SimpleCabService;
import org.springframework.stereotype.Component;
import ratpack.exec.Blocking;
import ratpack.handling.Context;
import ratpack.handling.Handler;

@Component
public class ClearCache implements Handler {

  @Override
  public void handle(Context ctx) throws Exception {
    final SimpleCabService cabService = ctx.get(SimpleCabService.class);
    final ResponseUtils responseUtils = ctx.get(ResponseUtils.class);
    Blocking.get(() -> {
      cabService.clearMedallionsTripsByDateCache();
      return null;
    }).then(x -> responseUtils.sendNoContent(ctx));
  }
}
