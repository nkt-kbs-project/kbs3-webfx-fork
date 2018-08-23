package naga.platform.services.json.spi;

import naga.platform.services.json.JsonFormatter;
import naga.platform.services.json.JsonParser;
import naga.platform.services.json.WritableJsonArray;
import naga.platform.services.json.WritableJsonObject;

/**
 * @author Bruno Salmon
 */
public interface JsonProvider extends JsonParser, JsonFormatter {

    /**
     * Create an empty native object.
     * @return a new empty native object
     */
    Object createNativeObject();

    /**
     * Create an empty native array.
     * @return a new empty native array
     */
    Object createNativeArray();

    /**
     * Create an empty json object.
     * @return a new empty json object
     */
    default WritableJsonObject createJsonObject() {
        return nativeToJavaJsonObject(createNativeObject());
    }

    /**
     * Create an empty json array.
     * @return a new empty json array
     */
    default WritableJsonArray createJsonArray() {
        return nativeToJavaJsonArray(createNativeArray());
    }

}