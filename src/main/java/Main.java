import Core.Context;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;

public class Main {
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        try {
            KeyPair wallet1 = Context.createWallet();
            KeyPairAnalizer(wallet1);
            System.out.println("-------------------");
            KeyPair wallet2 = Context.createWallet();
            KeyPairAnalizer(wallet2);

            Context.showWallets();

            LinkedList linkedList = new LinkedList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void KeyPairAnalizer(KeyPair keyPair) throws Exception {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        byte[] privateKeyBytes = privateKey.getEncoded();
        byte[] publicKeyBytes = publicKey.getEncoded();
        String privateKeyHex = Context.bytesToHex(privateKeyBytes);
        String publicKeyHex = Context.bytesToHex(publicKeyBytes);
        System.out.println("clave publica: " + publicKeyHex);
        System.out.println("clave privada: " + privateKeyHex);
    }
}
