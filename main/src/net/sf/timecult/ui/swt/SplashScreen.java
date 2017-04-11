package net.sf.timecult.ui.swt;

import net.sf.timecult.ResourceHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SplashScreen {
    private final int MIN_DISPLAY_TIME = 2000;

    public SplashScreen(Display display) {
        _display = display;
    }

    public void open() {
        _shell = new Shell(_display, SWT.ON_TOP);
        setup(_shell);
        _shell.pack();
        SWTMainWindow.centerShell(_shell);
        _shell.open();
        _display.asyncExec(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(MIN_DISPLAY_TIME);
                } catch (InterruptedException e) {
                    // ignore
                }
                if (isOpen()) close();
            }
        });
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
    }
    
    public void close() {
        if (!_shell.isDisposed() && _shell.isVisible()) {
            _shell.close();
        }
    }
    
    public boolean isOpen() {
        return _shell != null && _shell.isVisible();
    }

    private Display _display;

    private Shell _shell;
}
