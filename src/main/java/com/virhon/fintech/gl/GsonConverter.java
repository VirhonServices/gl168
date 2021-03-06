package com.virhon.fintech.gl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class GsonConverter {
    private GsonConverter() {
    }

    public static Gson create() {
        final Gson converter = new GsonBuilder()
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
                        final String value = in.nextString();
                        if (value != null && !value.toLowerCase().equals("null")) {
                            return ZonedDateTime.parse(value);
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
                        final String value = in.nextString();
                        if (value != null && !value.toLowerCase().equals("null")) {
                            return LocalDate.parse(value);
                        } else {
                            return null;
                        }
                    }
                })
                .serializeNulls()
                .enableComplexMapKeySerialization()
                .create();
        return converter;
    }
}
