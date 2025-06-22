package ja2.person.editor.wildfire.data;

import java.awt.*;

public class DataChangeEvent extends AWTEvent {
    // Instance data
    private final Object oldValue;
    private final Object newValue;

    // Instance methods
    public DataChangeEvent(Object source, Object oldValue, Object newValue) {
        super(source, RESERVED_ID_MAX + 1);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getOldValue() {
        return (String) this.oldValue;
    }

    public String getNewValue() {
        return (String) this.newValue;
    }
}				     
