import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class RedisVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(RedisVerticle.class);

    @Override
    public void start() {
        RedisClient redis = RedisClient.create(vertx, new RedisOptions());
        String address = config().getString("address");

        LOG.info("Redis is ready to get messages on the address: " + address);
        vertx.eventBus().consumer(address, message -> {
            JsonObject messageBody = (JsonObject) message.body();
            LOG.info("Redis received message: " + messageBody);
            redis.hmset("key:" + messageBody.getInteger("counter"), messageBody, res -> {
                if (res.succeeded()) {
                    System.out.println("Value stored");
                } else {
                    System.out.println(res.cause().getMessage());
                }
            });
        });
    }
}
