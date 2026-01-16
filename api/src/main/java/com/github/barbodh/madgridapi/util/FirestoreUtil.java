package com.github.barbodh.madgridapi.util;

import com.github.barbodh.madgridapi.exception.FirestoreOperationException;
import com.google.api.core.ApiFuture;

import java.util.concurrent.ExecutionException;

public class FirestoreUtil {
    public static <T> T awaitCompletion(ApiFuture<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException exception) {
            if (exception instanceof InterruptedException) Thread.currentThread().interrupt();
            throw new FirestoreOperationException(exception);
        }
    }
}
