package mongoose.activities.backend.event.clone;

import naga.framework.activity.combinations.domainpresentation.impl.DomainPresentationActivityImpl;

/**
 * @author Bruno Salmon
 */
public class CloneEventPresentationActivity extends DomainPresentationActivityImpl<CloneEventPresentationModel> {

    public CloneEventPresentationActivity() {
        super(CloneEventPresentationViewActivity::new, CloneEventPresentationLogicActivity::new);
    }
}