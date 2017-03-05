import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class StepanVerticle extends AbstractVerticle {

    private static final String STEPAN = "Stepan";
    private static final Logger LOG = LoggerFactory.getLogger(StepanVerticle.class);

    @Override
    public void start() {
        String address = config().getString("address");
        LOG.info(STEPAN + " is ready to get messages on the address: " + address);
        vertx.eventBus().consumer(address, message -> {
            JsonObject messageBody = (JsonObject)message.body();
            LOG.info(STEPAN + " received message: " + messageBody);
            message.reply("Thanks for the message!");
            LOG.info(STEPAN + " sent reply");
        });
    }

}
