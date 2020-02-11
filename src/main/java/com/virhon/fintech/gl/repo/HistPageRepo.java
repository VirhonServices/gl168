package com.virhon.fintech.gl.repo;

import com.virhon.fintech.gl.model.HistoricalPage;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Access in read-only mode
 */
public interface HistPageRepo {
    IdentifiedEntity<HistoricalPage> getById(Long id);

    /**
     *
     * @param accountId
     * @param postedAt              - the moment of POSTING !!!
     * @return
     */
    IdentifiedEntity<HistoricalPage> getByAccountId(Long accountId, ZonedDateTime postedAt);
    List<IdentifiedEntity<HistoricalPage>> getReporetedHistory(Long accountId, LocalDate from, LocalDate to);
    Long put(IdentifiedEntity<HistoricalPage> page);
    void commit();
}
