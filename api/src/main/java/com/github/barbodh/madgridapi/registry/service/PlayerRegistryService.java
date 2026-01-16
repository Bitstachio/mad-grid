package com.github.barbodh.madgridapi.registry.service;

public interface PlayerRegistryService {
    void update(String id);

    boolean exists(String id);

    void delete(String id);
}
