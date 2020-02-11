package com.virhon.fintech.gl.repo.mysql.accountattribute;

import com.virhon.fintech.gl.model.AccountAttributes;
import com.virhon.fintech.gl.model.AccountType;
import com.virhon.fintech.gl.repo.IdentifiedEntity;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;

public class MySQLAttrRepoTest {
    private MySQLAttrRepo repo = new MySQLAttrRepo();

    public MySQLAttrRepoTest() throws IOException {
    }

    @Test
    void testGetByIdDummy() {
        final IdentifiedEntity<AccountAttributes> attr = repo.getById(1L);
        Assert.assertNotNull(attr);
        final IdentifiedEntity<AccountAttributes> attrNull = repo.getById(-1L);
        Assert.assertNull(attrNull);
    }

    @Test(enabled = false)
    void createAttributes() {
        final Random generator = new Random();
        for (long i=0; i<10; i++) {
            final Long counter = 877 + generator.nextInt(100) + i * generator.nextInt(10000);
            final String accountNumber = "2600100".concat(counter.toString());
            final String iban = "UA6730587".concat(accountNumber);
            final AccountAttributes attributes = AccountAttributes.createNew(accountNumber,iban, AccountType.PASSIVE);
            attributes.setBalance(new BigDecimal(i));
            final IdentifiedEntity<AccountAttributes> identifiedEntity = new IdentifiedEntity<>(i, attributes);
            final Long id = this.repo.put(identifiedEntity);
            Assert.assertNotNull(id);
        }
        this.repo.commit();
    }

    @Test
    void testGetById() {
        final IdentifiedEntity<AccountAttributes> attr = repo.getById(25L);
        Assert.assertNotNull(attr);
        Assert.assertNotNull(attr.getEntity().getAccountType());
        Assert.assertNotNull(attr.getEntity().getAccountNumber());
        Assert.assertNotNull(attr.getEntity().getAccountUUID());
        Assert.assertNotNull(attr.getEntity().getIban());
        Assert.assertNotNull(attr.getEntity().getBalance());
        Assert.assertNotNull(attr.getEntity().getReservedAmount());
    }

    @Test
    void testGetByIdExclusive() {
        final IdentifiedEntity<AccountAttributes> attr = repo.getByIdExclusive(26L);
        Assert.assertNotNull(attr);
    }
}