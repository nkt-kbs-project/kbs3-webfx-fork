package emul.javafx.scene.shape;

import emul.javafx.beans.property.Property;
import emul.javafx.beans.property.SimpleObjectProperty;
import emul.com.sun.javafx.geom.BaseBounds;
import emul.com.sun.javafx.geom.transform.BaseTransform;
import emul.javafx.scene.paint.Paint;
import webfx.fxkit.mapper.spi.impl.peer.markers.*;

/**
 * @author Bruno Salmon
 */
public class Rectangle extends Shape implements
        HasXProperty,
        HasYProperty,
        HasWidthProperty,
        HasHeightProperty,
        HasArcWidthProperty,
        HasArcHeightProperty {

    public Rectangle() {
    }

    public Rectangle(double width, double height) {
        this(width, height, null);
    }

    public Rectangle(double width, double height, Paint fill) {
        this(0, 0, width, height, fill);
    }

    public Rectangle(double x, double y, double width, double height) {
        this(x, y, width, height, null);
    }

    public Rectangle(double x, double y, double width, double height, Paint fill) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        if (fill != null)
            setFill(fill);
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

    private final Property<Double> widthProperty = new SimpleObjectProperty<>(0d);
    @Override
    public Property<Double> widthProperty() {
        return widthProperty;
    }

    private final Property<Double> heightProperty = new SimpleObjectProperty<>(0d);
    @Override
    public Property<Double> heightProperty() {
        return heightProperty;
    }

    private final Property<Double> arcWidthProperty = new SimpleObjectProperty<>(0d);
    @Override
    public Property<Double> arcWidthProperty() {
        return arcWidthProperty;
    }

    private final Property<Double> arcHeightProperty = new SimpleObjectProperty<>(0d);
    @Override
    public Property<Double> arcHeightProperty() {
        return arcHeightProperty;
    }

    @Override
    public BaseBounds impl_computeGeomBounds(BaseBounds bounds, BaseTransform tx) {
        bounds.setBoundsAndSort(getX().floatValue(), getY().floatValue(), 0, (float) (getX() + getWidth()), (float) (getY() + getHeight()), 0);
        return bounds;
    }
}