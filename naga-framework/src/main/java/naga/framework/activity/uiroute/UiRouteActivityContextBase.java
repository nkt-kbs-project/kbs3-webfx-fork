package naga.framework.activity.uiroute;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import naga.framework.activity.view.ViewActivityContext;
import naga.framework.ui.i18n.I18n;
import naga.framework.ui.router.UiRouter;
import naga.platform.activity.ActivityContext;
import naga.platform.activity.ActivityContextBase;
import naga.platform.activity.ActivityContextFactory;
import naga.platform.json.Json;
import naga.platform.json.spi.JsonObject;

/**
 * @author Bruno Salmon
 */
public class UiRouteActivityContextBase
        <THIS extends UiRouteActivityContextBase<THIS>>

        extends ActivityContextBase<THIS>
        implements UiRouteActivityContext<THIS> {

    private UiRouter uiRouter;
    private JsonObject params;
    private I18n i18n;

    protected UiRouteActivityContextBase(ActivityContext parentContext, ActivityContextFactory<THIS> contextFactory) {
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
        if (parentContext instanceof UiRouteActivityContext)
            return ((UiRouteActivityContext) parentContext).getUiRouter();
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
            if (parentContext instanceof UiRouteActivityContext)
                return ((UiRouteActivityContext<?>) parentContext).getParameter(key);
        }
        return value;
    }

    public static <IC extends ActivityContext<IC>, OC extends UiRouteActivityContextBase<OC>> OC toUiRouterActivityContextBase(IC activityContext) {
        return from(activityContext, ac -> ac instanceof UiRouteActivityContextBase);
    }

    private final Property<Boolean> activeProperty = new SimpleObjectProperty<>(false);
    @Override
    public ReadOnlyProperty<Boolean> activeProperty() {
        return activeProperty;
    }

    @Override
    public THIS setI18n(I18n i18n) {
        this.i18n = i18n;
        return (THIS) this;
    }

    @Override
    public I18n getI18n() {
        if (i18n != null)
            return i18n;
        ActivityContext parentContext = getParentContext();
        if (parentContext instanceof ViewActivityContext)
            return ((ViewActivityContext) parentContext).getI18n();
        return null;
    }
}
