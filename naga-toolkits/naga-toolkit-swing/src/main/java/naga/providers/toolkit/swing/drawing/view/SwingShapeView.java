package naga.providers.toolkit.swing.drawing.view;

import naga.toolkit.drawing.paint.Paint;
import naga.toolkit.drawing.shapes.Shape;
import naga.toolkit.drawing.shapes.StrokeLineCap;
import naga.toolkit.drawing.shapes.StrokeLineJoin;
import naga.toolkit.drawing.spi.view.base.ShapeViewBase;
import naga.toolkit.drawing.spi.view.base.ShapeViewMixin;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * @author Bruno Salmon
 */
abstract class SwingShapeView
        <D extends Shape, DV extends ShapeViewBase<D, DV, DM>, DM extends ShapeViewMixin<D, DV, DM>>

        extends SwingDrawableView<D, DV, DM>
        implements ShapeViewMixin<D, DV, DM> {

    private final SwingPaintUpdater swingPaintUpdater = new SwingPaintUpdater();
    private final SwingStrokeUpdater swingStrokeUpdater = new SwingStrokeUpdater();
    private java.awt.Shape swingShape;

    SwingShapeView(DV base) {
        super(base);
    }

    @Override
    public void updateFill(Paint fill) {
        updatePaint();
    }

    @Override
    public void updateSmooth(Boolean smooth) {
    }

    @Override
    public void updateStroke(Paint stroke) {
        updateStroke();
    }

    @Override
    public void updateStrokeWidth(Double strokeWidth) {
        updateStroke();
    }

    @Override
    public void updateStrokeLineCap(StrokeLineCap strokeLineCap) {
        updateStroke();
    }

    @Override
    public void updateStrokeLineJoin(StrokeLineJoin strokeLineJoin) {
        updateStroke();
    }

    @Override
    public void updateStrokeMiterLimit(Double strokeMiterLimit) {
        updateStroke();
    }

    @Override
    public void updateStrokeDashOffset(Double strokeDashOffset) {
        updateStroke();
    }

    @Override
    public void updateStrokeDashArray(List<Double> dashArray) {
        updateStroke();
    }

    private void updatePaint() {
        swingPaintUpdater.updateFromShape(getDrawable());
    }

    private void updateStroke() {
        swingStrokeUpdater.updateFromShape(getDrawable());
    }

    protected void updateSwingShape() {
        swingShape = null;
    }

    private java.awt.Shape getOrCreateSwingShape(Graphics2D g) {
        if (swingShape == null)
            swingShape =  createSwingShape(g);
        return swingShape;
    }

    @Override
    public void paint(Graphics2D g) {
        paintSwingShape(g);
    }

    protected abstract java.awt.Shape createSwingShape(Graphics2D g);

    public void prepareCanvasContext(Graphics2D g) {
        super.prepareCanvasContext(g);
        D drawable = getDrawable();
        boolean smooth = drawable.isSmooth();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, smooth ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, smooth ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    void paintSwingShape(Graphics2D g) {
        Double width = null, height = null;
        if (swingStrokeUpdater.getSwingStroke() != null) {
            Rectangle2D bounds2D = getOrCreateSwingShape(g).getBounds2D();
            width = bounds2D.getWidth();
            height = bounds2D.getHeight();
        }
        paintSwingShape(width, height, g);
    }

    void paintSwingShape(Double width, Double height, Graphics2D g) {
        swingPaintUpdater.updateProportionalGradient(width, height);
        g.setPaint(swingPaintUpdater.getSwingPaint());
        g.fill(getOrCreateSwingShape(g));
        if (swingStrokeUpdater.getSwingStroke() != null) {
            g.setStroke(swingStrokeUpdater.getSwingStroke());
            swingStrokeUpdater.updateProportionalGradient(width, height);
            g.setPaint(swingStrokeUpdater.getSwingPaint());
            g.draw(swingShape);
        }
    }

}