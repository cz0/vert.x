import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.util.Arrays;

public class WebVerticle extends AbstractVerticle {

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.route("/").handler(this::handleRoot);
        router.route("/messages/*").handler(this::handleMessages);

        router.route().handler(StaticHandler.create("assets"));

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);
    }

    private void handleRoot(RoutingContext ctx) {
        ctx.response()
            .putHeader("content-type", "text/html")
            .end("<h1>Hello there!</h1>");
    }

    private void handleMessages(RoutingContext ctx) {
        String address = config().getString("address");
        PermittedOptions permittedOpts = new PermittedOptions().setAddress(address);

        BridgeOptions bridgeOpts = new BridgeOptions();
        bridgeOpts.setOutboundPermitted(Arrays.asList(permittedOpts));

        SockJSHandler.create(vertx).bridge(bridgeOpts).handle(ctx);
    }
}
