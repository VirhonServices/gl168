package com.virhon.fintech.gl.repo.mysql.currentpage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.virhon.fintech.gl.model.Page;
import com.virhon.fintech.gl.repo.CurPageRepo;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.TransactionIsolationLevel;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;

import static java.lang.System.getProperties;

public class MySQLCurrentPageRepo implements CurPageRepo {
    public static final String CONFIGURATION_XML = "mybatis/mybatis-config.xml";
    private InputStream inputStream = Resources.getResourceAsStream(CONFIGURATION_XML);
    private SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(inputStream, getProperties());
    private SqlSession session = sqlMapper.openSession(TransactionIsolationLevel.READ_COMMITTED);
    private MySQLCurrentPageDAO mapper = this.session.getMapper(MySQLCurrentPageDAO.class);

    private Gson converter = null;

    public MySQLCurrentPageRepo() throws IOException {
        this.converter = new GsonBuilder()
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
                .enableComplexMapKeySerialization()
                .create();
    }

    @Override
    public IdentifiedEntity<Page> getById(Long accountId) {
        final MySQLCurrentPageRecord record = this.mapper.selectById(accountId);
        if (record != null) {
            final Page page = this.converter.fromJson(record.getData(), Page.class);
            return new IdentifiedEntity<>(accountId, page);
        } else {
            return null;
        }
    }

    @Override
    public IdentifiedEntity<Page> getByIdExclusive(Long accountId) {
        final MySQLCurrentPageRecord record = this.mapper.selectByIdExclusive(accountId);
        if (record != null) {
            final Page page = this.converter.fromJson(record.getData(), Page.class);
            return new IdentifiedEntity<>(accountId, page);
        } else {
            return null;
        }
    }

    @Override
    public void put(IdentifiedEntity<Page> page) {
        final MySQLCurrentPageRecord record = new MySQLCurrentPageRecord();
        record.setId(page.getId());
        final String json = this.converter.toJson(page.getEntity());
        record.setData(json);
        if (this.mapper.selectById(page.getId()) == null) {
            this.mapper.insert(record);
        } else {
            this.mapper.update(record);
        }
    }

    @Override
    public void commit() {
        this.session.commit();
    }
}
