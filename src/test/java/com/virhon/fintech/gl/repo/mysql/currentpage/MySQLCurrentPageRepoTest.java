package com.virhon.fintech.gl.repo.mysql.currentpage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.virhon.fintech.gl.model.CurrentPage;
import com.virhon.fintech.gl.model.CurrentPageTest;
import com.virhon.fintech.gl.model.Post;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class MySQLCurrentPageRepoTest {
    private MySQLCurrentPageRepo repo = new MySQLCurrentPageRepo();

    private Gson gson = null;

    public MySQLCurrentPageRepoTest() throws IOException {
        gson = new GsonBuilder()
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

    @Test
    void gsonTest() {
        final String json2 = gson.toJson(LocalDate.now());
        final LocalDate deserialized = gson.fromJson(json2, LocalDate.class);

        final String json3 = gson.toJson(ZonedDateTime.now(ZoneId.systemDefault()));
        final ZonedDateTime desDate  = gson.fromJson(json3, ZonedDateTime.class);

        final Post post = new Post(100L, ZonedDateTime.now(), LocalDate.now(), new BigDecimal("777.77"));
        final String json = gson.toJson(post);
        final Post post1 = gson.fromJson(json, Post.class);
        Assert.assertTrue(true);
    }

    @Test(enabled = false)
    void testCreating() {
        for (long i=0;i<100;i++) {
            final CurrentPage page = CurrentPageTest.createTestPage();
            final IdentifiedEntity<CurrentPage> identifiedPage = new IdentifiedEntity<CurrentPage>(i, page);
            repo.put(identifiedPage);
        }
        repo.commit();
    }

    @Test
    void testGetting() {
        final IdentifiedEntity<CurrentPage> identifiedPage = repo.getById(55L);
        Assert.assertNotNull(identifiedPage);
        Assert.assertNotNull(identifiedPage.getEntity());
        Assert.assertEquals(identifiedPage.getEntity().getPosts().size(), 85);
    }
}