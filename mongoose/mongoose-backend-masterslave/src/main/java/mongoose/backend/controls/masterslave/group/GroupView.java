package mongoose.backend.controls.masterslave.group;

import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import mongoose.client.presentationmodel.*;
import webfx.framework.client.ui.filter.ExpressionColumn;
import webfx.framework.client.ui.filter.StringFilter;
import webfx.framework.shared.expression.Expression;
import webfx.framework.shared.expression.builder.ReferenceResolver;
import webfx.framework.shared.expression.builder.ThreadLocalReferenceResolver;
import webfx.framework.shared.expression.terms.As;
import webfx.framework.shared.expression.terms.ExpressionArray;
import webfx.framework.shared.expression.terms.function.Call;
import webfx.framework.shared.orm.entity.Entity;
import webfx.framework.shared.orm.entity.EntityId;
import webfx.fxkit.extra.controls.displaydata.SelectableDisplayResultControl;
import webfx.fxkit.extra.controls.displaydata.chart.AreaChart;
import webfx.fxkit.extra.controls.displaydata.chart.BarChart;
import webfx.fxkit.extra.controls.displaydata.chart.PieChart;
import webfx.fxkit.extra.controls.displaydata.datagrid.DataGrid;
import webfx.fxkit.extra.displaydata.*;
import webfx.fxkit.extra.type.PrimType;
import webfx.fxkit.extra.type.Type;
import webfx.fxkit.extra.type.Types;
import webfx.fxkit.extra.util.ImageStore;
import webfx.platform.shared.util.Numbers;

