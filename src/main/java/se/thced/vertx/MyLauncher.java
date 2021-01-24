package se.thced.vertx;

import io.vertx.core.Launcher;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MyLauncher extends Launcher {

  private final Logger log = LoggerFactory.getLogger(MyLauncher.class);

  public static void main(String[] args) {
    new MyLauncher().dispatch(args);
  }

  public MyLauncher() {
    super();
  }

  @Override
  public void beforeStartingVertx(VertxOptions options) {
    log.info("The current PID " + ProcessHandle.current().pid());
  }

  @Override
  public void afterStartingVertx(Vertx vertx) {
    vertx
        .eventBus()
        .addOutboundInterceptor(
            ctx -> {
              MultiMap headers = ctx.message().headers();
              try {
                headers.add("ip", InetAddress.getLocalHost().getHostAddress());
              } catch (UnknownHostException e) {
                headers.add("ip", "error");
              } finally {
                ctx.next();
              }
            });

    vertx.deployVerticle(new MainVerticle());
  }
}
