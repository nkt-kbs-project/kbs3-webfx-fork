package naga.providers.toolkit.html.drawing.html.view;

import elemental2.HTMLInputElement;
import naga.providers.toolkit.html.util.HtmlUtil;
import naga.toolkit.drawing.scene.control.TextField;
import naga.toolkit.drawing.spi.view.base.TextFieldViewBase;
import naga.toolkit.drawing.spi.view.base.TextFieldViewMixin;
import naga.toolkit.drawing.text.Font;

/**
 * @author Bruno Salmon
 */
public class HtmlTextFieldView
        extends HtmlNodeView<TextField, TextFieldViewBase, TextFieldViewMixin>
        implements TextFieldViewMixin, HtmlLayoutMeasurable {

    public HtmlTextFieldView() {
        super(new TextFieldViewBase(), HtmlUtil.createTextInput());
        HTMLInputElement inputElement = (HTMLInputElement) getElement();
        inputElement.oninput = a -> {
            getNode().setText(inputElement.value);
            return null;
        };
    }

    @Override
    public void updateFont(Font font) {
        setFontAttributes(font);
    }

    @Override
    public void updateText(String text) {
        setElementTextContent(text);
    }

    @Override
    public void updatePrompt(String prompt) {
        ((HTMLInputElement) getElement()).placeholder = prompt;
    }
}
