import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class BorisTest {

    Vertx vertx;
    String address;

    @Before
    public void before(TestContext context) {
        address = "some-address";

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("address", address));

        vertx = Vertx.vertx();

        vertx.deployVerticle(BorisVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void listenToBoris(TestContext context) {
        MessageConsumer<Message> messageConsumer = vertx.eventBus().consumer(address);

        Async async = context.async();

        messageConsumer.handler(message -> {
            JsonObject messageBody = (JsonObject)message.body();

            context.assertEquals("Boris", messageBody.getString("from"));
            context.assertNotEquals("Stepan", messageBody.getString("from"));

            context.assertEquals("Hello there!", messageBody.getString("content"));
            context.assertNotEquals("Hi there!", messageBody.getString("content"));

            context.assertEquals(1, messageBody.getInteger("counter"));
            context.assertNotEquals(2, messageBody.getInteger("counter"));

            async.complete();
        });
    }
}
