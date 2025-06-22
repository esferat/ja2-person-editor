package ja2.person.editor.wildfire.core.structures;

public interface Structure {
    void chain(Structure nextStructure);

    String get(String name);

    int getInt(String name);

    void set(String name, String value);

    void setInt(String name, int value);
}

