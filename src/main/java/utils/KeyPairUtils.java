package utils;

import Core.Context;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyPairUtils {

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
