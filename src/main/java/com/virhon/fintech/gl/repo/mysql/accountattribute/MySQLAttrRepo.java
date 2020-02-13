package com.virhon.fintech.gl.repo.mysql.accountattribute;

import com.virhon.fintech.gl.model.AccountAttributes;
import com.virhon.fintech.gl.repo.AttrRepo;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.mysql.MySQLAbstactRepo;

import java.io.IOException;

public class MySQLAttrRepo extends MySQLAbstactRepo<MySQLAccountAttributeDAO> implements AttrRepo {

    public MySQLAttrRepo(String tablename) throws IOException {
        super(tablename, MySQLAccountAttributeDAO.class);
    }

    @Override
    public IdentifiedEntity<AccountAttributes> getById(Long accountId) {
        final MySQLAccountAttributeRecord record = getMapper().selectById(getTablename(), accountId);
        if (record != null) {
            final AccountAttributes accountAttributes = getConverter().fromJson(record.getData(), AccountAttributes.class);
            return new IdentifiedEntity<>(accountId, accountAttributes);
        } else {
            return null;
        }
    }

    @Override
    public IdentifiedEntity<AccountAttributes> getByIdExclusive(Long accountId) {
        final MySQLAccountAttributeRecord record = getMapper().selectByIdExclusive(getTablename(), accountId);
        if (record != null) {
            final AccountAttributes accountAttributes = getConverter().fromJson(record.getData(), AccountAttributes.class);
            return new IdentifiedEntity<>(accountId, accountAttributes);
        } else {
            return null;
        }
    }

    @Override
    public void update(IdentifiedEntity<AccountAttributes> attributes) {
        final MySQLAccountAttributeRecord record = new MySQLAccountAttributeRecord();
        record.setId(attributes.getId());
        record.setAccountNumber(attributes.getEntity().getAccountNumber());
        record.setIban(attributes.getEntity().getIban());
        record.setUuid(attributes.getEntity().getAccountUUID());
        final String json = getConverter().toJson(attributes.getEntity());
        record.setData(json);
        getMapper().update(getTablename(), record);
    }

    @Override
    public Long insert(AccountAttributes attributes) {
        final MySQLAccountAttributeRecord record = new MySQLAccountAttributeRecord();
        record.setAccountNumber(attributes.getAccountNumber());
        record.setIban(attributes.getIban());
        record.setUuid(attributes.getAccountUUID());
        final String json = getConverter().toJson(attributes);
        record.setData(json);
        getMapper().insert(getTablename(), record);
        return record.getId();
    }

    @Override
    public void commit() {
        getSession().commit();
    }
}
