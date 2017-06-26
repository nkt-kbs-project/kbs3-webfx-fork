package naga.framework.ui.controls;

import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import naga.commons.util.function.Consumer;
import naga.framework.ui.action.ButtonFactoryMixin;

/**
 * @author Bruno Salmon
 */
public interface DialogBuilder extends ButtonFactoryMixin {

    Region build();

    void setDialogCallback(DialogCallback dialogCallback);

    DialogCallback getDialogCallback();

    default Button newButton(Object i18nKey, Consumer<DialogCallback> dialogAction) {
        return newButton(i18nKey, () -> dialogAction.accept(getDialogCallback()));
    }
}
