package Core;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.security.*;
import java.security.spec.*;
import java.util.HashMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Context {
    private static HashMap<String, Wallet> wallets = new HashMap<>();
    private static HashMap<String, Transaction> mempool = new HashMap<>();
    private static HashMap<String, Block> blocks = new HashMap<>();

    public static KeyPair createWallet() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
        keyPairGenerator.initialize(ecGenParameterSpec, secureRandom);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        String publicKey = Context.bytesToHex(keyPair.getPublic().getEncoded());
        Wallet wallet = new Wallet()
                        .setBalance(0)
                        .setNonce(0)
                        .setTransactions(new ArrayList<>())
                        .setPublicKey(publicKey)
                        .setState("Active");
        Context.wallets.put(publicKey, wallet);
        return keyPair;
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

    public HashMap<String, Wallet> wallets() {
        return wallets;
    }

    public void setWallets(HashMap<String, Wallet> wallets) {
        this.wallets = wallets;
    }

    public HashMap<String, Transaction> mempool() {
        return mempool;
    }

    public void setMempool(HashMap<String, Transaction> mempool) {
        this.mempool = mempool;
    }

    public static void showWallets() {
        for (Wallet wallet : wallets.values()) {
            wallet.showInfo();
        }
    }
}
