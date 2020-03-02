package com.virhon.fintech.gl;

import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.exception.LedgerException;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.AccountType;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.model.Transfer;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

// Класс ожидает что база данных существует но таблицы пусты
@SpringBootTest(classes = Application.class)
public class TestDataCreator extends AbstractTestNGSpringContextTests {
    final static Logger LOGGER = Logger.getLogger(TestDataCreator.class);
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MySQLGeneralLedger gl;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test(enabled = false)
    public void generateUahTestData() throws LedgerException, IOException, URISyntaxException {
        final Map<String, String> macros = new HashMap<>();
        final Ledger ledger = gl.getLedger("UAH");
        final List<String> uuids = Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
        // 1. Пассивные счета без истории и оборотов
        String area = "1";
        Integer count = 10;
        for (Integer i=0;i<count;i++) {
            final String accountNumber = "2600".concat(area).concat("000").concat(i.toString());
            final String iban = "UA11305299".concat(accountNumber);
            final Account account = ledger.openNew(uuids.get(i/uuids.size()), "clientCustomerId",
                    accountNumber, iban, AccountType.PASSIVE);
            final String key = "PASSIVE_EMPTY".concat(i.toString());
            macros.put(key, account.getAttributes().getEntity().getAccountUUID());
        }
        // 2. Активные счета
        area = "2";
        count = 3;
        for (Integer i=0;i<count;i++) {
            final String accountNumber = "1001".concat(area).concat("000").concat(i.toString());
            final String iban = "UA22305299".concat(accountNumber);
            final Account account = ledger.openNew(uuids.get(i/uuids.size()), "clientCustomerId",
                    accountNumber, iban, AccountType.ACTIVE);
            final String key = "ACTIVE".concat(i.toString());
            macros.put(key, account.getAttributes().getEntity().getAccountUUID());
        }

        final String debitAccountUuid = macros.get("ACTIVE1");

        // 3. Пассивные счета с оборотами но без истории
        area = "3";
        count = 10;
        for (Integer i=0;i<count;i++) {
            final String accountNumber = "2601".concat(area).concat("000").concat(i.toString());
            final String iban = "UA33305299".concat(accountNumber);
            final Account account = ledger.openNew(uuids.get(i/uuids.size()), "clientCustomerId",
                    accountNumber, iban, AccountType.PASSIVE);
            final String key = "PASSIVE_CURRENT".concat(i.toString());
            macros.put(key, account.getAttributes().getEntity().getAccountUUID());
        }

        macros.put("START_DATE", ZonedDateTime.now().toString());
        // Transfers
        for (Integer i=0;i<count;i++) {
            final String creditAccountMacro = "PASSIVE_CURRENT".concat(i.toString());
            final String creditAccountUuid = macros.get(creditAccountMacro);
            final int repDaySize = count/3;
            for (Integer j=0;j<count;j++) {
                final Integer trRef = i*count+j;
                final BigDecimal amount = new BigDecimal(trRef).divide(new BigDecimal("100.00"));
                final BigDecimal repAmount = amount.multiply(new BigDecimal("24.15"));
                final ZonedDateTime postedAt = ZonedDateTime.now().plusDays(j/repDaySize).plusHours(8);
                final LocalDate repDate = LocalDate.now().plusDays(j/repDaySize);
                ledger.transferFunds("AUTO-".concat(trRef.toString()), uuids.get(j/repDaySize), "Client###",
                        debitAccountUuid, creditAccountUuid, amount, repAmount, postedAt, repDate,"Autogenerated transfer");
            }
        }

        // 4. Пассивный счет с одностраничной историей
        area = "4";
        count = 10;
        for (Integer i=0;i<count;i++) {
            final String accountNumber = "2602".concat(area).concat("000").concat(i.toString());
            final String iban = "UA44305299".concat(accountNumber);
            final Account account = ledger.openNew(uuids.get(i/uuids.size()), "clientCustomerId",
                    accountNumber, iban, AccountType.PASSIVE);
            final String key = "PASSIVE_SINGLE_HISTORY".concat(i.toString());
            macros.put(key, account.getAttributes().getEntity().getAccountUUID());
        }
        // Transfers
        for (Integer i=0;i<count;i++) {
            final String creditAccountMacro = "PASSIVE_SINGLE_HISTORY".concat(i.toString());
            final String creditAccountUuid = macros.get(creditAccountMacro);
            final int trCount = Config.getInstance().getMaxNumPostsInBlock() + count;
            final int repDaySize = trCount/3;
            for (Integer j=0;j<trCount;j++) {
                final Integer trRef = i*count+j;
                final BigDecimal amount = new BigDecimal(trRef).divide(new BigDecimal("100.00"));
                final BigDecimal repAmount = amount.multiply(new BigDecimal("24.15"));
                final ZonedDateTime postedAt = ZonedDateTime.now().plusDays(j/repDaySize).plusHours(8);
                final LocalDate repDate = LocalDate.now().plusDays(j/repDaySize);
                ledger.transferFunds("AUTO-".concat(trRef.toString()), uuids.get(j/repDaySize), "Client###",
                        debitAccountUuid, creditAccountUuid, amount, repAmount, postedAt, repDate,"Autogenerated transfer");
            }
        }

        // 5. Пассивный счет с многостраничной историей
        area = "5";
        count = 10;
        for (Integer i=0;i<count;i++) {
            final String accountNumber = "2603".concat(area).concat("000").concat(i.toString());
            final String iban = "UA55305299".concat(accountNumber);
            final Account account = ledger.openNew(uuids.get(i/uuids.size()), "clientCustomerId",
                    accountNumber, iban, AccountType.PASSIVE);
            final String key = "PASSIVE_MULTI_HISTORY".concat(i.toString());
            macros.put(key, account.getAttributes().getEntity().getAccountUUID());
        }
        // Transfers
        Transfer tr = null;
        for (Integer i=0;i<count;i++) {
            final String creditAccountMacro = "PASSIVE_MULTI_HISTORY".concat(i.toString());
            final String creditAccountUuid = macros.get(creditAccountMacro);
            final int trCount = Config.getInstance().getMaxNumPostsInBlock()*(i+1) + count;
            final int repDaySize = trCount/3;
            for (Integer j=0;j<trCount;j++) {
                final Integer trRef = i*count+j;
                final BigDecimal amount = new BigDecimal(trRef).divide(new BigDecimal("100.00"));
                final BigDecimal repAmount = amount.multiply(new BigDecimal("24.15"));
                final Integer minus = j/repDaySize;
                final ZonedDateTime postedAt = ZonedDateTime.now().plusDays(j/repDaySize).plusHours(8);
                final LocalDate repDate = LocalDate.now().plusDays(minus);
                tr = ledger.transferFunds("AUTO-".concat(trRef.toString()), uuids.get(j/repDaySize), "Client###",
                        debitAccountUuid, creditAccountUuid, amount, repAmount, postedAt, repDate,"Autogenerated transfer");
            }
        }
        if (tr != null) {
            macros.put("TRANSFER", tr.getTransferUuid());
        }
        // 6. Сохранить изменения в БД
        gl.commit();

        // 7. Сохранить счета в файл
        PrintWriter writer = new PrintWriter(new File("macros.lst"));
        final String testAccounts = macros.toString();
        writer.write(testAccounts);
        writer.flush();
        writer.close();
    }
}
// select account_id,aa.uuid,account_number, count(account_id) from uah_account_attribute aa, uah_historical_page h where h.account_id = aa.id group by account_id, aa.uuid, account_number order by 4;
