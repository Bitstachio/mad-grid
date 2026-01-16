package com.github.barbodh.madgridapi.game.dao;

import com.github.barbodh.madgridapi.game.model.MultiplayerGame;
import com.github.barbodh.madgridapi.transaction.FirestoreTransactionContext;
import com.github.barbodh.madgridapi.util.FirestoreUtil;
import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameDaoImpl implements GameDao {
    private final String collectionName = "activeGames";
    private final Firestore firestore;

    @Override
    public void save(MultiplayerGame multiplayerGame) {
        FirestoreTransactionContext.get().set(firestore.collection(collectionName).document(multiplayerGame.getId()), multiplayerGame);
    }

    @Override
    public Optional<MultiplayerGame> findById(String id) {
        var future = FirestoreTransactionContext.get().get(firestore.collection(collectionName).document(id));
        var documentSnapshot = FirestoreUtil.awaitCompletion(future);
        return Optional.ofNullable(documentSnapshot.toObject(MultiplayerGame.class));
    }

    @Override
    public void deleteById(String id) {
        FirestoreTransactionContext.get().delete(firestore.collection(collectionName).document(id));
    }
}
