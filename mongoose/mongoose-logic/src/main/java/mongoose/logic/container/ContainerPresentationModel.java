package mongoose.logic.container;

import naga.core.ngui.presentation.PresentationModel;
import naga.core.spi.toolkit.event.ActionEvent;
import rx.subjects.BehaviorSubject;

/**
 * @author Bruno Salmon
 */
class ContainerPresentationModel implements PresentationModel {

    // Display input

    private final BehaviorSubject<ActionEvent> organizationButtonActionEventObservable = BehaviorSubject.create();
    BehaviorSubject<ActionEvent> organizationButtonActionEventObservable() { return organizationButtonActionEventObservable; }

    private final BehaviorSubject<ActionEvent> cartButtonActionEventObservable = BehaviorSubject.create();
    BehaviorSubject<ActionEvent> carButtonActionEventObservable() { return cartButtonActionEventObservable; }

}
