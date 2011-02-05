package net.sf.timecult.ui.swt;

import net.sf.timecult.ResourceHelper;
import net.sf.timecult.TimeTracker;
import net.sf.timecult.model.DescriptionHolder;
import net.sf.timecult.model.Workspace;
import net.sf.timecult.model.WorkspaceEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

public class SWTDetailsView {
	
	public SWTDetailsView(SWTMainWindow mainWindow) {
		_mainWindow = mainWindow;
		setup();
	}
	
	
	private void setup() {
        TabItem detailsTab = new TabItem(_mainWindow.getMainTabFolder()
                .getTabs(), SWT.BORDER);
        detailsTab.setText(ResourceHelper.getString("tab.notes"));
        _descriptionText = new Text(detailsTab.getParent(), 
                SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        _descriptionText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent evt) {
                updateText();
            }
        });
        detailsTab.setControl(_descriptionText);
    }
	
	public void updateOnSelection(Object selection) {
        if (selection instanceof DescriptionHolder) {
            _holder = (DescriptionHolder) selection;
            updateDescription(_holder.getDescription());
            _descriptionText.setEnabled(true);
        }
        else {
            updateDescription("");
            _descriptionText.setEnabled(false);
        }
    }
	
	private void updateDescription(String description) {
		_descriptionText.setText(description);
	}
	
	
	private void updateText() {
		String changedDescription = _descriptionText.getText();
		if (_holder != null && _holder.getDescription() != null
				&& !_holder.getDescription().equals(changedDescription)) {
			_holder.setDescription(changedDescription);
			Workspace ws = TimeTracker.getInstance().getWorkspace();
			ws.fireWorkspaceChanged(new WorkspaceEvent(
					WorkspaceEvent.NOTES_UPDATED, _holder));
		}
	}
	
	
	private SWTMainWindow _mainWindow = null;
	private DescriptionHolder _holder = null;
	private Text _descriptionText = null;

}
