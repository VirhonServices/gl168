package com.virhon.fintech.gl.repo.mysql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
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

public abstract class MySQLAbstactRepo<T> {
    public static final String CONFIGURATION_XML = "mybatis/mybatis-config.xml";
    private InputStream inputStream = Resources.getResourceAsStream(CONFIGURATION_XML);
    private SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(inputStream, getProperties());
    private SqlSession session = sqlMapper.openSession(TransactionIsolationLevel.READ_COMMITTED);
    private T mapper;

    private String tablename;

    private Gson converter = new GsonBuilder()
            .registerTypeAdapter(ZonedDateTime.class, new TypeAdapter<ZonedDateTime>() {
                @Override
                public void write(JsonWriter out, ZonedDateTime value) throws IOException {
                    if (value != null) {
                        out.value(value.toString());
                    } else {
                        out.value("null");
                    }
                }
                @Override
                public ZonedDateTime read(JsonReader in) throws IOException {
                    if (in != null) {
                        return ZonedDateTime.parse(in.nextString());
                    } else {
                        return null;
                    }
                }
            })
            .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                @Override
                public void write(JsonWriter out, LocalDate value) throws IOException {
                    if (value != null) {
                        out.value(value.toString());
                    } else {
                        out.value("null");
                    }
                }
                @Override
                public LocalDate read(JsonReader in) throws IOException {
                    if (in != null) {
                        return LocalDate.parse(in.nextString());
                    } else {
                        return null;
                    }
                }
            })
            .serializeNulls()
            .enableComplexMapKeySerialization()
            .create();

    protected MySQLAbstactRepo(String tablename, Class<T> aClass) throws IOException {
        this.tablename = tablename;
        this.mapper = this.session.getMapper(aClass);
    }

    public String getTablename() {
        return this.tablename;
    }

    public SqlSession getSession() {
        return this.session;
    }

    public T getMapper() {
        return this.mapper;
    }

    public Gson getConverter() {
        return converter;
    }
}
