package com.virhon.fintech.gl.repo.mysql.accountattribute;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
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
import java.time.LocalDate;
import java.time.ZonedDateTime;

import static java.lang.System.getProperties;

public class MySQLAttrRepo implements AttrRepo {
    public static final String CONFIGURATION_XML = "mybatis/mybatis-config.xml";
    private InputStream inputStream = Resources.getResourceAsStream(CONFIGURATION_XML);
    private SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(inputStream, getProperties());
    private SqlSession session = sqlMapper.openSession(TransactionIsolationLevel.READ_COMMITTED);
    private MySQLAccountAttributeDAO mapper = this.session.getMapper(MySQLAccountAttributeDAO.class);

    private Gson converter = new GsonBuilder()
            .registerTypeAdapter(ZonedDateTime.class, new TypeAdapter<ZonedDateTime>() {
                @Override
                public void write(JsonWriter out, ZonedDateTime value) throws IOException {
                    out.value(value.toString());
                }

                @Override
                public ZonedDateTime read(JsonReader in) throws IOException {
                    return ZonedDateTime.parse(in.nextString());
                }
            })
            .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                @Override
                public void write(JsonWriter out, LocalDate value) throws IOException {
                    out.value(value.toString());
                }

                @Override
                public LocalDate read(JsonReader in) throws IOException {
                    return LocalDate.parse(in.nextString());
                }
            })
            .enableComplexMapKeySerialization()
            .create();

    public MySQLAttrRepo() throws IOException {
    }

    @Override
    public IdentifiedEntity<AccountAttributes> getById(Long accountId) {
        final MySQLAccountAttributeRecord record = this.mapper.selectById(accountId);
        if (record != null) {
            final AccountAttributes accountAttributes = this.converter.fromJson(record.getData(), AccountAttributes.class);
            return new IdentifiedEntity<>(accountId, accountAttributes);
        } else {
            return null;
        }
    }

    @Override
    public IdentifiedEntity<AccountAttributes> getByIdExclusive(Long accountId) {
        final MySQLAccountAttributeRecord record = this.mapper.selectByIdExclusive(accountId);
        if (record != null) {
            final AccountAttributes accountAttributes = this.converter.fromJson(record.getData(), AccountAttributes.class);
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
        final String json = this.converter.toJson(attributes.getEntity());
        record.setData(json);
        this.mapper.update(record);
    }

    @Override
    public Long insert(AccountAttributes attributes) {
        final MySQLAccountAttributeRecord record = new MySQLAccountAttributeRecord();
        record.setAccountNumber(attributes.getAccountNumber());
        record.setIban(attributes.getIban());
        record.setUuid(attributes.getAccountUUID());
        final String json = this.converter.toJson(attributes);
        record.setData(json);
        this.mapper.insert(record);
        return record.getId();
    }

    @Override
    public void commit() {
        this.session.commit();
    }
}
