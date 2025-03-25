package utils;

import Core.Context;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class KeyPairUtils {

    // Existing methods...

    /**
     * Converts Base64 encoded key to Hexadecimal representation
     * @param base64Key Base64 encoded key
     * @return Hexadecimal representation of the key
     */
    public static String base64ToHex(String base64Key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            return bytesToHex(keyBytes);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Base64 key: " + e.getMessage());
            return base64Key; // Return original string if conversion fails
        }
    }

    /**
     * Converts Hexadecimal key to Base64 representation
     * @param hexKey Hexadecimal key
     * @return Base64 encoded representation of the key
     */
    public static String hexToBase64(String hexKey) {
        try {
            byte[] keyBytes = hexToBytes(hexKey);
            return Base64.getEncoder().encodeToString(keyBytes);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Hex key: " + e.getMessage());
            return hexKey; // Return original string if conversion fails
        }
    }

    /**
     * Converts a hex string to byte array
     * @param hexString Hexadecimal string
     * @return byte array
     */
    private static byte[] hexToBytes(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return data;
    }

    public static void KeyPairAnalizer(KeyPair keyPair) throws Exception {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        byte[] privateKeyBytes = privateKey.getEncoded();
        byte[] publicKeyBytes = publicKey.getEncoded();
        String privateKeyHex = bytesToHex(privateKeyBytes);
        String publicKeyHex = bytesToHex(publicKeyBytes);
        System.out.println("clave publica: " + publicKeyHex);
        System.out.println("clave privada: " + privateKeyHex);
    }

    public static String getPublickey(KeyPair keyPair) throws Exception {
        PublicKey publicKey = keyPair.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        return bytesToHex(publicKeyBytes);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
