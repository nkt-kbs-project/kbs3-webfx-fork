package mongoose.i18n;

import naga.framework.operation.i18n.ChangeLanguageRequest;
import naga.framework.ui.i18n.I18n;

/**
 * @author Bruno Salmon
 */
public final class EnglishLanguageRequest extends ChangeLanguageRequest {

    public EnglishLanguageRequest(I18n i18n) {
        super("en", i18n);
    }
}