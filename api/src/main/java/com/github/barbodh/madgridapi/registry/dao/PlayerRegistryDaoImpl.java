package com.github.barbodh.madgridapi.registry.dao;

import com.github.barbodh.madgridapi.registry.model.ActivePlayer;
import com.github.barbodh.madgridapi.transaction.FirestoreTransactionContext;
import com.github.barbodh.madgridapi.util.FirestoreUtil;
import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlayerRegistryDaoImpl implements PlayerRegistryDao {
    private final String collectionName = "activePlayers";
    private final Firestore firestore;

    @Override
    public void update(String id) {
        FirestoreTransactionContext.get().set(firestore.collection(collectionName).document(id), new ActivePlayer(id));
    }

    @Override
    public boolean exists(String id) {
        var future = FirestoreTransactionContext.get().get(firestore.collection(collectionName));
        var querySnapshot = FirestoreUtil.awaitCompletion(future);

        for (var document : querySnapshot.getDocuments()) {
            var activePlayer = document.toObject(ActivePlayer.class);
            if (activePlayer.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void delete(String id) {
        FirestoreTransactionContext.get().delete(firestore.collection(collectionName).document(id));
    }
}
