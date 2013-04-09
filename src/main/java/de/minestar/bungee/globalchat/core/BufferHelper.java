package de.minestar.bungee.globalchat.core;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class BufferHelper {
    public static Charset UTF8 = Charset.forName("UTF-8");
    public static CharsetEncoder ENCODER = UTF8.newEncoder();
    public static CharsetDecoder DECODER = UTF8.newDecoder();

    public static void setString(ByteBuffer buffer, String string) {
        try {
            ByteBuffer stringBuffer = ENCODER.encode(CharBuffer.wrap(string));
            buffer.put(stringBuffer);
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        }
    }

    public static String getString(ByteBuffer buffer) {
        String data = "";
        try {
            data = DECODER.decode(buffer).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return data;
    }
}
