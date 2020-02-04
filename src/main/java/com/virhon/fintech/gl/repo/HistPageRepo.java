package com.virhon.fintech.gl.repo;

import com.virhon.fintech.gl.model.HistoricalPage;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Access in read-only mode
 */
public interface HistPageRepo {
    IdentifiedEntity<HistoricalPage> getById(Long id);
    IdentifiedEntity<HistoricalPage> getByAccountId(Long accountId, ZonedDateTime at);
    IdentifiedEntity<HistoricalPage> getByAccountUuid(UUID accountUuid, ZonedDateTime at);
    IdentifiedEntity<HistoricalPage> getByAccountNumber(String accountNumber, ZonedDateTime at);
}
