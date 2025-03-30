package utils;

import Core.Entities.Transaction;

import java.util.ArrayList;
import java.util.List;

public class IterationUtils {

    public static List<String> getTransKeys( List<Transaction> transactions) {
        List<String> keys = new ArrayList<>();
        for (Transaction transaction: transactions) {
            keys.add(transaction.HashID());
        }

        return keys;
    }
}
