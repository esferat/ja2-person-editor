package ja2.person.editor.wildfire.gui.view;

import ja2.person.editor.wildfire.data.DataChangeEvent;
import ja2.person.editor.wildfire.data.DataChangeListener;
import ja2.person.editor.wildfire.data.DataChangeMixin;
import ja2.person.editor.wildfire.core.structures.Structure;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class ChoiceView extends Choice implements FieldView, ItemListener {
    private final String fieldName;
    // Data Change event processing
    private final DataChangeMixin mixin = new DataChangeMixin();
    private boolean suppressEvents = false;
    private boolean modified = false;
    private String oldValue;
    private Structure struct;

    public ChoiceView(String fieldName, Vector<String> choices) {
        super();
        this.fieldName = fieldName;
        for (int idx = 0; idx < choices.size(); ++idx) {
            this.add(choices.elementAt(idx));
        }
        this.addItemListener(this);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public boolean isModified() {
        return this.modified;
    }

    public String getValue() {
        return this.getSelectedItem();
    }

    public void setStruct(Structure struct) {
        this.struct = struct;
    }

    public void itemStateChanged(ItemEvent event) {
        // Ignore programmatic changes
        if (this.suppressEvents) {
            return;
        }

        // Get new value
        String newValue = this.getSelectedItem();

        // Attempt to save value
        this.commit(newValue);

        // Success. Save new value and notify listeners
        this.oldValue = newValue;
    }

    public void commit(String newValue) {
        // Look for the all structs with named field
        if (this.struct != null) {
            String oldValue = struct.get(this.fieldName);
            if ((oldValue == null && (newValue != null)) ||
                    (oldValue != null && (!oldValue.equals(newValue)))) {
                this.modified = true;
                struct.set(this.fieldName, newValue);
                this.fireDataChangeEvent(new DataChangeEvent(this, this.oldValue, newValue));
            }
        }
    }

    public synchronized void refresh() {
        try {
            this.suppressEvents = true;

            // Look for the first struct with named field
            String value = null;
            if (this.struct != null) {
                value = struct.get(this.fieldName);
            }

            // Update gui
            if (value != null) {
                this.select(value);
            } else {
                this.select(0);
            }
            this.oldValue = value;

            // Update state machine
            this.modified = false;
        } finally {
            this.suppressEvents = false;
        }
    }

    public void addDataChangeListener(DataChangeListener l) {
        this.mixin.addDataChangeListener(l);
    }

    public void removeDataChangeListener(DataChangeListener l) {
        this.mixin.removeDataChangeListener(l);
    }

    public void fireDataChangeEvent(DataChangeEvent e) {
        this.mixin.fireDataChangeEvent(e);
    }
}
