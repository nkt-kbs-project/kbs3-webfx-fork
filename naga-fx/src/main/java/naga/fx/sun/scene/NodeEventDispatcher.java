package naga.fx.sun.scene;

import naga.fx.sun.event.BasicEventDispatcher;
import naga.fx.sun.event.CompositeEventDispatcher;
import naga.fx.sun.event.EventHandlerManager;

/**
 * An {@code EventDispatcher} for {@code Node}. It is formed by a chain
 * of {@code MouseEnteredExitedHandler} followed by {@code EventHandlerManager}.
 */
public class NodeEventDispatcher extends CompositeEventDispatcher {

    private final EnteredExitedHandler enteredExitedHandler;
    private final EventHandlerManager eventHandlerManager;

    public NodeEventDispatcher(final Object eventSource) {
        this(new EnteredExitedHandler(eventSource), new EventHandlerManager(eventSource));
    }

    public NodeEventDispatcher(EnteredExitedHandler enteredExitedHandler, EventHandlerManager eventHandlerManager) {
        this.enteredExitedHandler = enteredExitedHandler;
        this.eventHandlerManager = eventHandlerManager;
        enteredExitedHandler.insertNextDispatcher(eventHandlerManager);
    }

    public final EnteredExitedHandler getEnteredExitedHandler() {
        return enteredExitedHandler;
    }

    public final EventHandlerManager getEventHandlerManager() {
        return eventHandlerManager;
    }

    @Override
    public BasicEventDispatcher getFirstDispatcher() {
        return enteredExitedHandler;
    }

    @Override
    public BasicEventDispatcher getLastDispatcher() {
        return eventHandlerManager;
    }
}