public final class GroupView<E extends Entity> implements
        HasGroupDisplayResultProperty,
        HasGroupDisplaySelectionProperty,
        HasGroupStringFilterProperty,
        HasSelectedGroupConditionStringFilterProperty,
        HasSelectedGroupProperty<E> {

    private final ObjectProperty<DisplayResult> groupDisplayResultProperty = new SimpleObjectProperty<>();
    @Override
    public ObjectProperty<DisplayResult> groupDisplayResultProperty() { return groupDisplayResultProperty; }

    private final ObjectProperty<DisplaySelection> groupDisplaySelectionProperty = new SimpleObjectProperty<>();
    @Override
    public ObjectProperty<DisplaySelection> groupDisplaySelectionProperty() { return groupDisplaySelectionProperty; }

    private final StringProperty groupStringFilterProperty = new SimpleStringProperty();
    @Override
    public StringProperty groupStringFilterProperty() { return groupStringFilterProperty; }

    private final ObjectProperty<E> selectedGroupProperty = new SimpleObjectProperty<E/*GWT*/>() {
        @Override
        protected void invalidated() {
            updateSelectedGroupCondition();
        }
    };
    @Override
    public ObjectProperty<E> selectedGroupProperty() { return selectedGroupProperty; }

    private final StringProperty selectedGroupConditionStringFilterProperty = new SimpleStringProperty();
    @Override
    public StringProperty selectedGroupConditionStringFilterProperty() { return selectedGroupConditionStringFilterProperty; }

    private ReferenceResolver referenceResolver;

    public void setReferenceResolver(ReferenceResolver referenceResolver) {
        this.referenceResolver = referenceResolver;
    }

    private final boolean tableOnly;

    public GroupView() {
        this(false);
    }

    public GroupView(boolean tableOnly) {
        this.tableOnly = tableOnly;
    }

    public static <E extends Entity> GroupView<E> createAndBind(HasGroupDisplayResultProperty pm) {
        GroupView<E> groupView = new GroupView<>();
        groupView.doDataBinding(pm);
        return groupView;
    }

    public static <E extends Entity> GroupView<E> createTableOnlyAndBind(HasGroupDisplayResultProperty pm) {
        GroupView<E> groupView = new GroupView<>();
        groupView.doDataBinding(pm);
        return groupView;
    }

    public static <E extends Entity> GroupView<E> createAndBind(ObjectProperty<DisplayResult> sourceGroupDisplayResultProperty,
                     ObjectProperty<DisplaySelection> targetGroupDisplaySelectionProperty,
                     StringProperty sourceGroupStringFilterProperty,
                     StringProperty targetSelectedGroupConditionStringFilterProperty,
                     ObjectProperty<E> sourceSelectedGroupProperty) {
        GroupView<E> groupView = new GroupView<>();
        groupView.doDataBinding(sourceGroupDisplayResultProperty, targetGroupDisplaySelectionProperty, sourceGroupStringFilterProperty, targetSelectedGroupConditionStringFilterProperty, sourceSelectedGroupProperty);
        return groupView;
    }

    public void doDataBinding(HasGroupDisplayResultProperty pm) {
        doDataBinding(pm.groupDisplayResultProperty(),
                pm instanceof HasGroupDisplaySelectionProperty ? ((HasGroupDisplaySelectionProperty) pm).groupDisplaySelectionProperty() : null,
                pm instanceof HasGroupStringFilterProperty ? ((HasGroupStringFilterProperty) pm).groupStringFilterProperty() : null,
                pm instanceof HasSelectedGroupConditionStringFilterProperty ? ((HasSelectedGroupConditionStringFilterProperty) pm).selectedGroupConditionStringFilterProperty() : null,
                pm instanceof HasSelectedGroupProperty ? ((HasSelectedGroupProperty) pm).selectedGroupProperty() : null
        );
    }

    public void doDataBinding(ObjectProperty<DisplayResult> sourceGroupDisplayResultProperty,
                              ObjectProperty<DisplaySelection> targetGroupDisplaySelectionProperty,
                              StringProperty sourceGroupStringFilterProperty,
                              StringProperty targetSelectedGroupConditionStringFilterProperty,
                              ObjectProperty<E> sourceSelectedGroupProperty) {
        bindWithSourceGroupDisplayResultProperty(sourceGroupDisplayResultProperty);
        bindWithTargetGroupDisplaySelectionProperty(targetGroupDisplaySelectionProperty);
        bindWithSourceGroupStringFilterProperty(sourceGroupStringFilterProperty);
        bindWithTargetSelectedGroupConditionStringFilterProperty(targetSelectedGroupConditionStringFilterProperty);
        bindWithSourceSelectedGroupProperty(sourceSelectedGroupProperty);
    }

    public void bindWithSourceGroupDisplayResultProperty(ObjectProperty<DisplayResult> sourceGroupDisplayResultProperty) {
        if (sourceGroupDisplayResultProperty != null)
            groupDisplayResultProperty.bind(sourceGroupDisplayResultProperty);
    }

    public void bindWithTargetGroupDisplaySelectionProperty(ObjectProperty<DisplaySelection> targetGroupDisplaySelectionProperty) {
        if (targetGroupDisplaySelectionProperty != null)
            targetGroupDisplaySelectionProperty.bind(groupDisplaySelectionProperty);
    }

    public void bindWithSourceGroupStringFilterProperty(StringProperty sourceGroupStringFilterProperty) {
        if (sourceGroupStringFilterProperty != null)
            groupStringFilterProperty.bind(sourceGroupStringFilterProperty);
    }

    public void bindWithTargetSelectedGroupConditionStringFilterProperty(StringProperty targetSelectedGroupConditionStringFilterProperty) {
        if (targetSelectedGroupConditionStringFilterProperty != null)
            targetSelectedGroupConditionStringFilterProperty.bind(selectedGroupConditionStringFilterProperty);
    }

    public void bindWithSourceSelectedGroupProperty(ObjectProperty<E> sourceSelectedGroupProperty) {
        if (sourceSelectedGroupProperty != null)
            selectedGroupProperty.bind(sourceSelectedGroupProperty);
    }

    public Node buildUi() {
        Node ui = tableOnly ? bindControl(new DataGrid()) : new TabPane(
                createGroupTab("table", "images/s16/table.png", new DataGrid()),
                createGroupTab("pie", "images/s16/pieChart.png", new PieChart()),
                createGroupTab("bar", "images/s16/barChart.png", new BarChart()),
                createGroupTab("area", "images/s16/barChart.png", new AreaChart())
        );
        ui.getProperties().put("groupView", this); // This is to avoid GC
        return ui;
    }

    private Tab createGroupTab(String text, String iconPath, SelectableDisplayResultControl control) {
        Tab tab = new Tab(text, bindControl(control));
        tab.setGraphic(ImageStore.createImageView(iconPath));
        tab.setClosable(false);
        return tab;
    }

    private <C extends SelectableDisplayResultControl> C bindControl(C control) {
        if (control instanceof DataGrid) {
            control.displayResultProperty().bind(groupDisplayResultProperty());
            groupDisplaySelectionProperty().bind(control.displaySelectionProperty());
        } else if (control != null)
            groupDisplayResultProperty().addListener((observable, oldValue, rs) ->
                    control.setDisplayResult(toSingleSeriesChartDisplayResult(rs, control instanceof PieChart))
            );
        return control;
    }

    private DisplayResult toSingleSeriesChartDisplayResult(DisplayResult rs, boolean pie) {
        DisplayResult result = null;
        if (rs != null) {
            int rowCount = rs.getRowCount();
            int colCount = rs.getColumnCount();
            // Searching the value column where to extract figures of the series => simply choosing the first column where type is numeric
            int valueCol = colCount - 1; // in case it's not found for any reason, we take the last column by default
            for (int col = 0; col < colCount; col++) {
                DisplayColumn column = rs.getColumns()[col];
                Type type = column.getType();
                boolean isNumber = Types.isNumberType(type);
                // If not a number, it may be a formatted number (ex: Price), so checking if the source is an expression column with a numeric type
                if (!isNumber && !Types.isArrayType(type) /*to ignore columns such as family, site, item*/ && column.getSource() instanceof ExpressionColumn) {
                    type = ((ExpressionColumn) column.getSource()).getExpression().getType();
                    isNumber = Types.isNumberType(type);
                }
                if (isNumber) {
                    valueCol = col;
                    break;
                }
            }
            DisplayResultBuilder rsb = new DisplayResultBuilder(rowCount, new DisplayColumn[]{new DisplayColumnBuilder(null, PrimType.STRING).setRole(pie ? "series" : null).build(), DisplayColumn.create(null, PrimType.INTEGER)});
            for (int row = 0; row < rowCount; row++) {
                // Generating the series name by concatenating text of all columns preceding the value column
                StringBuilder sb = new StringBuilder();
                for (int col = 0; col < valueCol; col++)
                    appendTextOnly(rs.getValue(row, col), sb);
                rsb.setValue(row, 0, sb.toString());
                // Generating the series value
                Object value = rs.getValue(row, valueCol);
                if (value != null && !(value instanceof Number)) // Ex: formatted price
                    value = Numbers.doubleValue(value); // => extracting a double value
                rsb.setValue(row, 1, value);
            }
            result = rsb.build();
        }
        return result;
    }

    private void updateSelectedGroupCondition() {
        E group = getSelectedGroup();
        String sf = null;
        String gsf = getGroupStringFilter();
        if (group != null && gsf != null) {
            StringBuilder sb = new StringBuilder();
            ThreadLocalReferenceResolver.executeCodeInvolvingReferenceResolver(() -> {
                ExpressionArray<Expression> columnsExpressionArray = group.getDomainClass().parseExpressionArray(new StringFilter(gsf).getColumns());
                for (Expression columnExpression : columnsExpressionArray.getExpressions()) {
                    if (isAggregateExpression(columnExpression))
                        continue;
                    if (sb.length() > 0)
                        sb.append(" and ");
                    Object value = group.evaluate(columnExpression);
                    if (value instanceof EntityId)
                        value = ((EntityId) value).getPrimaryKey();
                    if (value instanceof String)
                        value = "'" + value + "'";
                    if (columnExpression instanceof As)
                        columnExpression = ((As) columnExpression).getOperand();
                    sb.append(columnExpression).append('=').append(value);
                }
            }, referenceResolver);
            sf = "{where: `" + sb + "`}";
        }
        selectedGroupConditionStringFilterProperty().set(sf);
    }

    private boolean isAggregateExpression(Expression expression) {
        if (expression instanceof As)
            expression = ((As) expression).getOperand();
        if (expression instanceof Call)
            switch (((Call) expression).getFunctionName()) {
                case "count":
                case "sum":
                    return true;
        }
        return false;
    }

    private static void appendTextOnly(Object value, StringBuilder sb) {
        if (value == null)
            return;
        if (value instanceof Object[]) {
            for (Object v : (Object[]) value)
                appendTextOnly(v, sb);
        } else {
            String text = value.toString();
            if (text != null && !text.isEmpty() && !text.startsWith("images/")) {
                if (sb.length() > 0)
                    sb.append(' ');
                sb.append(text);
            }
        }
    }
}