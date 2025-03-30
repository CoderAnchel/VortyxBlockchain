package app.Controller;

import Core.Context;
import app.DTOS.TransactionConfirmedDTO;
import app.DTOS.TransactionDTO;
import app.DTOS.TransactionProposalDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import utils.KeyPairUtils;

@RestController
public class TransactionController {

    @PostMapping("Transactions/add")
    public TransactionDTO addTransaction(@RequestBody TransactionProposalDTO transactionProposalDTO) {
        try {
            Context.addTransaction(
                    transactionProposalDTO.senderPublicKey,
                    transactionProposalDTO.reciberPublicKey,
                    KeyPairUtils.hexToBase64(transactionProposalDTO.senderPrivateKey), transactionProposalDTO.value,
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
            return transactionConfirmedDTO;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
