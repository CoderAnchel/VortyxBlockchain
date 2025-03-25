package app.Controller;

import Core.Context;
import app.DTOS.WalletCreationDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.KeyPairUtils;

import java.security.KeyPair;
import java.util.Base64;

@RestController
public class WalletController {

    @GetMapping("/Create")
    public WalletCreationDTO getNewWallet()  {
        try {
            KeyPair keypair = Context.createWallet();
            String publicKey = Base64.getEncoder().encodeToString(keypair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keypair.getPrivate().getEncoded());
            WalletCreationDTO walletCreationDTO = new WalletCreationDTO();
            walletCreationDTO.publicKey = KeyPairUtils.base64ToHex(publicKey);
            walletCreationDTO.privateKey = KeyPairUtils.base64ToHex(privateKey);
            return walletCreationDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
