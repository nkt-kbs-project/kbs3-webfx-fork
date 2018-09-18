package webfx.platforms.core.services.query;

import webfx.platforms.core.services.buscall.BusCallService;
import webfx.platforms.core.services.query.spi.QueryServiceProvider;
import webfx.platforms.core.services.query.spi.impl.LocalOrRemoteQueryServiceProviderImpl;
import webfx.platforms.core.util.async.Batch;
import webfx.platforms.core.util.async.Future;
import webfx.platforms.core.util.serviceloader.SingleServiceLoader;

/**
 * @author Bruno Salmon
 */
public class QueryService {

    public static final String QUERY_SERVICE_ADDRESS = "service/query";
    public static final String QUERY_BATCH_SERVICE_ADDRESS = "service/query/batch";

    static {
        SingleServiceLoader.registerDefaultServiceFactory(QueryServiceProvider.class, LocalOrRemoteQueryServiceProviderImpl::new);
        // registerJsonCodecsAndBusCalls() body:
        BusCallService.registerJavaAsyncFunctionAsCallableService(QUERY_SERVICE_ADDRESS, QueryService::executeQuery);
        BusCallService.registerJavaAsyncFunctionAsCallableService(QUERY_BATCH_SERVICE_ADDRESS, QueryService::executeQueryBatch);
    }

    public static void registerJsonCodecsAndBusCalls() {
        // body actually moved to the static constructor
    }

    public static QueryServiceProvider getProvider() {
        return SingleServiceLoader.loadService(QueryServiceProvider.class);
    }

    public static void registerProvider(QueryServiceProvider provider) {
        SingleServiceLoader.cacheServiceInstance(QueryServiceProvider.class, provider);
    }

    public static Future<QueryResult> executeQuery(QueryArgument argument) {
        return getProvider().executeQuery(argument);
    }

    // Batch support

    public static Future<Batch<QueryResult>> executeQueryBatch(Batch<QueryArgument> batch) {
        return getProvider().executeQueryBatch(batch);
    }

}
