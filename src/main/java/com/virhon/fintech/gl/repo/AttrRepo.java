package com.virhon.fintech.gl.repo;

import com.virhon.fintech.gl.model.AccountAttributes;

public interface AttrRepo {
    IdentifiedEntity<AccountAttributes> getById(Long accountId);
    IdentifiedEntity<AccountAttributes> getByAccountNumber(String accountNumber);
    IdentifiedEntity<AccountAttributes> getByIban(String iban);
    IdentifiedEntity<AccountAttributes> getByIdExclusive(Long accountId);
    IdentifiedEntity<AccountAttributes> getByAccountNumberExclusive(String accountNumber);
    IdentifiedEntity<AccountAttributes> getByIbanExclusive(String iban);
    Long insert(AccountAttributes attributes);
    void update(IdentifiedEntity<AccountAttributes> attributes);
}
