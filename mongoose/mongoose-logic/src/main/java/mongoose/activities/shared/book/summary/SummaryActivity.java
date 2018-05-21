package mongoose.activities.shared.book.summary;

import javafx.application.Platform;
import javafx.beans.value.ObservableStringValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import mongoose.activities.shared.book.cart.RouteToCartRequest;
import mongoose.activities.shared.book.shared.BookingCalendar;
import mongoose.activities.shared.book.shared.BookingOptionsPanel;
import mongoose.activities.shared.book.shared.BookingProcessActivity;
import mongoose.activities.shared.book.shared.PersonDetailsPanel;
import mongoose.activities.shared.book.terms.RouteToTermsRequest;
import mongoose.activities.shared.logic.ui.validation.MongooseValidationSupport;
import mongoose.activities.shared.logic.work.WorkingDocument;
import mongoose.activities.shared.logic.work.sync.WorkingDocumentSubmitter;
import mongoose.entities.Cart;
import mongoose.entities.Document;
import naga.fx.properties.Properties;
import naga.platform.services.log.Logger;
import naga.util.Strings;

/**
 * @author Bruno Salmon
 */
class SummaryActivity extends BookingProcessActivity {

    private BookingOptionsPanel bookingOptionsPanel;
    private Node bookingCalendarSection;
    private BookingCalendar bookingCalendar;
    private PersonDetailsPanel personDetailsPanel;
    private TextArea commentTextArea;
    private CheckBox termsCheckBox;
    private ObservableStringValue agreeTCTranslationProperty; // to avoid GC
    private final MongooseValidationSupport validationSupport = new MongooseValidationSupport();

    @Override
    protected void createViewNodes() {
        super.createViewNodes();
        bookingOptionsPanel = new BookingOptionsPanel(getI18n());
        bookingCalendar = new BookingCalendar(false, getI18n());
        bookingCalendarSection = createBookingCalendarSection(bookingCalendar);
        personDetailsPanel = new PersonDetailsPanel(getEvent(), this, pageContainer);
        personDetailsPanel.setEditable(false);

        BorderPane commentPanel = createSectionPanel("Comment");
        commentPanel.setCenter(commentTextArea = newTextAreaWithPrompt("CommentPlaceholder"));

        BorderPane termsPanel = createSectionPanel("TermsAndConditions");
        termsPanel.setCenter(termsCheckBox = new CheckBox());
        BorderPane.setAlignment(termsCheckBox, Pos.CENTER_LEFT);
        BorderPane.setMargin(termsCheckBox, new Insets(10));
        agreeTCTranslationProperty = translationProperty("AgreeTC");
        Properties.runNowAndOnPropertiesChange(p -> setTermsCheckBoxText(Strings.toSafeString(p.getValue())), agreeTCTranslationProperty);

        verticalStack.getChildren().setAll(
                bookingOptionsPanel.getOptionsPanel(),
                bookingCalendarSection,
                personDetailsPanel.getSectionPanel(),
                commentPanel,
                termsPanel,
                nextButton
        );

        validationSupport.addValidationRule(termsCheckBox.selectedProperty(), termsCheckBox, "Please read and accept the terms and conditions");
    }

    private void setTermsCheckBoxText(String text) {
        Platform.runLater(() -> {
            int aStartPos = text.indexOf("<a");
            int aTextStart = text.indexOf(">", aStartPos) + 1;
            int aTextEnd = text.indexOf("</a>", aTextStart);
            String leftText = text.substring(0, aStartPos - 1);
            String hyperText = text.substring(aTextStart, aTextEnd);
            String rightText = text.substring(aTextEnd + 4);
            Label leftLabel = new Label(leftText);
            Hyperlink hyperlink = new Hyperlink(hyperText);
            hyperlink.setOnAction(e -> showTermsDialog());
            Label rightLabel = new Label(rightText);
/*
            TextFlow textFlow = new TextFlow(leftLabel, hyperlink, rightLabel);
            textFlow.setPrefHeight(hyperlink.getHeight());
            termsCheckBox.setGraphic(textFlow);
*/
            FlowPane textContainer = new FlowPane(leftLabel, hyperlink, rightLabel);
            textContainer.setAlignment(Pos.CENTER_LEFT);
            termsCheckBox.setGraphic(textContainer);
        });
    }

    private void showTermsDialog() {
        //new TermsDialog(getEventId(), getDataSourceModel(), getI18n(), pageContainer).setOnClose(() -> termsCheckBox.setSelected(true)).show();
        new RouteToTermsRequest(getEventId(), getHistory()).execute();
    }

    private void syncUiFromModel() {
        WorkingDocument workingDocument = getWorkingDocument();
        if (workingDocument != null) {
            bookingCalendar.createOrUpdateCalendarGraphicFromWorkingDocument(workingDocument, true);
            bookingOptionsPanel.syncUiFromModel(workingDocument);
            personDetailsPanel.syncUiFromModel(workingDocument.getDocument());
        }
    }

    private void syncModelFromUi() {
        WorkingDocument workingDocument = getWorkingDocument();
        if (workingDocument != null)
            personDetailsPanel.syncModelFromUi(workingDocument.getDocument());
    }

    @Override
    public void onResume() {
        super.onResume();
        syncUiFromModel();
    }

    @Override
    public void onPause() {
        super.onPause();
        syncModelFromUi();
    }

    @Override
    protected void onNextButtonPressed(ActionEvent event) {
        if (validationSupport.isValid())
            WorkingDocumentSubmitter.submit(getWorkingDocument(), commentTextArea.getText()).setHandler(ar -> {
                if (ar.failed())
                    Logger.log("Error submitting booking", ar.cause());
                else {
                    Document document = ar.result();
                    Cart cart = document.getCart();
                    if (cart == null) {
                        WorkingDocument workingDocument = getWorkingDocument();
                        if (workingDocument.getLoadedWorkingDocument() != null)
                            workingDocument = workingDocument.getLoadedWorkingDocument();
                        document = workingDocument.getDocument();
                        cart = document.getCart();
                    }
                    new RouteToCartRequest(cart.getUuid(), getHistory()).execute();
                }
            });
    }
}