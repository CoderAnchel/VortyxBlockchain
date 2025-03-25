package Core;

import Core.Entities.Block;
import Core.Entities.Transaction;
import Core.Entities.Wallet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;

import Exceptions.WalletException;
import static utils.KeyPairUtils.bytesToHex;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import utils.KeyPairUtils;

public class Context {
    private static HashMap<String, Wallet> wallets = new HashMap<>();
    private static HashMap<String, Transaction> mempool = new HashMap<>();
    private static HashMap<String, Block> chain = new HashMap<>();
    private static int transactionRate = 10;
    private static int initialValue = 100;
    //private static int transactionPerBlock;

    public static void init() {
        Context.loadWalletsFromFile();
    }


    public static void addTransaction(String senderPublicKey, String reciverPublicKey,
                                String privateKey, double value, String data,double fee) throws Exception {
        Wallet sender = wallets.get(senderPublicKey);
        Wallet reciver = wallets.get(reciverPublicKey);
        if(sender == null) {
            throw new WalletException("Sender wallet dosen't exist!");
        }
        if(reciver == null) {
            throw new WalletException("Reciver wallet dosen't exist!");
        }
        if((sender.balance() - (value + fee)) < 0) {
            throw new WalletException("Sender wallet dosen't have enought founds!");
        }
        Transaction transaction = new Transaction();
        transaction.setSenderPublicKey(senderPublicKey);
        transaction.setReciverPublicKey(reciverPublicKey);
        transaction.setValue(value);
        transaction.setData(data);
        transaction.setFee(fee);
        transaction.setTimestamp(new Date());

        String dataToHash = transaction.senderPublicKey()
            + transaction.reciverPublicKey()
            + transaction.value()
            + transaction.data()
            + transaction.fee()
            + transaction.timestamp();

        String sha256hex = Hashing.sha256().hashString(dataToHash, StandardCharsets.UTF_8).toString();

        transaction.setHashID(sha256hex);

        try {
            byte[] TransactionSignature = signTransaction(getPrivateKeyFromString(privateKey), dataToHash);
            PublicKey clavePublica = getPublicKeyFromString(sender.publicKeyBase64());
            if (!verifySign(clavePublica, dataToHash, TransactionSignature)) {
                System.out.println("Invalid transaction signature!");
                throw new WalletException("Invalid transaction signature!");
            }
            System.out.println("Operation signed and verified!, moving to mempool");
            Context.mempool.put(transaction.HashID(), transaction);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private static PrivateKey getPrivateKeyFromString(String key) throws GeneralSecurityException {
        try {
            // Use Base64 decoding
            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("EC", "BC"); // Use Bouncy Castle provider
            return kf.generatePrivate(spec);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Base64 encoding: " + e.getMessage());
            throw e;
        } catch (GeneralSecurityException e) {
            System.err.println("Key generation error: " + e.getMessage());
            throw e;
        }
    }

    public static PublicKey getPublicKeyFromString(String key) throws GeneralSecurityException {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("EC", "BC");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public static byte[] signTransaction(PrivateKey privateKey, String transactionData) throws
            NoSuchAlgorithmException, InvalidKeyException, SignatureException,
            NoSuchProviderException {
        Signature sign = Signature.getInstance("SHA256withECDSA", "BC");
        sign.initSign(privateKey);
        sign.update(transactionData.getBytes(StandardCharsets.UTF_8)); // Specify charset
        return sign.sign();
    }

        // Método para verificar
        public static boolean verifySign(PublicKey clavePublica, String mensaje, byte[] firma) throws Exception {
            Signature verificador = Signature.getInstance("SHA256withECDSA", "BC"); // Explicitly use Bouncy Castle
            verificador.initVerify(clavePublica);
            verificador.update(mensaje.getBytes(StandardCharsets.UTF_8)); // Specify charset
            return verificador.verify(firma);
        }

    public static void loadWalletsFromFile() {
        Gson gson = new Gson();
        try(BufferedReader reader = new BufferedReader(new FileReader("data/wallets.json"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Wallet wallet = gson.fromJson(line, Wallet.class);
                wallets.put(wallet.publicKey(), wallet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static KeyPair createWallet() throws Exception {
        Security.addProvider(new BouncyCastleProvider()); // Ensure BC provider is added
        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        keyPairGenerator.initialize(ecSpec, secureRandom);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Use consistent encoding
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        Wallet wallet = new Wallet()
                .setBalance(0)
                .setNonce(0)
                .setTransactions(new ArrayList<>())
                .setPublicKey(KeyPairUtils.base64ToHex(publicKey))
                .setState("Active")
                .setPublicKeyBase64(publicKey);

        Context.wallets.put(publicKey, wallet);
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("data/wallets.json", true)) {
            gson.toJson(wallet, writer);
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyPair;
    }

    public static KeyPair createGenesisWallet() throws Exception {
        Security.addProvider(new BouncyCastleProvider()); // Ensure BC provider is added
        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
        ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");
        keyPairGenerator.initialize(ecSpec, secureRandom);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Use consistent encoding
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        Wallet wallet = new Wallet()
                .setBalance(100)
                .setNonce(0)
                .setTransactions(new ArrayList<>())
                .setPublicKey(KeyPairUtils.base64ToHex(publicKey))
                .setState("Active")
                .setPublicKeyBase64(publicKey);

        Context.wallets.put(publicKey, wallet);

        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("data/wallets.json", true)) {
            gson.toJson(wallet, writer);
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyPair;
    }

    public static int transactionRate() {
        return transactionRate;
    }

    public static void setTransactionRate(int transactionRate) {
        Context.transactionRate = transactionRate;
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

    public static void showMempool() {
        for (Transaction elem : Context.mempool.values()) {
            elem.showInfo();
        }
    }
}
