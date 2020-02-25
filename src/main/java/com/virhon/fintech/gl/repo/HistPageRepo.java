package com.virhon.fintech.gl.repo;

import com.virhon.fintech.gl.model.Page;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Access in read-only mode
 */
public interface HistPageRepo {
    IdentifiedEntity<Page> getById(Long id);

    /**
     *
     * @param accountId
     * @param postedAt              - the moment of POSTING !!!
     * @return
     */
    IdentifiedEntity<Page> getByAccountId(Long accountId, ZonedDateTime postedAt);
    List<IdentifiedEntity<Page>> getHistory(Long accountId);
    List<IdentifiedEntity<Page>> getHistoryPeriod(Long accountId, LocalDate startPeriod, LocalDate finishPeriod);
    Long insert(Long accountId, Page page);
    Long update(Long accountId, IdentifiedEntity<Page> page);
}
