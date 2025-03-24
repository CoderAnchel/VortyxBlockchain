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
import java.util.Date;
import java.util.HashMap;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;

import Exceptions.WalletException;
import static utils.KeyPairUtils.bytesToHex;

public class Context {
    private static HashMap<String, Wallet> wallets = new HashMap<>();
    private static HashMap<String, Transaction> mempool = new HashMap<>();
    private static HashMap<String, Block> chain = new HashMap<>();
    private static int transactionRate = 10;
    //private static int transactionPerBlock;

    public static void init() {
        Context.loadWalletsFromFile();
    }


    public void addTransaction(String senderPublicKey, String reciverPublicKey, String privateKey, double value, String data, String signature,double fee) throws Exception {
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
            PublicKey clavePublica = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(java.util.Base64.getDecoder().decode(senderPublicKey)));
            if (!verifySign(clavePublica, dataToHash, TransactionSignature)) {
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

    private PrivateKey getPrivateKeyFromString(String key) throws GeneralSecurityException {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return kf.generatePrivate(spec);
    }


    public byte[] signTransaction(PrivateKey clavePrivada, String transactionData) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature sign = Signature.getInstance("SHA256withECDSA");
        sign.initSign(clavePrivada);
        sign.update(transactionData.getBytes());
        return sign.sign();
    }

        // Método para verificar
    public boolean verifySign(PublicKey clavePublica, String mensaje, byte[] firma) throws Exception {
        Signature verificador = Signature.getInstance("SHA256withECDSA");
        verificador.initVerify(clavePublica);
        verificador.update(mensaje.getBytes());
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
        Gson gson = new Gson();
        try(FileWriter writer = new FileWriter("data/wallets.json", true)) {
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
