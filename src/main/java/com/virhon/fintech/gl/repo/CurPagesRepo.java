package com.virhon.fintech.gl.repo;

import com.virhon.fintech.gl.model.CurrentPage;

import java.util.UUID;

public interface CurPagesRepo {
    IdentifiedEntity<CurrentPage> getById(Long accountId);
    IdentifiedEntity<CurrentPage> getByIdExclusive(Long accountId);
    void put(IdentifiedEntity<CurrentPage> page);
    void commit();
}
