package mongoose.backend.activities.payments;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import mongoose.backend.controls.masterslave.group.GroupMasterSlaveView;
import mongoose.backend.controls.masterslave.group.GroupView;
import mongoose.client.activity.eventdependent.EventDependentPresentationModel;
import mongoose.client.activity.eventdependent.EventDependentViewDomainActivity;
import mongoose.client.entities.util.FilterButtonSelectorFactoryMixin;
import mongoose.client.entities.util.Filters;
import mongoose.shared.domainmodel.functions.AbcNames;
import mongoose.shared.entities.Filter;
import mongoose.shared.entities.MoneyTransfer;
import webfx.framework.client.operation.action.OperationActionFactoryMixin;
import webfx.framework.client.ui.controls.button.EntityButtonSelector;
import webfx.framework.client.ui.filter.ReactiveExpressionFilter;
import webfx.framework.client.ui.filter.ReactiveExpressionFilterFactoryMixin;
import webfx.framework.client.ui.filter.StringFilter;
import webfx.framework.client.ui.layouts.SceneUtil;
import webfx.fxkit.extra.controls.displaydata.datagrid.DataGrid;
import webfx.fxkit.util.properties.Properties;
import webfx.platform.shared.util.Strings;

import static webfx.framework.client.ui.layouts.LayoutUtil.setHGrowable;
import static webfx.framework.client.ui.layouts.LayoutUtil.setMaxHeightToInfinite;

