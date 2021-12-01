package pt.iscte.iul;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Oleksandr Kobelyuk.
 */
public class Encoding {
    /**
     * Encodes the given string into base64.
     *
     * @param in Input string.
     * @return Encoded string.
     */
    public static String encode(String in) {
        return new String(Base64.encodeBase64(in.getBytes()));
    }

    /**
     * Decodes the given base64 string.
     *
     * @param in Input string.
     * @return Decoded string.
     */
    public static String decode(String in) {
        return new String(Base64.decodeBase64(in.getBytes()));
    }
}
