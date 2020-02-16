package com.virhon.fintech.gl.repo.mysql.currentpage;

import com.virhon.fintech.gl.model.Page;
import com.virhon.fintech.gl.repo.CurPageRepo;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.mysql.MySQLAbstactRepo;

import java.io.IOException;

public class MySQLCurrentPageRepo extends MySQLAbstactRepo<MySQLCurrentPageDAO> implements CurPageRepo {

    public MySQLCurrentPageRepo(String tablename) throws IOException {
        super(tablename, MySQLCurrentPageDAO.class);
    }

    @Override
    public IdentifiedEntity<Page> getById(Long accountId) {
        final MySQLCurrentPageRecord record = getMapper().selectById(getTablename(), accountId);
        if (record != null) {
            final Page page = getConverter().fromJson(record.getData(), Page.class);
            return new IdentifiedEntity<>(accountId, page);
        } else {
            return null;
        }
    }

    @Override
    public IdentifiedEntity<Page> getByIdExclusive(Long accountId) {
        final MySQLCurrentPageRecord record = getMapper().selectByIdExclusive(getTablename(), accountId);
        if (record != null) {
            final Page page = getConverter().fromJson(record.getData(), Page.class);
            return new IdentifiedEntity<>(accountId, page);
        } else {
            return null;
        }
    }

    @Override
    public void put(IdentifiedEntity<Page> page) {
        final MySQLCurrentPageRecord record = new MySQLCurrentPageRecord();
        record.setId(page.getId());
        final String json = getConverter().toJson(page.getEntity());
        record.setData(json);
        if (getMapper().selectById(getTablename(), page.getId()) == null) {
            getMapper().insert(getTablename(), record);
        } else {
            getMapper().update(getTablename(), record);
        }
    }
}