final class PaymentsActivity extends EventDependentViewDomainActivity
        implements OperationActionFactoryMixin,
        FilterButtonSelectorFactoryMixin,
        ReactiveExpressionFilterFactoryMixin {

    private TextField searchBox; // Keeping this reference to activate focus on activity resume

    private final PaymentsPresentationModel pm = new PaymentsPresentationModel();

    @Override
    public EventDependentPresentationModel getPresentationModel() {
        return pm; // eventId and organizationId will then be updated from route
    }

    @Override
    public Node buildUi() {
        BorderPane container = new BorderPane();
        // Building the top bar
        EntityButtonSelector<Filter> conditionSelector = createConditionFilterButtonSelector("payments","MoneyTransfer", container),
                                     groupSelector     = createGroupFilterButtonSelector(    "payments","MoneyTransfer", container);
        searchBox = newTextFieldWithPrompt("GenericSearchPlaceholder");
        container.setTop(new HBox(10, conditionSelector.getButton(), groupSelector.getButton(), setMaxHeightToInfinite(setHGrowable(searchBox))));

        // Building the main content, which is a group/master/slave view (group = group view, master = bookings table + limit checkbox, slave = booking details)
        DataGrid masterTable = new DataGrid();
        BorderPane.setAlignment(masterTable, Pos.TOP_CENTER);
        CheckBox limitCheckBox = newCheckBox("LimitTo100");
        limitCheckBox.setSelected(true);
        BorderPane masterPane = new BorderPane(masterTable, null, null, limitCheckBox, null);

        GroupView<MoneyTransfer> groupView = new GroupView<>();
        DataGrid slaveTable = new DataGrid();

        GroupMasterSlaveView groupMasterSlaveView = new GroupMasterSlaveView(Orientation.VERTICAL,
                groupView.buildUi(),
                masterPane,
                slaveTable);
        container.setCenter(groupMasterSlaveView.getSplitPane());

        // Initialization from the presentation model current state
        searchBox.setText(pm.searchTextProperty().getValue());

        // Binding the UI with the presentation model for further state changes
        // User inputs: the UI state changes are transferred in the presentation model
        pm.searchTextProperty().bind(searchBox.textProperty());
        Properties.runNowAndOnPropertiesChange(() -> pm.limitProperty().setValue(limitCheckBox.isSelected() ? 30 : -1), limitCheckBox.selectedProperty());
        masterTable.fullHeightProperty().bind(limitCheckBox.selectedProperty());
        pm.genericDisplaySelectionProperty().bindBidirectional(masterTable.displaySelectionProperty());
        // User outputs: the presentation model changes are transferred in the UI
        masterTable.displayResultProperty().bind(pm.genericDisplayResultProperty());
        slaveTable.displayResultProperty().bind(pm.slaveDisplayResultProperty());

        groupMasterSlaveView.groupVisibleProperty() .bind(Properties.compute(pm.groupStringFilterProperty(), s -> s != null && Strings.isNotEmpty(new StringFilter(s).getGroupBy())));
        groupMasterSlaveView.masterVisibleProperty().bind(Properties.combine(groupMasterSlaveView.groupVisibleProperty(),  pm.selectedGroupProperty(),    (groupVisible, selectedGroup)     -> !groupVisible || selectedGroup != null));
        groupMasterSlaveView.slaveVisibleProperty() .bind(Properties.combine(groupMasterSlaveView.masterVisibleProperty(), pm.selectedPaymentProperty(), (masterVisible, selectedPayment) -> masterVisible && selectedPayment != null && selectedPayment.getDocument() == null));

        // Group view data binding
        groupView.groupDisplayResultProperty().bind(pm.groupDisplayResultProperty());
        groupView.selectedGroupProperty().bind(pm.selectedGroupProperty());
        groupView.groupStringFilterProperty().bind(pm.groupStringFilterProperty());
        pm.selectedGroupConditionStringFilterProperty().bind(groupView.selectedGroupConditionStringFilterProperty());
        pm.groupDisplaySelectionProperty().bind(groupView.groupDisplaySelectionProperty());
        groupView.setReferenceResolver(groupFilter.getRootAliasReferenceResolver());

        pm.conditionStringFilterProperty() .bind(Properties.compute(conditionSelector .selectedItemProperty(), Filters::toStringJson));
        pm.groupStringFilterProperty()     .bind(Properties.compute(groupSelector     .selectedItemProperty(), Filters::toStringJson));
        return container;
    }

    @Override
    public void onResume() {
        super.onResume();
        SceneUtil.autoFocusIfEnabled(searchBox);
    }

    private ReactiveExpressionFilter<MoneyTransfer> groupFilter, masterFilter, slaveFilter;

    @Override
    protected void startLogic() {
        // Setting up the group filter that controls the content displayed in the group view
        groupFilter = this.<MoneyTransfer>createReactiveExpressionFilter("{class: 'MoneyTransfer', alias: 'mt', where: '!receiptsTransfer', orderBy: 'date desc,parent nulls first'}")
                // Applying the event condition
                .combineIfNotNullOtherwiseForceEmptyResult(pm.eventIdProperty(), eventId -> "{where:  `document.event=" + eventId + "`}")
                // Applying the condition and group selected by the user
                .combineIfNotNullOtherwiseForceEmptyResult(pm.conditionStringFilterProperty(), stringFilter -> stringFilter)
                .combineIfNotNullOtherwiseForceEmptyResult(pm.groupStringFilterProperty(), stringFilter -> stringFilter.contains("groupBy") ? stringFilter : "{where: 'false'}")
                // Displaying the result in the group view
                .displayResultInto(pm.groupDisplayResultProperty())
                // Reacting to a group selection
                .setSelectedEntityHandler(pm.groupDisplaySelectionProperty(), pm::setSelectedGroup)
                // Everything set up, let's start now!
                .start();
        // Setting up the master filter that controls the content displayed in the master view
        masterFilter = this.<MoneyTransfer>createReactiveExpressionFilter("{class: 'MoneyTransfer', alias: 'mt', where: '!receiptsTransfer', orderBy: 'date desc,parent nulls first'}")
                .combine("{columns: 'date,document,transactionRef,status,comment,amount,methodIcon,pending,successful'}")
                // Applying the event condition
                .combineIfNotNullOtherwiseForceEmptyResult(pm.eventIdProperty(), eventId -> "{where:  `document..event=" + eventId + " or document is null and exists(select MoneyTransfer where parent=mt and document.event=" + eventId + ")`}")
                // Applying the condition and columns selected by the user
                .combineIfNotNullOtherwiseForceEmptyResult(pm.conditionStringFilterProperty(), stringFilter -> stringFilter)
                // Also, in case groups are showing and a group is selected, applying the condition associated with that group
                .combineIfNotNull(pm.selectedGroupConditionStringFilterProperty(), s -> s)
                // Applying the user search
                .combineIfNotEmptyTrim(pm.searchTextProperty(), s ->
                        Character.isDigit(s.charAt(0)) ? "{where: `ref = " + s + "`}"
                                : s.contains("@") ? "{where: `lower(person_email) like '%" + s.toLowerCase() + "%'`}"
                                : "{where: `person_abcNames like '" + AbcNames.evaluate(s, true) + "'`}")
                // Limit clause
                .combineIfPositive(pm.limitProperty(), limit -> "{limit: `" + limit + "`}")
                // Colorizing the rows
                .applyDomainModelRowStyle()
                // Displaying the result in the master view
                .displayResultInto(pm.genericDisplayResultProperty())
                // When the result is a singe row, automatically select it
                .autoSelectSingleRow()
                // Reacting the a booking selection
                .setSelectedEntityHandler(pm.genericDisplaySelectionProperty(), pm::setSelectedDocument)
                // Activating server push notification
                .setPush(true)
                // Everything set up, let's start now!
                .start();
        // Slave filter
        slaveFilter = this.<MoneyTransfer>createReactiveExpressionFilter("{class: 'MoneyTransfer', alias: 'mt', orderBy: 'date desc,parent nulls first'}")
                .combine("{columns: 'date,document,transactionRef,status,comment,amount,methodIcon,pending,successful'}")
                .combineIfTrue(Properties.compute(pm.selectedPaymentProperty(), p -> p == null || p.getDocument() != null), () -> "{where: 'false'}")
                // Applying the event condition
                .combineIfNotNullOtherwiseForceEmptyResult(pm.selectedPaymentProperty(), mt -> "{where: 'parent=" + mt.getPrimaryKey() + "'}")
                // Colorizing the rows
                .applyDomainModelRowStyle()
                // Displaying the result in the master view
                .displayResultInto(pm.slaveDisplayResultProperty())
                // Activating server push notification
                .setPush(true)
                // Everything set up, let's start now!
                .start();
    }

    @Override
    protected void refreshDataOnActive() {
        groupFilter .refreshWhenActive();
        masterFilter.refreshWhenActive();
        slaveFilter .refreshWhenActive();
    }
}
