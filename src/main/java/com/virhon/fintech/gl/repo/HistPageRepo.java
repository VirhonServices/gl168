package com.virhon.fintech.gl.repo;

import com.virhon.fintech.gl.model.Page;

import java.time.ZonedDateTime;

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
    Long insert(Long accountId, Page page);
    Long update(Long accountId, IdentifiedEntity<Page> page);
    void commit();
}
