package ja2.person.editor.wildfire.core.fields;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StringField implements Field {
    private final int offset;
    private final int length;

    public StringField(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    public String get(byte[] data) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.length / 2; ++i) {
            int b0 = byteBuffer.get(this.offset + i * 2);
            int b1 = byteBuffer.get(this.offset + i * 2 + 1);
            char c = (char)((b0 & 0xFF) | ((b1 & 0xFF) << 8));
            if (c == 0) {
                break;
            }
            sb.append(Character.toChars(c));
        }

        try {
            return new String(sb.toString().getBytes(StandardCharsets.ISO_8859_1), "windows-1251");
        } catch (UnsupportedEncodingException e) {
            return sb.toString();
        }
    }

    public int getInt(byte[] data) throws NumberFormatException {
        return Integer.parseInt(this.get(data));
    }

    public void set(byte[] data, String value) {
        for (int i = 0; i < this.length / 2; ++i) {
            char c = 0;
            if (i < value.length()) {
                c = value.charAt(i);
            }

            data[this.offset + i * 2] = (byte) (((int) c) & 0xFF);
            data[this.offset + i * 2 + 1] = (byte) ((((int) c) & 0xFF) >>> 8);
        }
    }

    public void setInt(byte[] data, int value) {
        this.set(data, Integer.toString(value));
    }
}
    
