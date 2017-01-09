package naga.fx.sun.stage;

import naga.fx.stage.Window;
import naga.fx.sun.event.BasicEventDispatcher;
import naga.fx.sun.event.CompositeEventDispatcher;
import naga.fx.sun.event.EventHandlerManager;
import naga.fx.sun.event.EventRedirector;

/**
 * An {@code EventDispatcher} for {@code Window}. It is formed by a chain
 * in which a received event is first passed through {@code EventRedirector}
 * and then through {@code EventHandlerManager}.
 */
public class WindowEventDispatcher extends CompositeEventDispatcher {
    private final EventRedirector eventRedirector;

    private final WindowCloseRequestHandler windowCloseRequestHandler;

    private final EventHandlerManager eventHandlerManager;

    public WindowEventDispatcher(final Window window) {
        this(new EventRedirector(window),
                new WindowCloseRequestHandler(window),
                new EventHandlerManager(window));

    }

    public WindowEventDispatcher(
            final EventRedirector eventRedirector,
            final WindowCloseRequestHandler windowCloseRequestHandler,
            final EventHandlerManager eventHandlerManager) {
        this.eventRedirector = eventRedirector;
        this.windowCloseRequestHandler = windowCloseRequestHandler;
        this.eventHandlerManager = eventHandlerManager;

        eventRedirector.insertNextDispatcher(windowCloseRequestHandler);
        windowCloseRequestHandler.insertNextDispatcher(eventHandlerManager);
    }

    public final EventRedirector getEventRedirector() {
        return eventRedirector;
    }

    public final WindowCloseRequestHandler getWindowCloseRequestHandler() {
        return windowCloseRequestHandler;
    }

    public final EventHandlerManager getEventHandlerManager() {
        return eventHandlerManager;
    }

    @Override
    public BasicEventDispatcher getFirstDispatcher() {
        return eventRedirector;
    }

    @Override
    public BasicEventDispatcher getLastDispatcher() {
        return eventHandlerManager;
    }
}

