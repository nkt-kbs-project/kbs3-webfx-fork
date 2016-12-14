package naga.providers.toolkit.javafx.fx.viewer;

import javafx.scene.web.WebView;
import naga.commons.util.Strings;
import naga.toolkit.fx.ext.control.HtmlText;
import naga.toolkit.fx.spi.viewer.base.HtmlTextViewerBase;
import naga.toolkit.fx.spi.viewer.base.HtmlTextViewerMixin;

/**
 * @author Bruno Salmon
 */
public class FxHtmlTextViewer
        <FxN extends WebView, N extends HtmlText, NV extends HtmlTextViewerBase<N, NV, NM>, NM extends HtmlTextViewerMixin<N, NV, NM>>
        extends FxNodeViewer<FxN, N, NV, NM>
        implements HtmlTextViewerMixin<N, NV, NM>, FxLayoutMeasurable {

    private final WebView webView = new WebView();

    public FxHtmlTextViewer() {
        this((NV) new HtmlTextViewerBase());
    }

    FxHtmlTextViewer(NV base) {
        super(base);
        updateText(null);
    }

    @Override
    FxN createFxNode() {
        return (FxN) webView;
    }

    @Override
    public void updateText(String text) {
        webView.getEngine().loadContent("<div style='margin: 0; padding: 0;'>" + Strings.toSafeString(text) + "</div>");
        webView.getEngine().executeScript(jsFunctions);
    }

    @Override
    public void updateWidth(Double width) {
        webView.getEngine().executeScript("setDocumentWidth(" + documentWidth(width) + ");");
        updateResize();
    }

    @Override
    public void updateHeight(Double height) {
        updateResize();
    }

    private void updateResize() {
        N node = getNode();
        webView.resize(node.getWidth(), node.getHeight());
    }

    @Override
    public double minWidth(double height) {
        return 0;
    }

    @Override
    public double minHeight(double width) {
        return 0;
    }

    @Override
    public double prefHeight(double width) {
        String heightText = webView.getEngine().executeScript("documentPrefHeight(" + documentWidth(width) + ")").toString();
        return webViewHeight(Double.valueOf(heightText));
    }

    private double documentWidth(double webViewWidth) {
        return webViewWidth - 12;
    }

    private double webViewHeight(double documentHeight) {
        return documentHeight + 25;
    }

    @Override
    public double maxHeight(double width) {
        return Double.MAX_VALUE;
    }

    private final static String jsFunctions = "" +
            "function documentPrefHeight(width) {" +
            "var e = document.body.firstChild;" +
            "if (!e) return 0;" +
            "var s = e.style;" +
            "var w = s.width;" +
            "s.width = width > 0 ? width + 'px' : w;" +
            "var h = e.offsetHeight;" +
            "s.width = w;" +
            "return h;" +
            "};" +
            "function setDocumentWidth(width) {" +
            "var e = document.body.firstChild;" +
            "if (!e) return;" +
            "var s = e.style;" +
            "var w = s.width;" +
            "s.width = width > 0 ? width + 'px' : w;" +
            "};" +
            "document.body.style.overflow = 'hidden';";
}