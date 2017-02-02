package mongoose.activities.backend.book.event.shared;

import javafx.scene.Node;
import mongoose.activities.shared.book.event.shared.BookingCalendar;
import mongoose.activities.shared.logic.calendar.CalendarTimeline;
import mongoose.activities.shared.logic.ui.calendargraphic.CalendarClickEvent;
import mongoose.activities.shared.logic.work.WorkingDocumentLine;
import mongoose.entities.Option;
import naga.framework.orm.entity.UpdateStore;
import naga.framework.ui.i18n.I18n;

/**
 * @author Bruno Salmon
 */
public class EditableBookingCalendar extends BookingCalendar {

    private final Node parentOwner;
    private boolean editMode;

    public EditableBookingCalendar(boolean amendable, I18n i18n, Node parentOwner) {
        super(amendable, i18n);
        this.parentOwner = parentOwner;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    @Override
    protected void onCalendarClick(CalendarClickEvent event) {
        if (!isEditMode())
            super.onCalendarClick(event);
        else {
            CalendarTimeline calendarTimeline = event.getCalendarTimeline();
            Option option = getCalendarTimelineOption(calendarTimeline);
            if (option != null) {
                DayTimeRangeEditor.showDayTimeRangeEditorDialog(calendarTimeline.getDayTimeRange(),
                        event.getCalendarCell().getEpochDay(),
                        calendarTimeline,
                        (newDayTimeRange, dialogCallback) -> {
                            // Creating an update store
                            UpdateStore store = UpdateStore.create(workingDocument.getEventService().getEventDataSourceModel());
                            // Creating an instance of Option entity
                            Option updatingOption = store.getOrCreateEntity(option.getId());
                            // Updating the option time range
                            updatingOption.setTimeRange(newDayTimeRange.getText());
                            // Asking the update record this change in the database
                            store.executeUpdate().setHandler(asyncResult -> {
                                if (asyncResult.failed())
                                    dialogCallback.showException(asyncResult.cause());
                                else {
                                    dialogCallback.closeDialog();
                                    // Updating the UI
                                    option.setTimeRange(newDayTimeRange.getText());
                                    createOrUpdateCalendarGraphicFromWorkingDocument(workingDocument);
                                }
                            });
                        }, parentOwner, i18n
                );
            }
        }
    }

    private static Option getCalendarTimelineOption(CalendarTimeline calendarTimeline) {
        Object source = calendarTimeline.getSource();
        if (source instanceof Option)
            return (Option) source;
        if (source instanceof WorkingDocumentLine)
            return ((WorkingDocumentLine) source).getOption();
        return null;
    }

}