package mongoose.activities.bothends.logic.ui.highlevelcomponents;

import mongoose.activities.bothends.logic.ui.highlevelcomponents.impl.HighLevelComponentsFactoryImpl;
import naga.framework.ui.i18n.I18n;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

/**
 * @author Bruno Salmon
 */
public class HighLevelComponents {

    private static HighLevelComponentsFactory builder;

    public static void register(HighLevelComponentsFactory builder) {
        HighLevelComponents.builder = builder;
    }

    public static HighLevelComponentsFactory getBuilder() {
        if (builder == null)
            register(new HighLevelComponentsFactoryImpl());
        return builder;
    }

    public static BorderPane createSectionPanel(SectionPanelStyleOptions options) {
        return getBuilder().createSectionPanel(options);
    }

    public static BorderPane createSectionPanel(SectionPanelStyleOptions options, String iconImageUrl, String translationKey, I18n i18n) {
        return getBuilder().createSectionPanel(options, iconImageUrl, translationKey, i18n);
    }

    public static BorderPane createSectionPanel(SectionPanelStyleOptions options, Node... headerNodes) {
        return getBuilder().createSectionPanel(options, headerNodes);
    }

    public static Node createBadge(Node... badgeNodes) {
        return getBuilder().createBadge(badgeNodes);
    }

    public static Button createBookButton() {
        return getBuilder().createBookButton();
    }

    public static Button createSoldoutButton() {
        return getBuilder().createSoldoutButton();
    }
}