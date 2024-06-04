package www.experthere.in.helper.app;

import android.util.Base64;

public class Base64Util {

    // Method to encode a string
    public static String encode(String input) {
        // Convert input string to byte array
        byte[] data = input.getBytes();
        // Encode the byte array to a Base64 string
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    // Method to decode a string
    public static String decode(String encodedString) {
        // Decode the Base64 string to a byte array
        byte[] data = Base64.decode(encodedString, Base64.DEFAULT);
        // Convert the byte array to a string
        return new String(data);
    }
}
