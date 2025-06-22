package ja2.person.editor.wildfire.core.fields;

import java.util.Hashtable;

public class ChoiceField implements Field {
    private final Field baseField;
    private final Hashtable<Object, Object> table;

    public ChoiceField(Field baseField, Hashtable<Object, Object> table) {
        this.baseField = baseField;
        this.table = table;
    }

    public String get(byte[] data) {
        int value = this.baseField.getInt(data);
        return (String) this.table.get(value);
    }

    public int getInt(byte[] data) {
        return this.baseField.getInt(data);
    }

    public void set(byte[] data, String str) throws NumberFormatException {
        Integer val = (Integer) this.table.get(str);
        if (val == null) {
            return;
        }
        int value = val;
        this.setInt(data, value);
    }

    public void setInt(byte[] data, int value) throws NumberFormatException {
        this.baseField.setInt(data, value);
    }
}
    
