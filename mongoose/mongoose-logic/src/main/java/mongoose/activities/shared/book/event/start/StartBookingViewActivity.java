package mongoose.activities.shared.book.event.start;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mongoose.actions.MongooseActions;
import mongoose.activities.shared.book.event.shared.BookingProcessViewActivity;
import mongoose.util.Labels;
import naga.framework.ui.layouts.LayoutUtil;
import naga.fx.properties.Properties;
import naga.fx.spi.Toolkit;
import naga.fx.util.ImageStore;

/**
 * @author Bruno Salmon
 */
public class StartBookingViewActivity extends BookingProcessViewActivity {

    private ImageView eventImageView;
    private BorderPane eventImageViewContainer;
    private Label eventTitle;
    private Pane eventContainer;

    public StartBookingViewActivity() {
        super(null);
    }

    @Override
    protected void createViewNodes() {
        super.createViewNodes();
        eventImageViewContainer = LayoutUtil.setMinWidth(new BorderPane(eventImageView = new ImageView()), 0);
        eventTitle = new Label();
        eventTitle.setTextFill(Color.WHITE);
        Button bookButton = newTransparentButton(MongooseActions.newVisitBookAction(this::onBookButtonPressed));
        Button feesButton = newTransparentButton(MongooseActions.newVisitFeesAction(this::onFeesButtonPressed));
        Button termsButton = newTransparentButton(MongooseActions.newVisitTermsAndConditionsAction(this::onTermsButtonPressed));
        //Button programButton = newTransparentButton(MongooseActions.newVisitProgramAction(this::onProgramButtonPressed));
        Font eventFont = Font.font("Verdana", 24);
        Font bookButtonFont = Font.font("Verdana", 18);
        Font otherButtonFont = Font.font("Verdana", 12);
        eventTitle.setFont(eventFont);
        bookButton.setFont(bookButtonFont);
        feesButton.setFont(otherButtonFont);
        termsButton.setFont(otherButtonFont);
        //programButton.setFont(otherButtonFont);
        FlowPane flowPane = new FlowPane(5, 20, feesButton, termsButton/*, programButton*/);
        flowPane.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(20, eventImageViewContainer, eventTitle, bookButton, flowPane);
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.TOP_CENTER);
        eventContainer = LayoutUtil.createGoldLayout(vBox, 1.0, 0, null);
    }

    @Override
    protected Node assemblyViewNodes() {
        return eventContainer;
    }

    @Override
    protected Node styleUi(Node uiNode) {
        eventContainer.setVisible(false);
        onEvent().setHandler(ar -> {
            Toolkit.get().scheduler().runInUiThread(() -> {
                if (ar.succeeded()) {
                    Labels.translateLabel(eventTitle, Labels.bestLabelOrName(ar.result()), getI18n());
                    Object url = ar.result().evaluate("buddha.image.url");
                    if (url instanceof String) {
                        Image image = ImageStore.getOrCreateImage((String) url);
                        eventImageView.setImage(image);
                        eventImageView.setPreserveRatio(true);
                        eventImageView.fitWidthProperty().bind(Properties.combine(eventImageViewContainer.widthProperty(), image.widthProperty(),
                                (w1, w2) -> Math.min(w1.doubleValue(), w2.doubleValue())));
                    }
                }
                eventContainer.setVisible(true);
            });
        });
        return super.styleUi(uiNode);
    }

    private void onProgramButtonPressed() {
        goToNextBookingProcessPage("program");
    }

    private void onTermsButtonPressed() {
        goToNextBookingProcessPage("terms");
    }

    private void onFeesButtonPressed() {
        goToNextBookingProcessPage("fees");
    }

    private void onBookButtonPressed() {
        goToNextBookingProcessPage("options");
    }
}
