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

            Context.addTransaction("3056301006072a8648ce3d020106052b8104000a034200047998c5f5339a91f7c327114a850c9dffeea1cfd6475f0571498b050decd64f70e8f3d4daaef207e69fcaca607453d8a2f1ad79d44cc93acd4d546c59e640a9c4",
                    "3056301006072a8648ce3d020106052b8104000a034200043347fbaf8d012fae3e94d0278d685fb4932874138e5d733e156fe7fea6abeb9d869753356c8f2216ba0151df1fdc33e00a14fe5f4ff5643dff705e7e12304257",
                    "MIGNAgEAMBAGByqGSM49AgEGBSuBBAAKBHYwdAIBAQQgj2q7zKQpDU3ek6igXrDxOvXpWlMsKMxn0RAtljC92HGgBwYFK4EEAAqhRANCAAR5mMX1M5qR98MnEUqFDJ3/7qHP1kdfBXFJiwUN7NZPcOjz1Nqu8gfmn8rKYHRT2KLxrXnUTMk6zU1UbFnmQKnE",
                    0.00002,
                    "For you my friend!",
                    0.00002
            );

            Context.showMempool();

            //KeyPair wallet1 = Context.createGenesisWallet();
            //KeyPairAnalizer(wallet1);
            //System.out.println("-------------------");
            //KeyPair wallet2 = Context.createWallet();
            //KeyPairAnalizer(wallet2);

            //Context.showWallets();

            //String publicKey = KeyPairUtils.getPublickey(wallet1);
            //System.out.println("Finding for wallet with publickey: "+publicKey);
            //Context.showWalletInfo(publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
