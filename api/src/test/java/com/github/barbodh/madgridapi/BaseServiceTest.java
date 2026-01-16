package com.github.barbodh.madgridapi;

import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;

import static org.mockito.Mockito.verifyNoMoreInteractions;

public abstract class BaseServiceTest {
    protected boolean skipUnverifiedMockInteractionCheck = false;

    /**
     * Cleans up unit test and verifies no more interactions on mocks.
     */
    @AfterEach
    void tearDown() {
        if (!skipUnverifiedMockInteractionCheck) {
            try {
                for (var field : this.getClass().getDeclaredFields()) {
                    if (field.isAnnotationPresent(Mock.class)) {
                        field.setAccessible(true);
                        verifyNoMoreInteractions(field.get(this));
                    }
                }
            } catch (IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}
