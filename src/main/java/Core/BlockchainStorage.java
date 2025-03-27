package Core;

import Core.Entities.Wallet;
import org.iq80.leveldb.*;
import utils.rlpUtils;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

import java.io.File;
import java.io.IOException;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

public class BlockchainStorage {
    private DB database;

    public BlockchainStorage(String dbpath) {
        Options options = new Options();
        options.createIfMissing(true);

        try {
            database = factory.open(new File(dbpath), options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveWallet(Wallet wallet) {
        try {
            // Use public key as the key for storing wallet
            byte[] key = wallet.publicKeyHex().getBytes();

            // Serialize wallet to RLP
            byte[] value = wallet.toRLP();

            // Write to LevelDB
            database.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException("Error saving wallet to LevelDB", e);
        }
    }

    public Wallet getWallet(String publicKeyHex) {
        try {
            byte[] key = publicKeyHex.getBytes();
            byte[] walletData = database.get(key);

            if (walletData == null) {
                return null; // Wallet not found
            }

            // Deserialize from RLP
            return rlpUtils.WalletfromRLP(walletData);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving wallet from LevelDB", e);
        }
    }
}
