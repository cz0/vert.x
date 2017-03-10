import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeEventType;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class WebVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        System.out.println("Starting...");

        Router router = Router.router(vertx);

        BridgeOptions options = new BridgeOptions().addOutboundPermitted(new PermittedOptions().setAddress("some-nice-address"));

        router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(options));

        // Serve the static resources
        router.route().handler(StaticHandler.create());

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);

        // Publish a message to the address "news-feed" every second
        vertx.setPeriodic(5000, t -> {
                    vertx.eventBus().publish("some-nice-address", "news from the web verticle!");
                }
        );
    }
}
