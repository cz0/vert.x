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
        String address = config().getString("address");
        String host = config().getString("redis-host");

        RedisClient redis = RedisClient.create(vertx, new RedisOptions().setHost(host));

        LOG.info("Redis is ready to get messages on the address: " + address);
        vertx.eventBus().consumer(address, message -> {
            JsonObject messageBody = (JsonObject) message.body();
            LOG.info("Redis received message: " + messageBody);
            redis.hmset("key:" + messageBody.getInteger("counter"), messageBody, res -> {
                if (res.succeeded()) {
                    LOG.info("Value stored");
                } else {
                    LOG.info(res.cause().getMessage());
                }
            });
        });
    }
}
