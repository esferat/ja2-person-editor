package ja2.person.editor.wildfire.data;

import java.util.Vector;

public class DataChangeMixin {
    // Instance data
    private final Vector<DataChangeListener> listeners = new Vector<>(1);

    // Instance methods
    public synchronized void addDataChangeListener(DataChangeListener l) {
        if (l == null) {
            return;
        }
        this.listeners.addElement(l);
    }

    public synchronized void removeDataChangeListener(DataChangeListener l) {
        if (l == null) {
            return;
        }
        this.listeners.removeElement(l);
    }

    public void fireDataChangeEvent(DataChangeEvent event) {
        int size = this.listeners.size();
        for (int idx = 0; idx < size; ++idx) {
            this.listeners.elementAt(idx).dataChanged(event);
        }
    }
}
