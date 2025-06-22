package ja2.person.editor.wildfire.core.fields;

public interface Field {
    String get(byte[] data);

    int getInt(byte[] data);

    void set(byte[] data, String value);

    void setInt(byte[] data, int value);
}
