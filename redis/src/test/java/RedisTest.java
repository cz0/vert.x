import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.unit.junit.VertxUnitRunnerWithParametersFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(VertxUnitRunnerWithParametersFactory.class)
public class RedisTest {
    private Vertx vertx;
    private String address;
    private String host;
    private final String targetCountry;
    private RedisClient redis;

    @Parameterized.Parameters
    public static Iterable<String> targetCountries() {
        return Arrays.asList("Czech Republic", "Russia", "Ukraine");
    }

    public RedisTest(String targetCountry) {
        this.targetCountry = targetCountry;
    }

    @Before
    public void before(TestContext context) {
        address = "some-address";
        host = "localhost";

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("address", address).put("redis-host", host));

        vertx = Vertx.vertx();

        vertx.deployVerticle(RedisVerticle.class.getName(), options, context.asyncAssertSuccess());

        redis = RedisClient.create(vertx, new RedisOptions().setHost(host));
       // redis.flushall(context.asyncAssertSuccess());
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void talkToRedis(TestContext context) {
        vertx.eventBus().publish(address, new JsonObject()
                .put("content", "Hello, " + targetCountry)
                .put("counter", targetCountry.length()));

        Async async = context.async();

        redis.hgetall("key:" + targetCountry.length(), asyncResult -> {
            if (asyncResult.succeeded()) {
                if (asyncResult.succeeded()) {
                    context.assertEquals("Hello, " + targetCountry,
                            asyncResult.result().getString("content"));
                } else {
                    System.out.println(asyncResult.cause().getMessage());
                }
                async.complete();
            }
        });
    }
}


