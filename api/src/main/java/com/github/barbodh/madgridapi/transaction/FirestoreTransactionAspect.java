package com.github.barbodh.madgridapi.transaction;

import com.github.barbodh.madgridapi.exception.FirestoreTransactionException;
import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class FirestoreTransactionAspect {
    private final Firestore firestore;

    @Around("@annotation(FirestoreTransactional)")
    public Object manageTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        if (FirestoreTransactionContext.get() != null) {
            return joinPoint.proceed();
        }
        return firestore.runTransaction(transaction -> {
            FirestoreTransactionContext.set(transaction);
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                throw new FirestoreTransactionException(throwable);
            } finally {
                FirestoreTransactionContext.clear();
            }
        }).get();
    }
}
