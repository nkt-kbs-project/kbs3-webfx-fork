package naga.providers.platform.client.gwt.services.resource;

import com.google.gwt.resources.client.TextResource;

/**
 * @author Bruno Salmon
 */
public interface GwtBundle {

    TextResource getTextResource(String resourcePath);

}