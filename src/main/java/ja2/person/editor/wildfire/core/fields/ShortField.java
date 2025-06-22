package ja2.person.editor.wildfire.core.fields;

public class ShortField implements Field {
    private final int offset;

    public ShortField(int offset) {
        this.offset = offset;
    }

    public String get(byte[] data) {
        return Integer.toString(this.getInt(data));
    }

    public int getInt(byte[] data) {
        return ((((int) data[this.offset]) & 0xFF) |
                ((((int) data[this.offset + 1]) & 0xFF) << 8));
    }

    public void set(byte[] data, String str) throws NumberFormatException {
        int value = Integer.parseInt(str);
        this.setInt(data, value);
    }

    public void setInt(byte[] data, int value) {
        data[this.offset] = (byte) (value & 0xFF);
        data[this.offset + 1] = (byte) ((value >>> 8) & 0xFF);
    }
}
    
