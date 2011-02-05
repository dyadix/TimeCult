package net.sf.timecult.ui.swt.calendar;

import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vafada.swtcalendar.SWTCalendar;
import org.vafada.swtcalendar.SWTCalendarEvent;
import org.vafada.swtcalendar.SWTCalendarListener;

import net.sf.timecult.ui.swt.SWTDialog;

public class CalendarDialog extends SWTDialog {
    
    private SWTCalendar swtCal;
    private ICalendarDialogListener listener;
    private Calendar calendar;
    private Text dateField;

    public CalendarDialog(Shell parent, Text dateField) {
        super(parent);
        this.dateField = dateField;
    }
    

    @Override
    protected Composite createContentPanel(Shell shell) {
        Composite contentPanel = new Composite(shell, SWT.None);
        GridLayout gl = new GridLayout();
        contentPanel.setLayout(gl);

        swtCal = new SWTCalendar(contentPanel);
        this.calendar = swtCal.getCalendar();

        swtCal.addSWTCalendarListener(new SWTCalendarListener() {
            public void dateChanged(SWTCalendarEvent evt) {
                CalendarDialog.this.calendar = evt.getCalendar();
            }
        });

        return contentPanel;
    }


    @Override
    protected String getTitle() {
        return "Calendar"; //TODO: Localize
    }


    @Override
    protected boolean handleOk() {
        if (this.listener != null) {
            this.listener.dateSelected(this.calendar, this.dateField);
        }
        return true;
    }
    
    
    public void setListener(ICalendarDialogListener listener) {
        this.listener = listener;
    }

}
