package com.virhon.fintech.gl.repo.mysql;

import com.google.gson.Gson;
import com.virhon.fintech.gl.model.AccountAttributes;
import com.virhon.fintech.gl.repo.AttrRepo;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.TransactionIsolationLevel;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.System.getProperties;

public class MySQLAttrRepo implements AttrRepo {
    public static final String CONFIGURATION_XML = "mybatis/mybatis-config.xml";
    private InputStream inputStream = Resources.getResourceAsStream(CONFIGURATION_XML);
    private SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(inputStream, getProperties());
    private SqlSession session = sqlMapper.openSession(TransactionIsolationLevel.READ_COMMITTED);
    private MySQLAccountAttributeDAO mapper = this.session.getMapper(MySQLAccountAttributeDAO.class);

    private Gson converter = new Gson();

    public MySQLAttrRepo() throws IOException {
    }

    @Override
    public IdentifiedEntity<AccountAttributes> getById(Long accountId) {
        final MySQLAccountAttributeRecord record = mapper.selectById(accountId);
        if (record != null) {
            final AccountAttributes accountAttributes = converter.fromJson(record.getData(), AccountAttributes.class);
            return new IdentifiedEntity<>(accountId, accountAttributes);
        } else {
            return null;
        }
    }

    @Override
    public IdentifiedEntity<AccountAttributes> getByIdExclusive(Long accountId) {
        final MySQLAccountAttributeRecord record = mapper.selectByIdExclusive(accountId);
        if (record != null) {
            final AccountAttributes accountAttributes = converter.fromJson(record.getData(), AccountAttributes.class);
            return new IdentifiedEntity<>(accountId, accountAttributes);
        } else {
            return null;
        }
    }

    @Override
    public Long put(IdentifiedEntity<AccountAttributes> attributes) {
        final MySQLAccountAttributeRecord record = new MySQLAccountAttributeRecord();
        record.setId(attributes.getId());
        record.setAccountNumber(attributes.getEntity().getAccountNumber());
        record.setIban(attributes.getEntity().getIban());
        record.setUuid(attributes.getEntity().getAccountUUID());
        final String json = converter.toJson(attributes.getEntity());
        record.setData(json);
        if (this.mapper.selectById(attributes.getId()) == null) {
            return this.mapper.insert(record);
        } else {
            return this.mapper.update(record);
        }
    }

    @Override
    public void commit() {
        this.session.commit();
    }
}
