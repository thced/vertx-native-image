package se.thced.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.auth.VertxContextPRNG;

public class PingPonger extends AbstractVerticle {

  private final Logger log = LoggerFactory.getLogger(PingPonger.class);

  private VertxContextPRNG prng;
  private MessageConsumer<String> consumer;

  @Override
  public void start(Promise<Void> startPromise) {
    prng = VertxContextPRNG.current(vertx);
    consumer = vertx.eventBus().consumer("ping.pong");
    consumer.completionHandler(startPromise);
    consumer.handler(this::doPingPong);
  }

  private void doPingPong(Message<String> message) {

    String s = message.body();

    String msg =
        String.format(
            "[%s] received a message from %s containing %s",
            deploymentID(), message.headers().get("ip"), s);

    log.info(msg);

    vertx.setTimer(
        1000L,
        id -> {
          String payload = prng.nextString(4);
          log.info("Sending..");
          vertx.eventBus().send("ping.pong", payload);
        });
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    consumer.unregister().onComplete(stopPromise);
  }
}
