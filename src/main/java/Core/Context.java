package Core;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.security.*;
import java.security.spec.*;
import java.util.HashMap;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import static utils.KeyPairUtils.bytesToHex;

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
        String publicKey = bytesToHex(keyPair.getPublic().getEncoded());
        Wallet wallet = new Wallet()
                        .setBalance(0)
                        .setNonce(0)
                        .setTransactions(new ArrayList<>())
                        .setPublicKey(publicKey)
                        .setState("Active");
        Context.wallets.put(publicKey, wallet);
        return keyPair;
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

    public static void showWalletInfo(String publicKey) {
        wallets.get(publicKey).showInfo();
    }

    public static void showWallets() {
        for (Wallet wallet : wallets.values()) {
            wallet.showInfo();
        }
    }
}
