package naga.toolkit.transform.impl;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import naga.toolkit.drawing.shapes.Point2D;
import naga.toolkit.transform.Transform;
import naga.toolkit.transform.Translate;

/**
 * @author Bruno Salmon
 */
public class TranslateImpl extends TransformImpl implements Translate {

    public TranslateImpl() {
    }

    public TranslateImpl(double x) {
        this(x, 0d);
    }

    public TranslateImpl(double x, double y) {
        setX(x);
        setY(y);
    }

    private final Property<Double> xProperty = new SimpleObjectProperty<>(0d);
    @Override
    public Property<Double> xProperty() {
        return xProperty;
    }

    private final Property<Double> yProperty = new SimpleObjectProperty<>(0d);
    @Override
    public Property<Double> yProperty() {
        return yProperty;
    }

    @Override
    public Point2D transform(double x, double y) {
        return new Point2D(x + getX(), y + getY());
    }

    @Override
    public Transform createInverse() {
        return new TranslateImpl(-getX(), -getY());
    }

    @Override
    protected Property[] propertiesInvalidatingCache() {
        return new Property[]{xProperty, yProperty};
    }
}
