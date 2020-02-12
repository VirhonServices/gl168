package com.virhon.fintech.gl.repo;

import com.virhon.fintech.gl.model.AccountAttributes;

public interface AttrRepo {
    IdentifiedEntity<AccountAttributes> getById(Long accountId);
    IdentifiedEntity<AccountAttributes> getByIdExclusive(Long accountId);
    Long insert(AccountAttributes attributes);
    void update(IdentifiedEntity<AccountAttributes> attributes);
    void commit();
}
