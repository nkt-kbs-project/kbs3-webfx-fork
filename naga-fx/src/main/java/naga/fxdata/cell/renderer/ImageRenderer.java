package naga.fxdata.cell.renderer;

import naga.commons.util.Strings;
import javafx.scene.image.ImageView;

/**
 * @author Bruno Salmon
 */
class ImageRenderer implements ValueRenderer {

    public static ImageRenderer SINGLETON = new ImageRenderer();

    private ImageRenderer() {}

    @Override
    public ImageView renderCellValue(Object value) {
        return value == null ? new ImageView() : new ImageView(Strings.toString(value));
    }
}