import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class RedisTest {
    Vertx vertx;
    String address;
    String host;

    @Before
    public void before(TestContext context) {
        address = "some-address";
        host = "localhost";

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("address", address).put("redis-host", host));

        vertx = Vertx.vertx();

        vertx.deployVerticle(RedisVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void talkToRedis(TestContext context) {
        vertx.eventBus().publish(address, new JsonObject()
                .put("content", "Hello there!")
                .put("counter", 1));

        RedisClient redis = RedisClient.create(vertx, new RedisOptions().setHost(host));
        Async async = context.async();

        redis.hgetall("key:1", asyncResult -> {
            if (asyncResult.succeeded()) {
                if (asyncResult.succeeded()) {
                    context.assertEquals("Hello there!",
                            asyncResult.result().getString("content"));
                } else {
                    System.out.println(asyncResult.cause().getMessage());
                }
                async.complete();
            }
        });
    }
}


