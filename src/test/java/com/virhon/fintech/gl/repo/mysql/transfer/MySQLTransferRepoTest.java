package com.virhon.fintech.gl.repo.mysql.transfer;

import com.virhon.fintech.gl.model.Transfer;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.mysql.MySQLStorageSession;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class MySQLTransferRepoTest {
    @Test(enabled = false)
    void test() throws IOException {
        final List<Long> ids = new ArrayList<>();
        final MySQLTransferRepo repo = new MySQLTransferRepo("transfer");
        for (int i=0;i<10;i++) {
            final Transfer transfer = new Transfer();
            transfer.setTransferRef("ref-".concat(new Integer(i).toString()));
            transfer.setPostedAt(ZonedDateTime.now());
            transfer.setReportedOn(LocalDate.now());
            transfer.setAmount(BigDecimal.valueOf(i+2.37374));
            transfer.setLocalAmount(BigDecimal.valueOf(i+2.37374));
            transfer.setDescription("Transfer #".concat(Integer.valueOf(i).toString()));
            final IdentifiedEntity<Transfer> iTr = repo.insert(transfer);
            ids.add(iTr.getId());
        }
        MySQLStorageSession.getInstance().commit();
        final Long id = ids.get(3);
        final Transfer tr = repo.getById(id).getEntity();
        Assert.assertEquals(tr.getDescription(), "Transfer #3");
    }
}