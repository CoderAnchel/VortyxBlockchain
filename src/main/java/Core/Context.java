package Core;

import Core.Entities.Block;
import Core.Entities.MinerTrans;
import Core.Entities.Transaction;
import Core.Entities.Wallet;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.security.*;
import java.security.spec.*;

import app.DTOS.KeyPairWalletDTO;
import com.google.common.hash.Hashing;
import com.google.gson.Gson;

import Exceptions.WalletException;
import io.github.cdimascio.dotenv.Dotenv;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.util.encoders.Hex;
import utils.IterationUtils;
import utils.KeyPairUtils;

public class Context {
    private static HashMap<String, Wallet> wallets = new HashMap<>();
    private static HashMap<String, Transaction> mempool = new HashMap<>();
    private static HashMap<String, Transaction> confirmedTransactions = new HashMap<>();
    private static HashMap<String, Block> chain = new HashMap<>();
    private static int transactionRate = 10;
    private static int initialValue = 100;
    private static final String MINER_PUBLIC_KEY = Dotenv.load().get("PUBLIC_KEY");
    //private static int transactionPerBlock;
    private static BlockchainStorage blockchainStorage;
    private static int confNum = 15;

    public static void init() {
        Context.blockchainStorage = new BlockchainStorage("database/");
        Context.loadWalletsFromFile();
        Context.loadTransactionsMEEMPOOLFromFile();
        Context.loadBlocksFromFile();
        while(true) {
            boolean out = buildBlock();
            if (!out) {
                break;
            }
        }
    }

