package mongoose.activities.backend.monitor;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import naga.framework.activity.base.elementals.presentation.view.impl.PresentationViewActivityImpl;
import naga.fxdata.chart.Chart;
import naga.fxdata.chart.LineChart;

import static naga.framework.ui.layouts.LayoutUtil.setVGrowable;

/**
 * @author Bruno Salmon
 */
final class MonitorPresentationViewActivity extends PresentationViewActivityImpl<MonitorPresentationModel> {

    private Chart memoryChart;
    private Chart cpuChart;

    @Override
    protected void createViewNodes(MonitorPresentationModel pm) {
        memoryChart = setVGrowable(new LineChart());
        cpuChart = setVGrowable(new LineChart());

        memoryChart.displayResultProperty().bind(pm.memoryDisplayResultProperty());
        cpuChart.displayResultProperty().bind(pm.cpuDisplayResultProperty());
    }

    @Override
    protected Node assemblyViewNodes() {
        return new VBox(memoryChart, cpuChart);
    }
}