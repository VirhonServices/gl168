package com.virhon.fintech.gl.repo.mysql.currentpage;

import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Page;
import com.virhon.fintech.gl.model.PageTest;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.mysql.MySQLStorageConnection;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class MySQLCurrentPageRepoTest {
    private MySQLCurrentPageRepo repo = new MySQLCurrentPageRepo("uah_current_page");

    public MySQLCurrentPageRepoTest() throws IOException {
    }

    @Test(enabled = false)
    void testCreating() throws LedgerException, IOException {
        for (long i=0;i<100;i++) {
            final Page page = PageTest.createTestPage();
            final IdentifiedEntity<Page> identifiedPage = new IdentifiedEntity<Page>(i, page);
            repo.put(identifiedPage);
        }
        MySQLStorageConnection.getInstance().commit();
    }

    @Test(enabled = false)
    void testGetting() {
        final IdentifiedEntity<Page> identifiedPage = repo.getById(55L);
        Assert.assertNotNull(identifiedPage);
        Assert.assertNotNull(identifiedPage.getEntity());
        Assert.assertEquals(identifiedPage.getEntity().getPosts().size(), 85);
    }
}