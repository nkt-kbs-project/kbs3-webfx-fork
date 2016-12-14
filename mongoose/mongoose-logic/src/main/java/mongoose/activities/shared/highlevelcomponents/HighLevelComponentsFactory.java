package mongoose.activities.shared.highlevelcomponents;

import naga.framework.ui.i18n.I18n;
import naga.framework.ui.presentation.PresentationActivity;
import naga.toolkit.fx.ext.cell.collator.NodeCollatorRegistry;
import naga.toolkit.fx.scene.Node;
import naga.toolkit.fx.scene.control.Button;
import naga.toolkit.fx.scene.layout.BorderPane;

/**
 * @author Bruno Salmon
 */
public interface HighLevelComponentsFactory {

    BorderPane createSectionPanel(SectionPanelStyleOptions options);

    default BorderPane createSectionPanel(SectionPanelStyleOptions options, String iconImageUrl, String translationKey, I18n i18n) {
        return createSectionPanel(options, PresentationActivity.createImageView(iconImageUrl), PresentationActivity.createTextView(translationKey, i18n));
    }

    default BorderPane createSectionPanel(SectionPanelStyleOptions options, Node... headerNodes) {
        BorderPane panel = createSectionPanel(options);
        panel.setTop(NodeCollatorRegistry.hBoxCollator().collateNodes(headerNodes));
        return panel;
    }

    Node createBadge(Node... badgeNodes);

    Button createBookButton();

    Button createSoldoutButton();
}
