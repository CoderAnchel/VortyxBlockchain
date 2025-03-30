package Core;

import Core.Entities.Block;
import Core.Entities.Transaction;
import Core.Entities.Wallet;
import org.iq80.leveldb.*;
import utils.rlpUtils;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

public class BlockchainStorage {
    private DB walletsDatabase;
    private DB mempoolDatabase;
    private DB transactionDatabase;
    private DB blocksDatabase;

    public enum Types {
        WALLETS,
        MEMPOOL,
        TRANSACTIONS,
        BLOCKS;
    }

    public BlockchainStorage(String dbpath) {
        Options options = new Options();
        options.createIfMissing(true);

        try {
            walletsDatabase = factory.open(new File(dbpath+"wallets"), options);
            mempoolDatabase = factory.open(new File(dbpath+"mempool"), options);
            transactionDatabase = factory.open(new File(dbpath+"transactions"), options);
            blocksDatabase = factory.open(new File(dbpath+"blocks"), options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveBlock(Block block) {
        try {
            byte[] key = block.getHash().getBytes();
            byte[] value = block.toRLP();

            // Write to LevelDB
            blocksDatabase.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException("Error saving wallet to LevelDB", e);
        }
    }

    public void saveWallet(Wallet wallet) {
        try {
            // Use public key as the key for storing wallet
            byte[] key = wallet.publicKeyHex().getBytes();

            // Serialize wallet to RLP
            byte[] value = wallet.toRLP();

            // Write to LevelDB
            walletsDatabase.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException("Error saving wallet to LevelDB", e);
        }
    }

    public Wallet getWallet(String publicKeyHex) {
        try {
            byte[] key = publicKeyHex.getBytes();
            byte[] walletData = walletsDatabase.get(key);

            if (walletData == null) {
                return null; // Wallet not found
            }

            // Deserialize from RLP
            return rlpUtils.WalletfromRLP(walletData);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving wallet from LevelDB", e);
        }
    }

    // Delete a wallet from LevelDB
    public void deleteWallet(String publicKeyHex) {
        try {
            byte[] key = publicKeyHex.getBytes();
            walletsDatabase.delete(key);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting wallet from LevelDB", e);
        }
    }

    public void saveTransactionMempoool(Transaction tx) {
        try {
            byte[] key = tx.HashID().getBytes();
            byte[] walletData = tx.toRLP();
            this.mempoolDatabase.put(key, walletData);
        } catch (Exception e) {
            throw new RuntimeException("Error saving transaction to LevelDB", e);
        }
    }

    public void deleteTransactionMempool(String publicKeyHex) {
        try {
            byte[] key = publicKeyHex.getBytes();
            this.transactionDatabase.delete(key);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting transaction from LevelDB", e);
        }
    }

    public Transaction getTransactionMempool(String publicKeyHex) {
        try {
            byte[] key = publicKeyHex.getBytes();
            byte[] response = this.transactionDatabase.get(key);
            if (response == null) {
                return null;
            }
            return rlpUtils.TransactionfromRLP(response);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting transaction from LevelDB", e);
        }
    }

    public void saveTransaction(Transaction tx) {
        try {
            byte[] key = tx.HashID().getBytes();
            byte[] walletData = tx.toRLP();
            this.transactionDatabase.put(key, walletData);
        } catch (Exception e) {
            throw new RuntimeException("Error saving transaction to LevelDB", e);
        }
    }

    public void deleteTransaction(String publicKeyHex) {
        try {
            byte[] key = publicKeyHex.getBytes();
            this.transactionDatabase.delete(key);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting transaction from LevelDB", e);
        }
    }

    public Transaction getTransaction(String publicKeyHex) {
        try {
            byte[] key = publicKeyHex.getBytes();
            byte[] response = this.transactionDatabase.get(key);
            if (response == null) {
                return null;
            }
            return rlpUtils.TransactionfromRLP(response);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting transaction from LevelDB", e);
        }
    }

    public int getDatabaseSize(Types type) {
        DB database = switch (type) {
            case WALLETS -> walletsDatabase;
            case MEMPOOL -> mempoolDatabase;
            case TRANSACTIONS -> transactionDatabase;
            case BLOCKS -> blocksDatabase;
            default -> throw new IllegalArgumentException("Unknown database type: " + type);
        };

        int size = 0;
        try (DBIterator iterator = database.iterator()) {
            while (iterator.hasNext()) {
                iterator.next();
                size++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting database size", e);
        }
        return size;
    }

    public List<Transaction> getSliceOfTransactions(int portion, Types type) {
        List<Transaction> transactions = new ArrayList<>();

        DB database = switch (type) {
            case MEMPOOL -> mempoolDatabase;
            case TRANSACTIONS -> transactionDatabase;
            default -> throw new IllegalArgumentException("Tipo de base de datos inválido: " + type);
        };

        try(DBIterator iterator = database.iterator()) {
            int counter = 0;
            iterator.seekToFirst();

            // Verificar si hay elementos
            if (!iterator.hasNext()) {
                return transactions; // Devolver lista vacía si no hay elementos
            }

            while (iterator.hasNext() && counter < portion) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                byte[] value = entry.getValue();

                // Verificar que el valor no es nulo ni vacío
                if (value != null && value.length > 0) {
                    try {
                        Transaction transaction = rlpUtils.TransactionfromRLP(value);
                        transactions.add(transaction);
                        counter++;
                    } catch (Exception e) {
                        System.out.println("Error al deserializar transacción: " + e.getMessage());
                        // Continuar con la siguiente transacción
                    }
                }
            }
            return transactions;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void showBlocks() {
        try(DBIterator iterator = blocksDatabase.iterator()) {
            while (iterator.hasNext()) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                Block block = rlpUtils.BlockFromRLP(entry.getValue());
                System.out.println("Block "+block.getHash()+": "+"----------------");
                System.out.println(block.toString());
                System.out.println("TRANSACTIONS BLOCK :");
                showTransactionsUsingLevel(block.transactions());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void showTransactionsUsingLevel(List<String> keys) throws ParseException {
        for (String key : keys) {
            byte[] txData = transactionDatabase.get(key.getBytes());
            if (txData == null) {
                System.out.println("Transacción no encontrada: " + key);
                continue;
            }

            try {
                Transaction tx = rlpUtils.TransactionfromRLP(txData);
                tx.showInfo();
            } catch (Exception e) {
                System.out.println("Error al deserializar transacción " + key + ": " + e.getMessage());
            }
        }
    }
    public void deleteEntities(List<String> keys, Types type) {
        DB database = switch (type) {
            case BLOCKS -> blocksDatabase;
            case MEMPOOL -> mempoolDatabase;
            case TRANSACTIONS -> transactionDatabase;
            case WALLETS -> walletsDatabase;
            default -> throw new IllegalArgumentException("Unknown database type: " + type);
        };

        for(String key : keys) {
            database.delete(key.getBytes());
        };
    }

    public void addTransactionsToMempool(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            try {
                byte[] key = transaction.HashID().getBytes();
                byte[] walletData = transaction.toRLP();
                this.mempoolDatabase.put(key, walletData);
            } catch (Exception e) {
                throw new RuntimeException("Error saving transaction to LevelDB", e);
            }
        }
    }

    public void addTransactionsToDefinitive(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            try {
                byte[] key = transaction.HashID().getBytes();
                byte[] walletData = transaction.toRLP();
                this.transactionDatabase.put(key, walletData);
            } catch (Exception e) {
                throw new RuntimeException("Error saving transaction to LevelDB", e);
            }
        }
    }
}
