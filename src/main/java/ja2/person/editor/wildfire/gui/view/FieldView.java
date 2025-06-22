package ja2.person.editor.wildfire.gui.view;

import ja2.person.editor.wildfire.core.structures.Structure;
import ja2.person.editor.wildfire.data.DataChangeEvent;
import ja2.person.editor.wildfire.data.DataChangeListener;

public interface FieldView {
    String getFieldName();

    String getValue();

    boolean isModified();

    void setStruct(Structure struct);

    void refresh();

    void addDataChangeListener(DataChangeListener l);

    void removeDataChangeListener(DataChangeListener l);

    void fireDataChangeEvent(DataChangeEvent e);
}
