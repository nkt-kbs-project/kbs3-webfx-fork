package mongoose.activities.backend.cloneevent;

import naga.framework.operation.HasOperationCode;
import naga.framework.ui.router.PushRouteRequest;
import naga.platform.client.url.history.History;

/**
 * @author Bruno Salmon
 */
public class RouteToCloneEventRequest extends PushRouteRequest implements HasOperationCode {

    private final static String OPERATION_CODE = "CLONE_EVENT";

    public RouteToCloneEventRequest(Object eventId, History history) {
        super(CloneEventRouting.getCloneEventPath(eventId), history);
    }

    @Override
    public Object getOperationCode() {
        return OPERATION_CODE;
    }
}