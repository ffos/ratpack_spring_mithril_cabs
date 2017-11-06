package com.bd.cabs.handler;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.stereotype.Component;
import ratpack.handling.Context;
import ratpack.http.MediaType;

@Component
public class ResponseUtils {

  public void sendBadRequest(Context ctx, String message) {
    ctx.getResponse()
        .status(HttpResponseStatus.BAD_REQUEST.code())
        .send(message);
  }

  public void sendNoContent(Context ctx) {
    ctx.getResponse()
        .status(HttpResponseStatus.NO_CONTENT.code())
        .send();
  }

  public void sendJson(Context ctx, String json) {
    ctx.getResponse()
        .beforeSend(r -> r.getHeaders().add(HttpHeaderNames.CONTENT_TYPE, MediaType.APPLICATION_JSON))
        .send(json);
  }

}
