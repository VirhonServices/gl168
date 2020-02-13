package com.virhon.fintech.gl.repo.mysql.transfer;

import com.virhon.fintech.gl.model.Transfer;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.TransferRepo;
import com.virhon.fintech.gl.repo.mysql.MySQLAbstactRepo;

import java.io.IOException;

public class MySQLTransferRepo extends MySQLAbstactRepo<MySQLTransferDAO> implements TransferRepo {

    public MySQLTransferRepo(String tablename) throws IOException {
        super(tablename, MySQLTransferDAO.class);
    }

    @Override
    public IdentifiedEntity<Transfer> getById(Long id) {
        final MySQLTransferRecord record = this.getMapper().selectById(this.getTablename(), id);
        if (record != null) {
            final Transfer transfer = getConverter().fromJson(record.getData(), Transfer.class);
            return new IdentifiedEntity<>(id, transfer);
        } else {
            return null;
        }
    }

    @Override
    public IdentifiedEntity<Transfer> getByIdExclusive(Long id) {
        final MySQLTransferRecord record = this.getMapper().selectByIdExclusive(this.getTablename(), id);
        if (record != null) {
            final Transfer transfer = getConverter().fromJson(record.getData(), Transfer.class);
            return new IdentifiedEntity<>(id, transfer);
        } else {
            return null;
        }
    }

    @Override
    public Long insert(Transfer transfer) {
        final MySQLTransferRecord record = new MySQLTransferRecord();
        final String data = getConverter().toJson(transfer);
        record.setData(data);
        getMapper().insert(getTablename(), record);
        return record.getId();
    }

    @Override
    public void update(IdentifiedEntity<Transfer> transfer) {
        final MySQLTransferRecord record = new MySQLTransferRecord();
        record.setId(transfer.getId());
        final String data = getConverter().toJson(transfer.getEntity());
        record.setData(data);
        getMapper().update(getTablename(), record);
    }

    @Override
    public void commit() {
        this.getSession().commit();
    }
}
