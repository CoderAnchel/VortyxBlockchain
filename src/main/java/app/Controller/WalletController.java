package app.Controller;

import Core.Context;
import app.DTOS.TransactionConfirmedDTO;
import app.DTOS.TransactionDTO;
import app.DTOS.TransactionProposalDTO;
import app.DTOS.WalletCreationDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import utils.KeyPairUtils;

import java.security.KeyPair;
import java.util.Base64;

@RestController
public class WalletController {

    @GetMapping("/Wallet/Create")
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

    @PostMapping("Transactions/add")
    public TransactionDTO addTransaction(@RequestBody TransactionProposalDTO transactionProposalDTO) {
        try {
            Context.addTransaction(
                    transactionProposalDTO.senderPublicKey,
                    transactionProposalDTO.reciberPublicKey,
                    transactionProposalDTO.senderPrivateKey, transactionProposalDTO.value,
                    transactionProposalDTO.data, transactionProposalDTO.fee
            );

            TransactionConfirmedDTO transactionConfirmedDTO = new TransactionConfirmedDTO();
            transactionConfirmedDTO.messague = "Operation signed and verified!, moving to mempool";
            transactionConfirmedDTO.status = "MEMPOOL";
            transactionConfirmedDTO.data = transactionProposalDTO.data;
            transactionConfirmedDTO.fee = transactionProposalDTO.fee;
            transactionConfirmedDTO.value = transactionProposalDTO.value;
            transactionConfirmedDTO.reciberPublicKey = transactionProposalDTO.reciberPublicKey;
            transactionConfirmedDTO.senderPublicKey = transactionProposalDTO.senderPublicKey;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
