package com.virhon.fintech.gl.repo;

import com.virhon.fintech.gl.model.Page;

public interface CurPageRepo {
    IdentifiedEntity<Page> getById(Long accountId);
    IdentifiedEntity<Page> getByIdExclusive(Long accountId);
    void put(IdentifiedEntity<Page> page);
}