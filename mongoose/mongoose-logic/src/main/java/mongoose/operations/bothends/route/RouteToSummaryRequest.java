package mongoose.operations.bothends.route;

import mongoose.activities.bothends.book.summary.SummaryRouting;
import naga.framework.operations.route.RoutePushRequest;
import naga.platform.client.url.history.History;

/**
 * @author Bruno Salmon
 */
public class RouteToSummaryRequest extends RoutePushRequest {

    public RouteToSummaryRequest(Object eventId, History history) {
        super(SummaryRouting.getSummaryPath(eventId), history);
    }

}