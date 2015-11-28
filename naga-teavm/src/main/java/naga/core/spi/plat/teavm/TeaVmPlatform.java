package naga.core.spi.plat.teavm;

import naga.core.spi.json.JsonFactory;
import naga.core.spi.json.teavm.TeaVmJsonFactory;
import naga.core.spi.sock.WebSocketFactory;
import naga.core.spi.plat.Platform;
import naga.core.spi.plat.Scheduler;
import naga.core.spi.sock.teavm.TeaVmWebSocketFactory;

import java.util.logging.Logger;

/**
 * @author Bruno Salmon
 */
public final class TeaVmPlatform implements Platform {

    public static void register() {
        Platform.register(new TeaVmPlatform());
    }

    private final WebSocketFactory webSocketFactory = new TeaVmWebSocketFactory();
    private final JsonFactory jsonFactory = new TeaVmJsonFactory();
    private final Scheduler scheduler = new TeaVmScheduler();

    @Override
    public WebSocketFactory webSocketFactory() {
        return webSocketFactory;
    }

    @Override
    public Scheduler scheduler() {
        return scheduler;
    }

    @Override
    public JsonFactory jsonFactory() {
        return jsonFactory;
    }

    @Override
    public Logger logger() {
        return Logger.getAnonymousLogger();
    }

}
