package pt.iscte.iul;

import org.apache.commons.codec.binary.Base64;

public class Encoding {
    public static String Encode(String in) {
        return new String(Base64.encodeBase64(in.getBytes()));
    }

    public static String Decode(String in) {
        return new String(Base64.decodeBase64(in.getBytes()));
    }
}
