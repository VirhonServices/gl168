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
    Long put(IdentifiedEntity<Page> page);
    void commit();
}
