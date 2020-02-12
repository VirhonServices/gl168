package com.virhon.fintech.gl.repo.mysql.historicalpage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.virhon.fintech.gl.model.Page;
import com.virhon.fintech.gl.repo.HistPageRepo;
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
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getProperties;

public class MySQLHistoricalPageRepo implements HistPageRepo {
    public static final String CONFIGURATION_XML = "mybatis/mybatis-config.xml";
    private InputStream inputStream = Resources.getResourceAsStream(CONFIGURATION_XML);
    private SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(inputStream, getProperties());
    private SqlSession session = sqlMapper.openSession(TransactionIsolationLevel.READ_COMMITTED);
    private MySQLHitoricalPageDAO mapper = this.session.getMapper(MySQLHitoricalPageDAO.class);

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

    public MySQLHistoricalPageRepo() throws IOException {
    }


    @Override
    public IdentifiedEntity<Page> getById(Long id) {
        final MySQLHistoricalPageRecord record = this.mapper.selectById(id);
        if (record!=null) {
            final Page page = this.converter.fromJson(record.getData(), Page.class);
            return new IdentifiedEntity<>(id, page);
        }
        return null;
    }

    @Override
    public IdentifiedEntity<Page> getByAccountId(Long accountId, ZonedDateTime at) {
        final MySQLHistoricalPageRecord record = this.mapper.selectByAccountId(accountId, at);
        if (record!=null) {
            final Page page = this.converter.fromJson(record.getData(), Page.class);
            return new IdentifiedEntity<>(record.getId(), page);
        }
        return null;
    }

    @Deprecated
    public List<IdentifiedEntity<Page>> getReporetedHistory(Long accountId, LocalDate from, LocalDate to) {
        final List<MySQLHistoricalPageRecord> records = this.mapper.selectReportedHistory(accountId, from, to);
        if (!records.isEmpty()) {
            final List<IdentifiedEntity<Page>> result = new ArrayList<>();
            records.forEach(r -> {
                final Page page = this.converter.fromJson(r.getData(), Page.class);
                result.add(new IdentifiedEntity<Page>(r.getId(), page));
            });
            return result;
        }
        return null;
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
        final String data = this.converter.toJson(page);
        record.setData(data);
        this.mapper.update(record);
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
        final String data = this.converter.toJson(page);
        record.setData(data);
        this.mapper.insert(record);
        return record.getId();
    }

    @Override
    public void commit() {
        this.session.commit();
    }
}