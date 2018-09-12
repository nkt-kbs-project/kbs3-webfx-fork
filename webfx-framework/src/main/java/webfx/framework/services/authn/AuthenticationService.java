package webfx.framework.services.authn;

import webfx.framework.services.authn.spi.AuthenticationServiceProvider;
import webfx.platforms.core.util.async.Future;
import webfx.platforms.core.util.serviceloader.SingleServiceLoader;

/**
 * @author Bruno Salmon
 */
public final class AuthenticationService {

    public static Future<?> authenticate(Object userCredentials) {
        return getProvider().authenticate(userCredentials);
    }

    public static AuthenticationServiceProvider getProvider() {
        return SingleServiceLoader.loadService(AuthenticationServiceProvider.class);
    }
}
