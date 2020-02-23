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
    public IdentifiedEntity<Transfer> getByUuid(String uuid) {
        final MySQLTransferRecord record = this.getMapper().selectByUuid(this.getTablename(), uuid);
        if (record != null) {
            final Transfer transfer = getConverter().fromJson(record.getData(), Transfer.class);
            return new IdentifiedEntity<>(record.getId(), transfer);
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
    public IdentifiedEntity<Transfer> insert(Transfer transfer) {
        final MySQLTransferRecord record = new MySQLTransferRecord();
        record.setUuid(transfer.getTransferUuid());
        final String data = getConverter().toJson(transfer);
        record.setData(data);
        getMapper().insert(getTablename(), record);
        return new IdentifiedEntity<Transfer>(record.getId(), transfer);
    }

    @Override
    public void update(IdentifiedEntity<Transfer> transfer) {
        final MySQLTransferRecord record = new MySQLTransferRecord();
        record.setId(transfer.getId());
        record.setUuid(transfer.getEntity().getTransferUuid());
        final String data = getConverter().toJson(transfer.getEntity());
        record.setData(data);
        getMapper().update(getTablename(), record);
    }

}
