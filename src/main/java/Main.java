import Core.Context;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import utils.KeyPairUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.Base64;

import static utils.KeyPairUtils.KeyPairAnalizer;

public class Main {
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        try {
            Context.init();

            // Generate wallets first
            KeyPair wallet1 = Context.createGenesisWallet();
            KeyPair wallet2 = Context.createWallet();

            // Get public keys
            String publicKey1 = Base64.getEncoder().encodeToString(wallet1.getPublic().getEncoded());
            String publicKey2 = Base64.getEncoder().encodeToString(wallet2.getPublic().getEncoded());
            String privateKey1 = Base64.getEncoder().encodeToString(wallet1.getPrivate().getEncoded());

            // Add transaction between newly created wallets
            Context.addTransaction(
                    publicKey1,  // sender public key
                    publicKey2,  // receiver public key
                    privateKey1, // sender private key
                    99,     // value
                    "For you my friend!", // data
                    0.00002      // fee
            );

            Context.showMempool();
            Context.showWallets();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
