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
import java.util.Locale;

public class rlpUtils {

    // Método de deserialización correspondiente
    public static Wallet WalletfromRLP(byte[] rlpData) throws ParseException {
        RlpList decoded = RlpDecoder.decode(rlpData);
        RlpList walletList = (RlpList) decoded.getValues().get(0);
        List<RlpType> values = walletList.getValues();

        // More robust conversion
        String publicKey = new String(((RlpString) values.get(0)).getBytes());
        String publicKeyBase64 = new String(((RlpString) values.get(1)).getBytes());
        double balance = Double.parseDouble(new String(((RlpString) values.get(2)).getBytes()));

        // Deserialize transactions
        ArrayList<Transaction> transactions = new ArrayList<>();
        RlpList txList = (RlpList) values.get(3);
        for (RlpType txRlp : txList.getValues()) {
            byte[] txBytes = ((RlpString) txRlp).getBytes();
            transactions.add(rlpUtils.TransactionfromRLP(txBytes));
        }

        int nonce = Integer.parseInt(new String(((RlpString) values.get(4)).getBytes()));
        String state = new String(((RlpString) values.get(5)).getBytes());

        return new Wallet(publicKey, publicKeyBase64, balance, transactions, nonce, state);
    }

    // Método de deserialización correspondiente
    public static Transaction TransactionfromRLP(byte[] rlpData) throws ParseException {
        RlpList decoded = RlpDecoder.decode(rlpData);
        RlpList transactionList = (RlpList) decoded.getValues().get(0);
        List<RlpType> values = transactionList.getValues();

        String HashID = new String(((RlpString)values.get(0)).getBytes());
        String senderPublicKey = new String(((RlpString)values.get(1)).getBytes());
        String reciverPublicKey = new String(((RlpString)values.get(2)).getBytes());
        String blockHash = new String(((RlpString)values.get(3)).getBytes());
        String state = new String(((RlpString)values.get(4)).getBytes());

        // Manejo robusto para valores numéricos
        double value = 0.0;
        try {
            value = Double.parseDouble(new String(((RlpString)values.get(5)).getBytes()));
        } catch (NumberFormatException e) {
            System.out.println("Error al parsear valor: " + new String(((RlpString)values.get(5)).getBytes()));
        }

        int nonce = 0;
        try {
            nonce = Integer.parseInt(new String(((RlpString)values.get(6)).getBytes()));
        } catch (NumberFormatException e) {
            System.out.println("Error al parsear nonce: " + new String(((RlpString)values.get(6)).getBytes()));
        }

// Parsing de fecha con manejo de excepciones
        // Parsing de fecha con manejo de excepciones
        Date timestamp = new Date();
        try {
            String dateStr = new String(((RlpString)values.get(7)).getBytes());
            // Intenta con varios formatos posibles
            try {
                // Formato ISO
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                timestamp = isoFormat.parse(dateStr);
            } catch (ParseException e1) {
                try {
                    // Formato JSON
                    SimpleDateFormat jsonFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss a", Locale.US);
                    timestamp = jsonFormat.parse(dateStr);
                } catch (ParseException e2) {
                    try {
                        // Formato toString() de Date
                        SimpleDateFormat defaultFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                        timestamp = defaultFormat.parse(dateStr);
                    } catch (ParseException e3) {
                        System.out.println("Error al parsear fecha: " + dateStr);
                        throw e3;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al parsear fecha, usando fecha actual: " + e.getMessage());
            // e.printStackTrace();
        }

        String data = new String(((RlpString)values.get(8)).getBytes());

        Double fee = 0.0;
        try {
            fee = Double.parseDouble(new String(((RlpString)values.get(9)).getBytes()));
        } catch (NumberFormatException e) {
            System.out.println("Error al parsear fee: " + new String(((RlpString)values.get(9)).getBytes()));
        }

        int numberOfComfirmations = 0;
        try {
            numberOfComfirmations = Integer.parseInt(new String(((RlpString)values.get(10)).getBytes()));
        } catch (NumberFormatException e) {
            System.out.println("Error al parsear confirmaciones: " + new String(((RlpString)values.get(10)).getBytes()));
        }

        return new Transaction(HashID, senderPublicKey, reciverPublicKey, blockHash, state, value, nonce, timestamp, data, fee, numberOfComfirmations);
    }
}
