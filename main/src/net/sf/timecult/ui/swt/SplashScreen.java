package net.sf.timecult.ui.swt;

import net.sf.timecult.ResourceHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SplashScreen {

    public SplashScreen(Display display) {
        _display = display;        
    }

    public void open() {
        _shell = new Shell(_display, SWT.ON_TOP);
        setup(_shell);
        _shell.pack();
        SWTMainWindow.centerShell(_shell);
        _shell.open();
    }

    private void setup(Shell shell) {
        GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        gl.verticalSpacing = 0;
        shell.setLayout(gl);
        Image splash = new Image(shell.getDisplay(), ResourceHelper
            .openStream("images/splash.png"));
        Label splashLabel = new Label(shell, SWT.NULL);
        splashLabel.setImage(splash);
        
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.grabExcessHorizontalSpace = true;
        gd.heightHint = 20;
        Label infoLabel = new Label(shell, SWT.BORDER);
        infoLabel.setLayoutData(gd);
        infoLabel.setText("  Loading...");
    }
    
    public void close() {
        if (!_shell.isDisposed() && _shell.isVisible()) {
            _shell.close();
        }
    }
    
    public boolean isOpen() {
        if (_shell != null && _shell.isVisible()) {
            return true;
        }
        else {
            return false;
        }
    }

    private Display _display;

    private Shell _shell;
}
