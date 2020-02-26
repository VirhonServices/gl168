package com.virhon.fintech.gl.repo.mysql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.virhon.fintech.gl.GsonConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public abstract class MySQLAbstactRepo<T> {
    private T mapper;
    private String tablename;
    private Gson converter = GsonConverter.create();

    protected MySQLAbstactRepo(String tablename, Class<T> aClass) throws IOException {
        this.tablename = tablename;
        this.mapper = MySQLStorageConnection.getInstance().getSession().getMapper(aClass);
    }

    public String getTablename() {
        return this.tablename;
    }

    public T getMapper() {
        return this.mapper;
    }

    public Gson getConverter() {
        return converter;
    }
}
