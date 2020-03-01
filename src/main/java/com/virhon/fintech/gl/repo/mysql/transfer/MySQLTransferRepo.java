package com.virhon.fintech.gl.repo.mysql.transfer;

import com.virhon.fintech.gl.repo.TransferPages;
import com.virhon.fintech.gl.repo.TransferRepo;
import com.virhon.fintech.gl.repo.mysql.MySQLAbstactRepo;

import java.io.IOException;

public class MySQLTransferRepo extends MySQLAbstactRepo<MySQLTransferDAO> implements TransferRepo {

    public MySQLTransferRepo(String tablename) throws IOException {
        super(tablename, MySQLTransferDAO.class);
    }

    @Override
    public Long reg(String uuid, String clientUuid, String clientCustomerId,
                    String debitPageUuid, String creditPageUuid) {
        final MySQLTransferRecord record = new MySQLTransferRecord();
        record.setUuid(uuid);
        record.setClientUuid(clientUuid);
        record.setClientCustomerId(clientCustomerId);
        record.setDebitPageUuid(debitPageUuid);
        record.setCreditPageUuid(creditPageUuid);
        return getMapper().insert(getTablename(), record);
    }

    @Override
    public TransferPages get(String uuid) {
        final MySQLTransferRecord record = this.getMapper().selectByUuid(getTablename(), uuid);
        if (record != null) {
            final TransferPages pages = new TransferPages();
            pages.setTransferUuid(record.getUuid());
            pages.setClientUuid(record.getClientUuid());
            pages.setClientCustomerId(record.getClientCustomerId());
            pages.setDebitPageUuid(record.getDebitPageUuid());
            pages.setCreditPageUuid(record.getCreditPageUuid());
            return pages;
        }
        return null;
    }

    @Override
    public TransferPages get(Long id) {
        final MySQLTransferRecord record = this.getMapper().selectById(getTablename(), id);
        if (record != null) {
            final TransferPages pages = new TransferPages();
            pages.setTransferUuid(record.getUuid());
            pages.setClientUuid(record.getClientUuid());
            pages.setClientCustomerId(record.getClientCustomerId());
            pages.setDebitPageUuid(record.getDebitPageUuid());
            pages.setCreditPageUuid(record.getCreditPageUuid());
            return pages;
        }
        return null;
    }

}
