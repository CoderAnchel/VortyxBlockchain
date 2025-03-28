package app;

import Core.BlockchainStorage;
import Core.Context;
import Core.Entities.Transaction;
import Core.Entities.Wallet;
import app.DTOS.KeyPairWalletDTO;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import utils.KeyPairUtils;
import utils.rlpUtils;

import java.security.KeyPair;
import java.security.Security;
import java.util.Base64;

@SpringBootApplication
public class NodeApp {
    public static void main(String[] args) {
        SpringApplication.run(NodeApp.class, args);
        Security.addProvider(new BouncyCastleProvider());
        try {
            Context.init();

            // Generate wallets first
            KeyPair wallet1 = Context.createGenesisWallet();
            KeyPair wallet2 = Context.createWallet();

            // Get public keys
            String publicKey1 = Base64.getEncoder().encodeToString(wallet1.getPublic().getEncoded());
            String privateKey1 = Base64.getEncoder().encodeToString(wallet1.getPrivate().getEncoded());

            // Add transaction between newly created wallets
            Context.addTransaction(
                    "3056301006072a8648ce3d020106052b8104000a0342000401f3ac8f12f85fbc713326183cc49c89f70766cf0f712adef60bd058e4bf9816b58e60bba80d7aada501da93dd8acc06950900596ee89e13f7811fa0f795cfcc",  // sender public key
                    "3056301006072a8648ce3d020106052b8104000a034200048e50ccc030acc2015c499e69bb7585bdf82f6ac43a01f75162829104715082c8da69ca298ae0531257d5e9947bdbbe2a375465e8b27980ddec33dc5e6662008a",  // receiver public key
                    "MIGNAgEAMBAGByqGSM49AgEGBSuBBAAKBHYwdAIBAQQgpJ/U+z9zGdLMmWAXz9ILSYE+92jE9L17X1PoXPpPJ7WgBwYFK4EEAAqhRANCAAQB86yPEvhfvHEzJhg8xJyJ9wdmzw9xKt72C9BY5L+YFrWOYLuoDXqtpQHak92KzAaVCQBZbuieE/eBH6D3lc/M", // sender private key
                    0.00002,// value
                    "For you my friend!",// data
                    0.000001// fee
            );

            Context.showMempool();


            KeyPairWalletDTO testWallet = Context.createWalletIns();
            System.out.println("TESTING PUBLIC: "+KeyPairUtils.base64ToHex(Base64.getEncoder().encodeToString(testWallet.getKeyPair().getPublic().getEncoded())));
            System.out.println("TESTING PRIVATE: "+KeyPairUtils.base64ToHex(Base64.getEncoder().encodeToString(testWallet.getKeyPair().getPrivate().getEncoded())));
            System.out.println("TESTING WALLET BEFORE RLP: ");
            testWallet.getWallet().showInfo();
            byte[] rlpWallet = testWallet.getWallet().toRLP();
            System.out.println("RLP COONVERTED!: "+rlpWallet);
            System.out.println("TESTING WALLET AFTER RLP: ");
            Wallet walletConverted = rlpUtils.WalletfromRLP(rlpWallet);
            walletConverted.showInfo();

            System.out.println("GETTING IT FROM LEVEEEL!!!!!!");
            Context.getWalletFrmoLevel(KeyPairUtils.base64ToHex(Base64.getEncoder().encodeToString(testWallet.getKeyPair().getPublic().getEncoded()))).showInfo();

            System.out.println("----------");
            System.out.println("Transaction from level!!!");
            Transaction transaction = Context.getTransactionFromLevel(
                    "f173846b45382e3e3f4a9774d4d72ceaef316f7fbff0ffb24c003ac997fb72fa");
            transaction.showInfo();

            System.out.println("Wallets size: "+Context.getDatabaseSize(BlockchainStorage.Types.WALLETS));
            System.out.println("Mempool size: "+Context.getDatabaseSize(BlockchainStorage.Types.MEMPOOL));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
