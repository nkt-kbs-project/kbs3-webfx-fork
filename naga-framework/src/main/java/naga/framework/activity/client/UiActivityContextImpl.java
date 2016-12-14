package naga.framework.activity.client;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import naga.framework.ui.i18n.I18n;
import naga.framework.ui.router.UiRouter;
import naga.platform.activity.ActivityContext;
import naga.platform.activity.ActivityContextFactory;
import naga.platform.activity.ActivityContextImpl;
import naga.platform.activity.HasActivityContext;
import naga.platform.json.Json;
import naga.platform.json.spi.JsonObject;
import naga.toolkit.fx.scene.Node;

/**
 * @author Bruno Salmon
 */
public class UiActivityContextImpl<C extends UiActivityContextImpl<C>> extends ActivityContextImpl<C> implements UiActivityContext<C> {

    private UiRouter uiRouter;
    private JsonObject params;
    private I18n i18n;

    protected UiActivityContextImpl(ActivityContext parentContext, ActivityContextFactory<C> contextFactory) {
        super(parentContext, contextFactory);
    }

    public void setUiRouter(UiRouter uiRouter) {
        this.uiRouter = uiRouter;
    }

    @Override
    public UiRouter getUiRouter() {
        if (uiRouter != null)
            return uiRouter;
        ActivityContext parentContext = getParentContext();
        if (parentContext instanceof UiActivityContext)
            return ((UiActivityContext) parentContext).getUiRouter();
        return null;
    }

    public void setParams(JsonObject params) {
        this.params = params;
    }

    @Override
    public JsonObject getParams() {
        if (params == null)
            params = Json.createObject();
        return params;
    }

    @Override
    public <T> T getParameter(String key) {
        T value = getParams().get(key);
        if (value == null && !params.has(key)) {
            ActivityContext parentContext = getParentContext();
            if (parentContext instanceof UiActivityContext)
                return ((UiActivityContext<?>) parentContext).getParameter(key);
        }
        return null;
    }

    private final Property<Node> nodeProperty = new SimpleObjectProperty<>();
    @Override
    public Property<Node> nodeProperty() {
        return nodeProperty;
    }

    private final Property<Node> mountNodeProperty = new SimpleObjectProperty<>();
    @Override
    public Property<Node> mountNodeProperty() {
        return mountNodeProperty;
    }

    @Override
    public I18n getI18n() {
        if (i18n != null)
            return i18n;
        ActivityContext parentContext = getParentContext();
        if (parentContext instanceof UiActivityContext)
            return ((UiActivityContext) parentContext).getI18n();
        return null;
    }

    public static <C extends ActivityContext> UiActivityContextImpl from(C activityContext) {
        if (activityContext instanceof UiActivityContextImpl)
            return (UiActivityContextImpl) activityContext;
        if (activityContext instanceof HasActivityContext) // including ActivityContextDirectAccess
            return from(((HasActivityContext) activityContext).getActivityContext());
        return null;
    }

}
