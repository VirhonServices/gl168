package com.virhon.fintech.gl.repo;

import com.virhon.fintech.gl.model.Transfer;

public interface TransferRepo {
    IdentifiedEntity<Transfer> getById(Long id);
    IdentifiedEntity<Transfer> getByIdExclusive(Long id);
    Long insert(Transfer transfer);
    void update(IdentifiedEntity<Transfer> transfer);
    void commit();
}