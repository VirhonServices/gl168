package com.virhon.fintech.gl.repo.mysql.historicalpage;

import com.virhon.fintech.gl.model.Page;
import com.virhon.fintech.gl.repo.HistPageRepo;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.mysql.MySQLAbstactRepo;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class MySQLHistoricalPageRepo extends MySQLAbstactRepo<MySQLHitoricalPageDAO> implements HistPageRepo {

    public MySQLHistoricalPageRepo(String tablename) throws IOException {
        super(tablename, MySQLHitoricalPageDAO.class);
    }

    @Override
    public IdentifiedEntity<Page> getById(Long id) {
        final MySQLHistoricalPageRecord record = getMapper().selectById(getTablename(), id);
        if (record!=null) {
            final Page page = getConverter().fromJson(record.getData(), Page.class);
            return new IdentifiedEntity<>(id, page);
        }
        return null;
    }

    @Override
    public IdentifiedEntity<Page> getByAccountId(Long accountId, ZonedDateTime at) {
        final MySQLHistoricalPageRecord record = getMapper().selectByAccountId(getTablename(), accountId, at);
        if (record!=null) {
            final Page page = getConverter().fromJson(record.getData(), Page.class);
            return new IdentifiedEntity<>(record.getId(), page);
        }
        return null;
    }

    @Override
    public List<IdentifiedEntity<Page>> getHistory(Long accountId) {
        final List<MySQLHistoricalPageRecord> records = getMapper().selectHistory(getTablename(), accountId);
        final List<IdentifiedEntity<Page>> result = new ArrayList<>();
        if (!records.isEmpty()) {
            records.forEach(r -> {
                final Page page = getConverter().fromJson(r.getData(), Page.class);
                result.add(new IdentifiedEntity<Page>(r.getId(), page));
            });
        }
        return result;
    }

    @Override
    public Long update(Long accountId, IdentifiedEntity<Page> page) {
        final MySQLHistoricalPageRecord record = new MySQLHistoricalPageRecord();
        record.setId(page.getId());
        record.setAccountId(accountId);
        record.setStartedAt(page.getEntity().getStartedAt());
        record.setFinishedAt(page.getEntity().getFinishedAt());
        record.setRepStartedOn(page.getEntity().getRepStartedOn());
        record.setRepFinishedOn(page.getEntity().getRepFinishedOn());
        final String data = getConverter().toJson(page.getEntity());
        record.setData(data);
        getMapper().update(getTablename(), record);
        return page.getId();
    }

    @Override
    public Long insert(Long accountId, Page page) {
        final MySQLHistoricalPageRecord record = new MySQLHistoricalPageRecord();
        record.setAccountId(accountId);
        record.setStartedAt(page.getStartedAt());
        record.setFinishedAt(page.getFinishedAt());
        record.setRepStartedOn(page.getRepStartedOn());
        record.setRepFinishedOn(page.getRepFinishedOn());
        final String data = getConverter().toJson(page);
        record.setData(data);
        getMapper().insert(getTablename(), record);
        return record.getId();
    }
}