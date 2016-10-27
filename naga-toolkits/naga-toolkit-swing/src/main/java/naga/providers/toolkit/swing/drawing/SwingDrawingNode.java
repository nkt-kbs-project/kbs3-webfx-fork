package naga.providers.toolkit.swing.drawing;

import naga.providers.toolkit.swing.drawing.view.SwingShapeView;
import naga.providers.toolkit.swing.nodes.SwingNode;
import naga.toolkit.drawing.shapes.Shape;
import naga.toolkit.drawing.shapes.ShapeParent;
import naga.toolkit.drawing.spi.Drawing;
import naga.toolkit.drawing.spi.DrawingMixin;
import naga.toolkit.drawing.spi.DrawingNode;
import naga.toolkit.drawing.spi.impl.DrawingImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Collection;

/**
 * @author Bruno Salmon
 */
public class SwingDrawingNode extends SwingNode<SwingDrawingNode.DrawingPanel> implements DrawingNode<SwingDrawingNode.DrawingPanel>, DrawingMixin {

    public SwingDrawingNode() {
        this(new DrawingPanel());
    }

    private SwingDrawingNode(DrawingPanel node) {
        super(node);
    }

    @Override
    public Drawing getDrawing() {
        return node.drawing;
    }

    static class DrawingPanel extends JPanel {

        private final DrawingImpl drawing = new DrawingImpl(SwingShapeViewFactory.SINGLETON) {
            @Override
            protected void onShapeRepaintRequested(Shape shape) {
                repaint();
            }
        };


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintShapes(drawing.getChildrenShapes(), (Graphics2D) g);
        }

        private void paintShapes(Collection<Shape> shapes, Graphics2D g) {
            AffineTransform parentTransform = g.getTransform();
            for (Shape shape : shapes) {
                g.setTransform(parentTransform); // Resetting the graphics to the initial transform (ex: x,y to 0,0)
                paintShape(shape, g);
            }
        }

        private void paintShape(Shape shape, Graphics2D g) {
            SwingShapeView shapeView = (SwingShapeView) drawing.getOrCreateAndBindShapeView(shape);
            shapeView.paintShape(g);
            if (shape instanceof ShapeParent)
                paintShapes(((ShapeParent) shape).getChildrenShapes(), g);
        }
    }
}
