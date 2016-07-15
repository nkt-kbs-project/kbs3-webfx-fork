package naga.providers.platform.server.vertx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import naga.providers.platform.server.vertx.util.VertxRunner;

/**
 * @author Bruno Salmon
 */
public class WebVerticle extends AbstractVerticle {

    // Convenient method to run the microservice directly in the IDE
    public static void main(String[] args) {
        VertxRunner.runVerticle(WebVerticle.class);
    }

    @Override
    public void start() throws Exception {
        //createHttpServer(80);   // standard web port
        createHttpServer(9090); // (temporary): alternative secondary access in case the web port is already bound
    }

    private void createHttpServer(int port) {
        // Creating web server and its router
        HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setCompressionSupported(true) // enabling gzip and deflate compression
                .setPort(port); // web port
        HttpServer server = vertx.createHttpServer(httpServerOptions);
        Router router = Router.router(vertx);

        // Logging web requests
        router.route("/*").handler(LoggerHandler.create());

        // SockJS event bus bridge
        router.route("/eventbus/*").handler(SockJSHandler.create(vertx)
                .bridge(new BridgeOptions()
                        .addInboundPermitted(new PermittedOptions(new JsonObject()))
                        .addOutboundPermitted(new PermittedOptions(new JsonObject()))
                        // Uncomment to watch events on the bridge , event -> System.out.println(event.type())
                )
        );

        // Serving static files under the webroot folder
        router.route("/*").handler(StaticHandler.create());

        // Binding the web port
        server.requestHandler(router::accept).listen();
    }
}
