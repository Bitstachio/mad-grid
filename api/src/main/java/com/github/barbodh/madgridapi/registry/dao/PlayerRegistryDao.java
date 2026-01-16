package com.github.barbodh.madgridapi.registry.dao;

public interface PlayerRegistryDao {
    void update(String id);

    boolean exists(String id);

    void delete(String id);
}
