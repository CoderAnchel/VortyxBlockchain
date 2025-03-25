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
            System.out.println(privateKey1);

            // Add transaction between newly created wallets
            Context.addTransaction(
                    "3056301006072a8648ce3d020106052b8104000a0342000401f3ac8f12f85fbc713326183cc49c89f70766cf0f712adef60bd058e4bf9816b58e60bba80d7aada501da93dd8acc06950900596ee89e13f7811fa0f795cfcc",  // sender public key
                    "3056301006072a8648ce3d020106052b8104000a034200048e50ccc030acc2015c499e69bb7585bdf82f6ac43a01f75162829104715082c8da69ca298ae0531257d5e9947bdbbe2a375465e8b27980ddec33dc5e6662008a",  // receiver public key
                    "MIGNAgEAMBAGByqGSM49AgEGBSuBBAAKBHYwdAIBAQQgpJ/U+z9zGdLMmWAXz9ILSYE+92jE9L17X1PoXPpPJ7WgBwYFK4EEAAqhRANCAAQB86yPEvhfvHEzJhg8xJyJ9wdmzw9xKt72C9BY5L+YFrWOYLuoDXqtpQHak92KzAaVCQBZbuieE/eBH6D3lc/M", // sender private key
                    0.00002,     // value
                    "For you my friend!", // data
                    0.000001      // fee
            );

            Context.showMempool();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
