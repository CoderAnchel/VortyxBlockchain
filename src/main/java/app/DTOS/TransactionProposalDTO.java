package app.DTOS;

public class TransactionProposalDTO extends TransactionDTO {
    public String senderPublicKey;
    public String reciberPublicKey;
    public String senderPrivateKey;
    public double value;
    public double fee;
    public String data;
}
