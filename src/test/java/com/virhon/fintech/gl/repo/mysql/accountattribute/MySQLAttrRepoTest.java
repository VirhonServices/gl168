package com.virhon.fintech.gl.repo.mysql.accountattribute;

import com.virhon.fintech.gl.model.AccountAttributes;
import com.virhon.fintech.gl.model.AccountType;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import com.virhon.fintech.gl.repo.mysql.MySQLStorageConnection;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;

public class MySQLAttrRepoTest {
    private MySQLAttrRepo repo = new MySQLAttrRepo("uah_account_attribute");

    public MySQLAttrRepoTest() throws IOException {
    }

    @Test(enabled = false)
    void testGetByIdDummy() {
        final IdentifiedEntity<AccountAttributes> attr = repo.getById(21L);
        Assert.assertNotNull(attr);
        final IdentifiedEntity<AccountAttributes> attrNull = repo.getById(-1L);
        Assert.assertNull(attrNull);
    }

    @Test(enabled = false)
    void createAttributes() throws IOException {
        final Random generator = new Random();
        for (long i=0; i<10; i++) {
            final Long counter = 877 + generator.nextInt(100) + i * generator.nextInt(10000);
            final String accountNumber = "2600100".concat(counter.toString());
            final String iban = "UA6730587".concat(accountNumber);
            final AccountAttributes attributes = AccountAttributes.createNew(accountNumber,iban, AccountType.PASSIVE);
            attributes.setBalance(new BigDecimal(i));
            final IdentifiedEntity<AccountAttributes> identifiedEntity = new IdentifiedEntity<>(i, attributes);
            final Long id = this.repo.insert(attributes);
            Assert.assertNotNull(id);
        }
        MySQLStorageConnection.getInstance().commit();
    }

}