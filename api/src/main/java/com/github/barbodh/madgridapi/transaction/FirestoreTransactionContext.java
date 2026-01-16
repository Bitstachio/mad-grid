package com.github.barbodh.madgridapi.transaction;

import com.google.cloud.firestore.Transaction;

public class FirestoreTransactionContext {
    private static final ThreadLocal<Transaction> context = new ThreadLocal<>();

    public static void set(Transaction transaction) {
        context.set(transaction);
    }

    public static Transaction get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}
