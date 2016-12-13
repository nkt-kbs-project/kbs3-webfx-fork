package naga.toolkit.fx.ext.cell.renderer;

import naga.commons.util.Arrays;
import naga.toolkit.fx.ext.cell.collator.NodeCollatorRegistry;
import naga.toolkit.fx.scene.Node;
import naga.toolkit.fx.scene.image.ImageView;
import naga.toolkit.fx.scene.text.Text;

/**
 * @author Bruno Salmon
 */
public class ImageTextRenderer implements ValueRenderer {

    public static ImageTextRenderer SINGLETON = new ImageTextRenderer();

    private ImageTextRenderer() {}

    @Override
    public Node renderCellValue(Object value) {
        Object[] array = getAndCheckArray(value);
        return array == null ? null : NodeCollatorRegistry.hBoxCollator().collateNodes(getImage(array), getTextNode(array));
    }

    public Object[] getAndCheckArray(Object value) {
        Object[] array = null;
        if (value instanceof Object[]) {
            array = (Object[]) value;
            if (Arrays.length(array) != 2)
                array = null;
        }
        return array;
    }

    private String getImageUrl(Object[] array) {
        return Arrays.getString(array, 0);
    }

    public ImageView getImage(Object[] array) {
        String imageUrl = getImageUrl(array);
        return ImageRenderer.SINGLETON.renderCellValue(imageUrl);
    }

    public String getText(Object[] array) {
        return Arrays.getString(array, 1);
    }

    private Text getTextNode(Object[] array) {
        String text = getText(array);
        return TextRenderer.SINGLETON.renderCellValue(text);
    }
}