package dev.webfx.kit.mapper.peers.javafxcontrols.gwt.html;

import dev.webfx.kit.mapper.peers.javafxcontrols.base.TextInputControlPeerBase;
import dev.webfx.kit.mapper.peers.javafxcontrols.base.TextInputControlPeerMixin;
import dev.webfx.kit.mapper.peers.javafxgraphics.SceneRequester;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwt.util.HtmlUtil;
import dev.webfx.kit.util.properties.FXProperties;
import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.platform.util.Booleans;
import dev.webfx.platform.util.Objects;
import dev.webfx.platform.util.Strings;
import elemental2.dom.*;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Font;

/**
 * @author Bruno Salmon
 */
public abstract class HtmlTextInputControlPeer
        <N extends TextInputControl, NB extends TextInputControlPeerBase<N, NB, NM>, NM extends TextInputControlPeerMixin<N, NB, NM>>

        extends HtmlControlPeer<N, NB, NM>
        implements TextInputControlPeerMixin<N, NB, NM>, TextInputControl.SelectableTextInputControlPeer {

    public HtmlTextInputControlPeer(NB base, HTMLElement textInputElement) {
        super(base, textInputElement);
        prepareDomForAdditionalSkinChildren("fx-textinputcontrol");
        // Restoring pointer events (were disabled by prepareDomForAdditionalSkinChildren()) in case the graphic is clickable (ex: radio button)
        HtmlUtil.setStyleAttribute(getChildrenContainer(), "pointer-events", "auto");
        textInputElement.oninput = e -> {
            getNode().setText(getValue());
            updateElementInitialValue();
            return null;
        };
        textInputElement.onkeypress = e -> {
            if ("Enter".equals(((KeyboardEvent) e).key))
                getNode().fireEvent(new ActionEvent());
            return null;
        };
    /*
    The behavior when setting the style width/height on a text input seems different from on other html elements.
    On other html elements (ex: a button) this will size the outer visual box (including padding and border) to the
    specified width/height. On a text input, this will size the inner visual box (excluding the padding and border).
    */
        subtractCssPaddingBorderWhenUpdatingSize = true;
    }

    @Override
    public void bind(N node, SceneRequester sceneRequester) {
        super.bind(node, sceneRequester);
        FXProperties.runNowAndOnPropertiesChange(() -> onSceneChanged(node.getScene()), node.sceneProperty());
    }

    private void onSceneChanged(Scene scene) {
        updateElementInitialValue();
    }

    private void updateElementInitialValue() {
        // The "value" attribute (which normally refers to the initial value only of the text input) has no meaning for
        // WebFX (there is no mapping with JavaFX) but we update it here for HTML CSS styling purpose. We set it either
        // to "" or "not-empty". This is used for example in modality.css with input[type="password"]:not([value=""]) {
        // font-size: 36px; ... } to increase the size of the dots for passwords (otherwise they are tiny), but we don't
        // want this big font size to be applied to the prompt text (i.e. html placeholder) which should be displayed in
        // the normal font size otherwise (when the password input is empty).
        Element focusElement = getFocusElement();
        if (focusElement != null) {
            String initialValue = Strings.isEmpty(getNode().getText()) ? "" : "not-empty";
            setElementAttribute(focusElement, "value", initialValue);
        }
    }

    @Override
    public void selectRange(int anchor, int caretPosition) {
        Element focusElement = getFocusElement();
        if (focusElement instanceof HTMLInputElement) {
            HTMLInputElement inputElement = (HTMLInputElement) focusElement;
            inputElement.setSelectionRange(anchor, caretPosition);
            // Note: There is a bug in Chrome: the previous selection request is ignored if it happens during a focus requested
            // So let's double-check if the selection has been applied
            if (inputElement.selectionStart != anchor || inputElement.selectionEnd != caretPosition)
                // If not, we reapply the selection request later, after the focus request should have been completed
                UiScheduler.scheduleInAnimationFrame(() -> inputElement.setSelectionRange(anchor, caretPosition), 1);
        }
    }

    @Override
    public void updateFont(Font font) {
        setFontAttributes(font);
    }

    @Override
    public void updateText(String text) {
        String safeText = Strings.toSafeString(text);
        if (!Objects.areEquals(getValue(), safeText)) // To avoid caret position reset
            setValue(safeText);
    }

    @Override
    public void updatePromptText(String prompt) {
        if (!getNode().getStyleClass().contains("material"))
            setPlaceholder(Strings.toSafeString(prompt));
    }

    @Override
    public void updateEditable(Boolean editable) {
        setElementAttribute(getElement(),"readonly", Booleans.isFalse(editable) ? "true" : null);
    }

    protected String getValue() {
        Element element = getFocusElement();
        if (element instanceof HTMLInputElement)
            return ((HTMLInputElement) element).value;
        if (element instanceof HTMLTextAreaElement)
            return ((HTMLTextAreaElement) element).value;
        return null;
    }

    protected void setValue(String value) {
        Element element = getFocusElement();
        if (element instanceof HTMLInputElement)
            ((HTMLInputElement) element).value = value;
        else if (element instanceof HTMLTextAreaElement)
            ((HTMLTextAreaElement) element).value = value;
    }

    protected void setPlaceholder(String placeholder) {
        HTMLElement element = getElement();
        if (element instanceof HTMLInputElement)
            ((HTMLInputElement) element).placeholder = placeholder;
        else if (element instanceof HTMLTextAreaElement)
            setElementAttribute(element, "placeholder", placeholder);
    }
}
