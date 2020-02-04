package com.virhon.fintech.gl.repo;

import com.virhon.fintech.gl.model.AccountAttributes;

import java.util.UUID;

public interface AttrRepo {
    IdentifiedEntity<AccountAttributes> getById(Long accountId);
    IdentifiedEntity<AccountAttributes> getByIdExclusive(Long accountId);
    void put(IdentifiedEntity<AccountAttributes> attributes);
    void commit();
}
