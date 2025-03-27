package utils;

import Core.Entities.Transaction;
import Core.Entities.Wallet;
import org.web3j.rlp.RlpDecoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class rlpUtils {

    // Método de deserialización correspondiente
    public static Wallet WalletfromRLP(byte[] rlpData) throws ParseException {
        RlpList decoded = RlpDecoder.decode(rlpData);
        List<RlpType> values = decoded.getValues();

        String publicKey = ((RlpString)values.get(0)).asString();
        String publicKeyBase64 = ((RlpString)values.get(1)).asString();
        double balance = Double.parseDouble(((RlpString)values.get(2)).asString());

        // Deserializar lista de transacciones
        ArrayList<Transaction> transactions = new ArrayList<>();
        RlpList txList = (RlpList)values.get(3);
        for (RlpType txRlp : txList.getValues()) {
            // Deserializar directamente desde los bytes RLP
            byte[] txBytes = ((RlpString)txRlp).getBytes();
            transactions.add(rlpUtils.TransactionfromRLP(txBytes));
        }

        int nonce = Integer.parseInt(((RlpString)values.get(4)).asString());
        String state = ((RlpString)values.get(5)).asString();

        return new Wallet(publicKey, publicKeyBase64, balance, transactions, nonce, state);
    }

    // Método de deserialización correspondiente
    public static Transaction TransactionfromRLP(byte[] rlpData) throws ParseException {
        RlpList decoded = RlpDecoder.decode(rlpData);
        List<RlpType> values = decoded.getValues();

        String HashID = ((RlpString)values.get(0)).asString();
        String senderPublicKey = ((RlpString)values.get(1)).asString();
        String reciverPublicKey = ((RlpString)values.get(2)).asString();
        String blockHash = ((RlpString)values.get(3)).asString();
        String state = ((RlpString)values.get(4)).asString();
        double value = Double.parseDouble(((RlpString)values.get(5)).asString());
        int nonce = Integer.parseInt(((RlpString)values.get(6)).asString());
        Date timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(((RlpString)values.get(7)).asString());
        String data  = ((RlpString)values.get(8)).asString();
        Double fee = Double.parseDouble(((RlpString)values.get(9)).asString());
        int numberOfComfirmations = Integer.parseInt(((RlpString)values.get(10)).asString());

        return  new Transaction(HashID, senderPublicKey, reciverPublicKey, blockHash, state, value, nonce, timestamp, data, fee, numberOfComfirmations);
    }
}