    public static boolean buildBlock() {

        String PRIVATE = Dotenv.load().get("PRIVATE_KEY");
        Wallet minerWallet = Context.blockchainStorage.getWallet(Context.MINER_PUBLIC_KEY);

        // Verificar tamaño del mempool primero
        int mempoolSize = Context.blockchainStorage.getDatabaseSize(BlockchainStorage.Types.MEMPOOL);

        if (minerWallet != null && Context.transactionRate <= mempoolSize) {
            Block block = new Block();
            block.setTimestamp(new Date());
            block.setMiner(Context.MINER_PUBLIC_KEY);
            block.setPreviousHash(""); // Inicializa previousHash con cadena vacía para el primer bloque
            List<Transaction> transactions =
                    Context.blockchainStorage.getSliceOfTransactions(Context.transactionRate - 1,
                            BlockchainStorage.Types.MEMPOOL);
            //deleting transactions from mempool
            Context.blockchainStorage.deleteEntities(IterationUtils.getTransKeys(transactions), BlockchainStorage.Types.MEMPOOL );
            // Use dynamic difficulty (adjustable)
            int difficulty = 4; // Can be made configurable
            boolean minedSuccessfully = mineBlock(block, difficulty);
            if (!minedSuccessfully) {
                System.out.println("Block mining failed.");
                Context.blockchainStorage.addTransactionsToMempool(transactions);
                return false;
            }

            MinerTrans transaction = new MinerTrans();
            transaction.setReciverPublicKey(MINER_PUBLIC_KEY);
            transaction.setValue(3.15);
            transaction.setData("BLOCK CREATED");
            transaction.setTimestamp(new Date());
            transaction.setBlockHash(block.getHash());
            transaction.setState("CONFIRMED");

            String dataToHash =  transaction.reciverPublicKey()
                    + transaction.value()
                    + transaction.data()
                    + transaction.timestamp();

            String sha256hex = Hashing.sha256().hashString(dataToHash, StandardCharsets.UTF_8).toString();

            transaction.setHashID(sha256hex);

            try {
                byte[] TransactionSignature = signTransaction(getPrivateKeyFromStringMiner(PRIVATE),
                        dataToHash);
                PublicKey clavePublica = getPublicKeyFromStringMiner(transaction.reciverPublicKey());
                if (!verifySign(clavePublica, dataToHash, TransactionSignature)) {
                    System.out.println("Invalid transaction signature!");
                    throw new WalletException("Invalid transaction signature!");
                }
                System.out.println("Operation signed and verified!, moving to mempool");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (NoSuchProviderException e) {
                throw new RuntimeException(e);
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            List<Transaction> validated = Context.liquidate(transactions, transaction);
            for (Transaction tx : validated) {
                tx.setBlockHash(block.getHash());
                tx.setState("CONFIRMED");
            }
            block.setTransactions(IterationUtils.getTransKeys(validated));
            block.setMerkleRoot(Context.calculateMerkleRoot(IterationUtils.getTransKeys(validated)));
            block.setPreviousHash(Context.blockchainStorage.getLastBlock().getHash());
            block.setPosition(Context.blockchainStorage.getDatabaseSize(BlockchainStorage.Types.BLOCKS) + 1);
            Context.blockchainStorage.addTransactionsToDefinitive(validated);
            Context.blockchainStorage.saveBlock(block);
            Context.confirmTransactions(block, Context.confNum);
            Context.blockchainStorage.showBlocks();
            return true;
        }
        return false;
    }

    public static void buildGenesisBlock() {
        String PRIVATE = Dotenv.load().get("PRIVATE_KEY");
        Wallet minerWallet = Context.blockchainStorage.getWallet(Context.MINER_PUBLIC_KEY);
            Block block = new Block();
            block.setTimestamp(new Date());
            block.setMiner(Context.MINER_PUBLIC_KEY);
            block.setPreviousHash(""); // Inicializa previousHash con cadena vacía para el primer bloque
            // Use dynamic difficulty (adjustable)
            int difficulty = 4; // Can be made configurable
            mineBlock(block, difficulty);

            MinerTrans transaction = new MinerTrans();
            transaction.setReciverPublicKey(MINER_PUBLIC_KEY);
            transaction.setValue(600);
            transaction.setData("BLOCK CREATED");
            transaction.setTimestamp(new Date());
            transaction.setBlockHash(block.getHash());
            transaction.setState("CONFIRMED");

            String dataToHash =  transaction.reciverPublicKey()
                    + transaction.value()
                    + transaction.data()
                    + transaction.timestamp();

            String sha256hex = Hashing.sha256().hashString(dataToHash, StandardCharsets.UTF_8).toString();

            transaction.setHashID(sha256hex);

            try {
                byte[] TransactionSignature = signTransaction(getPrivateKeyFromStringMiner(PRIVATE),
                        dataToHash);
                PublicKey clavePublica = getPublicKeyFromStringMiner(transaction.reciverPublicKey());
                if (!verifySign(clavePublica, dataToHash, TransactionSignature)) {
                    System.out.println("Invalid transaction signature!");
                    throw new WalletException("Invalid transaction signature!");
                }
                System.out.println("Operation signed and verified!, moving to mempool");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (NoSuchProviderException e) {
                throw new RuntimeException(e);
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            List<String> transactionsList = new ArrayList<>();
            transactionsList.add(transaction.HashID());
            Context.blockchainStorage.saveTransaction(transaction.toTransaction());
            block.setTransactions(transactionsList);
            block.setMerkleRoot(Context.calculateMerkleRoot(transactionsList));
            block.setPosition(Context.blockchainStorage.getDatabaseSize(BlockchainStorage.Types.BLOCKS) + 1);
            Context.liquidateGenesis(transaction);
            Context.blockchainStorage.saveBlock(block);
            Context.blockchainStorage.saveLastBlock(block);
            Context.blockchainStorage.showBlocks();
    }


    /**
     * this method is made to liquidate all the block transactions
     */
    public static List<Transaction> liquidate(List<Transaction> transactions, MinerTrans minerTrans) {
        List<Transaction> validated = new ArrayList<>();
        for (Transaction transaction : transactions) {
            Wallet senderWallet = Context.blockchainStorage.getWallet(transaction.senderPublicKey());
            Wallet reciverWallet = Context.blockchainStorage.getWallet(transaction.reciverPublicKey());
            if (senderWallet != null && reciverWallet != null && (senderWallet.balance() - (transaction.value() + transaction.fee())) >= 0) {
                senderWallet.setBalance(senderWallet.balance() - transaction.value());
                transaction.setNonce(senderWallet.Nonce());
                senderWallet.setNonce(senderWallet.Nonce() + 1);
                Context.blockchainStorage.saveWallet(senderWallet);
                System.out.println("Sender Wallet balance mod! info: !");
                Context.blockchainStorage.getWallet(transaction.senderPublicKey()).showInfo();
                reciverWallet.setBalance(reciverWallet.balance() + transaction.value());
                Context.blockchainStorage.saveWallet(reciverWallet);
                System.out.println("Reciver Wallet balance mod info: !");
                Context.blockchainStorage.getWallet(transaction.reciverPublicKey()).showInfo();
                validated.add(transaction);
            } else {
                System.out.println("We got problems out of cash!! moving back to mempool");
                // Context.blockchainStorage.saveTransactionMempoool(transaction);
            }
        }

        // Para la transacción del minero (ya funciona correctamente)
        Wallet minerWallet = Context.blockchainStorage.getWallet(minerTrans.reciverPublicKey());
        if (minerWallet != null) {
            minerWallet.setBalance(minerWallet.balance() + minerTrans.value());
            Context.blockchainStorage.saveWallet(minerWallet);
            System.out.println("Miner Wallet balance mod!");
            System.out.println("MINER WALLET UPDATED: ");
            validated.add(minerTrans.toTransaction());
            Context.getWalletFromLevel(minerWallet.publicKeyHex()).showInfo();
        }
        return validated;
    }


    public static void confirmTransactions(Block initialBlock, int confNum) {
        Block block = initialBlock;

        for (int i = 0; i < confNum && block != null; i++) {
            if (block.getPreviousHash() == null || block.getPreviousHash().isEmpty()) {
                break;
            }

            block = Context.blockchainStorage.getBlock(block.getPreviousHash());

            if (block == null) {
                break;
            }

            for (Transaction tx : Context.blockchainStorage.getTransactions(block.transactions())) {
                tx.setNumberOfComfirmations(tx.numberOfComfirmations() + 1);
                Context.blockchainStorage.saveTransaction(tx);
            }
        }
    }

    public static void liquidateGenesis(MinerTrans minerTrans) {
        // Para la transacción del minero (ya funciona correctamente)
        Wallet minerWallet = Context.blockchainStorage.getWallet(minerTrans.reciverPublicKey());
        if (minerWallet != null) {
            minerWallet.setBalance(minerWallet.balance() + minerTrans.value());
            Context.blockchainStorage.saveWallet(minerWallet);
            System.out.println("Miner Wallet balance mod!");
            System.out.println("MINER WALLET UPDATED: ");
            Context.getWalletFromLevel(minerWallet.publicKeyHex()).showInfo();
        }
    }

    /**
     * /THIS METHOD IS DEPRECATED!!!!!
     * @return
     * @throws WalletException
     */
    public static boolean CreateBlock() throws WalletException {
        if (mempool.size() >= Context.transactionRate && wallets.containsKey(MINER_PUBLIC_KEY)) {
            HashMap<String, Transaction> processingpool = new HashMap<>();
            Block block = new Block();
            int counter = 0;
           //miner signs is hown transaction for reciving the money
            String PRIVATE = Dotenv.load().get("PRIVATE_KEY");
            List<Transaction> transactionsToProcess = new ArrayList<>();
            for (Transaction transaction : mempool.values()) {
                if (counter < transactionRate) {
                    transactionsToProcess.add(transaction);
                    counter++;
                } else {
                    break;
                }
            }
            for (Transaction transaction : transactionsToProcess) {
                Transaction tras = mempool.remove(transaction.HashID());
                block.transactions().add(tras.HashID());
                processingpool.put(tras.HashID(), tras);
            }
            block.setTimestamp(new Date());
            block.setMerkleRoot(Context.calculateMerkleRoot(new ArrayList<>(processingpool.keySet())));
            block.setMiner(Context.MINER_PUBLIC_KEY);
            // Use dynamic difficulty (adjustable)
            int difficulty = 16; // Can be made configurable
            boolean minedSuccessfully = mineBlock(block, difficulty);

            if (!minedSuccessfully) {
                System.out.println("Block mining failed.");
                return false;
            }
            chain.put(block.getHash(), block);
            Gson gson = new Gson();
            try (FileWriter writer = new FileWriter("data/blocks.json", true)) {
                gson.toJson(block, writer);
                writer.write("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Set<String> processedTransactionHashes = new HashSet<>();
            List<Transaction> toDelete = new ArrayList<>();
            for (Transaction trans : processingpool.values()) {
                confirmedTransactions.put(trans.HashID(), trans);
                toDelete.add(trans);
                processedTransactionHashes.add(trans.HashID());
                try (FileWriter writer = new FileWriter("data/transaction_VALIDATED.json", true)) {
                    gson.toJson(trans, writer);
                    writer.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (Transaction trans : toDelete) {
                processingpool.remove(trans.HashID());
            }

            // Update mempool file, removing processed transactions
            updateMempoolFile(processedTransactionHashes);

            return true;
        }
        System.out.println("Miner Wallet dosen't exist or not enough Trans in pool!");
        return false;
    }

    public static boolean mineBlock(Block block, int difficulty) {
        // Start time for mining performance tracking
        long startTime = System.currentTimeMillis();

        // Difficulty prefix generation
        String difficultyPrefix = "0".repeat(difficulty);

        long nonce = 0;
        String sha256hex;

        while (true) {
            System.out.println("mining...");
            // Include nonce in the hash calculation to prevent caching
            sha256hex = Hashing.sha256()
                    .hashString(block.toString() + nonce, StandardCharsets.UTF_8)
                    .toString();

            // Performance and debug logging
            if (nonce % 100000 == 0) {
                System.out.printf("Mining... (Nonce: %d, Current Hash: %s)%n", nonce, sha256hex);
            }

            // Check if hash meets difficulty requirement
            if (sha256hex.startsWith(difficultyPrefix)) {
                System.out.printf("Block mined! ✅%n");
                System.out.printf("Nonce: %d%n", nonce);
                System.out.printf("Hash: %s%n", sha256hex);

                // Calculate and log mining time
                long miningTime = System.currentTimeMillis() - startTime;
                System.out.printf("Mining Time: %d ms%n", miningTime);

                block.setHash(sha256hex);
                block.setNonce((int)nonce);
                return true;
            }

            nonce++;

            // Optional: Add a timeout or max nonce to prevent infinite loops
            //if (nonce > Long.MAX_VALUE - 1000) {
              //  System.out.println("Max nonce reached. Resetting.");
                //return false;
            //}
        }
    }

    public static void updateMempoolFile(Set<String> processedTransactionHashes) {
        try {
            File inputFile = new File("data/transactions_MEEMPOOL.json");
            File tempFile = new File("data/transactions_MEEMPOOL_temp.json");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            Gson gson = new Gson();
            String line;

            while ((line = reader.readLine()) != null) {
                // Parse each line to check its hash
                Transaction transaction = gson.fromJson(line, Transaction.class);

                // Only write the line if it's not in the processed transactions
                if (!processedTransactionHashes.contains(transaction.HashID())) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            reader.close();
            writer.close();

            // Replace the original file
            if (inputFile.delete()) {
                if (!tempFile.renameTo(inputFile)) {
                    System.err.println("Could not rename temporary file");
                }
            } else {
                System.err.println("Could not delete original mempool file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para calcular hash SHA-256
    private static String calculateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));

            // Convertir a representación hexadecimal
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Método para construir el Merkle Root
    public static String calculateMerkleRoot(List<String> transactions) {
        // Si no hay transacciones, devolver hash nulo
        if (transactions == null || transactions.isEmpty()) {
            return "";
        }

        // Si solo hay una transacción, devolver su hash
        while (transactions.size() > 1) {
            List<String> newHashes = new ArrayList<>();

            // Hacer hash de pares de hashes
            for (int i = 0; i < transactions.size(); i += 2) {
                String hash1 = transactions.get(i);
                String hash2 = (i + 1 < transactions.size()) ? transactions.get(i + 1) : hash1;

                // Concatenar y hacer hash de los pares
                String combinedHash = calculateHash(hash1 + hash2);
                newHashes.add(combinedHash);
            }

            // Actualizar lista de hashes
            transactions = newHashes;
        }

        // Devolver el hash final (Merkle Root)
        return transactions.get(0);
    }

    // Método para verificar una transacción en el Merkle Tree
    public boolean verifyTransactionMerkle(List<String> transactions, String transaction,
                                      String merkleRoot) {
        // Calcular el Merkle Root actual
        String calculatedMerkleRoot = calculateMerkleRoot(transactions);

        // Comparar con el Merkle Root proporcionado
        return calculatedMerkleRoot.equals(merkleRoot);
    }

    public static void addTransaction(String senderPublicKey, String reciverPublicKey, String privateKey, double value, String data,double fee) throws Exception {
        System.out.println("Mempool size: "+Context.getDatabaseSize(BlockchainStorage.Types.MEMPOOL));
        System.out.println("Blocks size: "+Context.getDatabaseSize(BlockchainStorage.Types.BLOCKS));
        Wallet sender = Context.blockchainStorage.getWallet(senderPublicKey);
        Wallet reciver = Context.blockchainStorage.getWallet(reciverPublicKey);
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

        // Configurar claves públicas en formato Base64
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
            transaction.setState("Meempool");
            Context.blockchainStorage.saveTransactionMempoool(transaction);
            Context.buildBlock();
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

    private static PrivateKey getPrivateKeyFromStringMiner(String privateKeyString) {
        try {
            // Si es una transacción de minería y no hay clave privada real
            if (privateKeyString == null || privateKeyString.isEmpty()) {
                // Usar una clave privada específica para minería (puede ser la del sistema)
                privateKeyString = System.getenv("PRIVATE_KEY");
                if (privateKeyString == null || privateKeyString.isEmpty()) {
                    throw new RuntimeException("No se encontró una clave privada para la operación de minería");
                }
            }

            byte[] keyBytes = Hex.decode(privateKeyString);
            KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    public static PublicKey getPublicKeyFromStringMiner(String publicKeyStr) {
        try {
            // Comprobar si la clave ya está en formato hexadecimal
            if (publicKeyStr.startsWith("3056")) {
                // Convertir de hexadecimal a bytes
                byte[] publicKeyBytes = Hex.decode(publicKeyStr);
                KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
                return keyFactory.generatePublic(keySpec);
            } else {
                // Si no es hexadecimal, intentar decodificar como Base64
                byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
                KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
                return keyFactory.generatePublic(keySpec);
            }
        } catch (Exception e) {
            // En caso de error, intentar un método alternativo de decodificación
            try {
                byte[] publicKeyBytes = Hex.decode(publicKeyStr);
                KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");

                // Intentar extraer solo la parte relevante de la clave si hay datos adicionales
                int keyLength = 91; // Longitud estándar de clave EC codificada en X.509
                if (publicKeyBytes.length > keyLength) {
                    byte[] trimmedKeyBytes = new byte[keyLength];
                    System.arraycopy(publicKeyBytes, 0, trimmedKeyBytes, 0, keyLength);
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(trimmedKeyBytes);
                    return keyFactory.generatePublic(keySpec);
                } else {
                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
                    return keyFactory.generatePublic(keySpec);
                }
            } catch (Exception ex) {
                throw new RuntimeException("Error al procesar la clave pública: " + ex.getMessage(), ex);
            }
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

    public static void loadTransactionsMEEMPOOLFromFile() {
        Gson gson = new Gson();
        try(BufferedReader reader = new BufferedReader(new FileReader("data/blocks.json"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Block block = gson.fromJson(line, Block.class);
                chain.put(block.getHash(), block);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadBlocksFromFile() {
        Gson gson = new Gson();
        try(BufferedReader reader = new BufferedReader(new FileReader("data/transactions_MEEMPOOL.json"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Transaction transaction = gson.fromJson(line, Transaction.class);
                mempool.put(transaction.HashID(), transaction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Wallet getWalletDetails(String publicKey) {
        return Context.blockchainStorage.getWallet(publicKey);
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
        System.out.println("BASE64:" +publicKey);
        System.out.println("hesa:" +(KeyPairUtils.base64ToHex(publicKey)));
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        Wallet wallet = new Wallet()
                .setBalance(0)
                .setNonce(0)
                .setTransactions(new ArrayList<>())
                .setPublicKey(KeyPairUtils.base64ToHex(publicKey))
                .setState("Active")
                .setPublicKeyBase64(publicKey);

        Context.wallets.put(publicKey, wallet);
        Context.blockchainStorage.saveWallet(wallet);
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("data/wallets.json", true)) {
            gson.toJson(wallet, writer);
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyPair;
    }

    public static KeyPairWalletDTO createWalletIns() throws Exception {
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
        Context.blockchainStorage.saveWallet(wallet);
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("data/wallets.json", true)) {
            gson.toJson(wallet, writer);
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new KeyPairWalletDTO(keyPair, wallet);
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
        System.out.println("BASE64:" +publicKey);
        System.out.println("hesa:" +(KeyPairUtils.base64ToHex(publicKey)));
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        Wallet wallet = new Wallet()
                .setBalance(100)
                .setNonce(0)
                .setTransactions(new ArrayList<>())
                .setPublicKey(KeyPairUtils.base64ToHex(publicKey))
                .setState("Active")
                .setPublicKeyBase64(publicKey);

        Context.wallets.put(publicKey, wallet);
        Context.blockchainStorage.saveWallet(wallet);
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("data/wallets.json", true)) {
            gson.toJson(wallet, writer);
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyPair;
    }

    public static int getDatabaseSize(BlockchainStorage.Types type) {
        return Context.blockchainStorage.getDatabaseSize(type);
    }

    public static Wallet getWalletFromLevel(String publicKeyHex) {
        return Context.blockchainStorage.getWallet(publicKeyHex);
    }

    public static Transaction getTransactionFromLevel(String txHash) {
        return Context.blockchainStorage.getTransactionMempool(txHash);
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

    public static void showBlocks() {
        Context.blockchainStorage.showBlocks();
    }

    public static void recollect(List<Transaction> transactions, MinerTrans minerTrans) {

    }
}