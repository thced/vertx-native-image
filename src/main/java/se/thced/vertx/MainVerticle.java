package se.thced.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {

    vertx.setTimer(3000L, id -> vertx.eventBus().send("ping.pong", "hello"));
    vertx
        .deployVerticle(new PingPonger())
        .<Void>mapEmpty()
        .onComplete(startPromise);
  }
}
