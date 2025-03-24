import Core.Context;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import utils.KeyPairUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import static utils.KeyPairUtils.KeyPairAnalizer;

public class Main {
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        try {
            Context.init();
            KeyPair wallet1 = Context.createWallet();
            KeyPairAnalizer(wallet1);
            System.out.println("-------------------");
            KeyPair wallet2 = Context.createWallet();
            KeyPairAnalizer(wallet2);

            Context.showWallets();

            String publicKey = KeyPairUtils.getPublickey(wallet1);
            System.out.println("Finding for wallet with publickey: "+publicKey);
            Context.showWalletInfo(publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
