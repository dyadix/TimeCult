package net.sf.timecult.ui.swt;

public class MissingSelectionObjectException extends Exception {

    public MissingSelectionObjectException(Object o) {
        super(o.toString());
    }
}
