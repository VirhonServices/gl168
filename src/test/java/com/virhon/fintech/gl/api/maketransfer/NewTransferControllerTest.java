package com.virhon.fintech.gl.api.maketransfer;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.APIConfig;
import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.api.SeparatedDate;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.model.AccountAttributes;
import com.virhon.fintech.gl.model.GeneralLedger;
import com.virhon.fintech.gl.model.Ledger;
import com.virhon.fintech.gl.security.Authorizer;
import com.virhon.fintech.gl.security.SignatureChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
public class NewTransferControllerTest extends AbstractTestNGSpringContextTests {
    private Gson gson = GsonConverter.create();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    APIConfig config;

    private MockMvc mockMvc;

    @Autowired
    private TestDataMacros macros;

    @Autowired
    private GeneralLedger gl;

    @Autowired
    private Authorizer authorizer;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void test201() throws Exception {
        final BigDecimal amount = new BigDecimal("5000.00");
        final BigDecimal repAmount = new BigDecimal("200.00");
        final SeparatedDate reportedOn = new SeparatedDate();
        reportedOn.setYear(2020);
        reportedOn.setMonth(2);
        reportedOn.setDay(21);
        final NewTransferRequestBody request = new NewTransferRequestBody();
        request.setTransferRef("TRANSFER-001");
        final String debitUuid = macros.getObjectUuid("ACTIVE2");
        final String creditUuid = macros.getObjectUuid("PASSIVE_EMPTY0");
        request.setClientCustomerId("ClientCustomerId must be here");
        request.setCreditAccountUuid(creditUuid);
        request.setAmount(amount);
        request.setRepAmount(repAmount);
        request.setReportedOn(reportedOn);
        request.setDescription("Test transfer #111");
        final String req = gson.toJson(request);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final Ledger ledger = gl.getLedger("UAH");
        final Account debit = ledger.getExistingByUuid(debitUuid);
        final AccountAttributes attr = debit.getAttributes().getEntity();
        final String clientUuid = attr.getClientUuid();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(debitUuid).concat("/transfers"))
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.credit.accUuid").value(creditUuid)
                );
        // TODO: 22.02.20 check balances after test data added
    }

    @Test
    void testNPE() throws Exception {
        final BigDecimal amount = new BigDecimal("5000.00");
        final BigDecimal repAmount = new BigDecimal("200.00");
        final SeparatedDate reportedOn = new SeparatedDate();
        reportedOn.setYear(2020);
        reportedOn.setMonth(2);
        reportedOn.setDay(15);
        final NewTransferRequestBody request = new NewTransferRequestBody();
        request.setTransferRef("TRANSFER-001");
        final String debitUuid = macros.getObjectUuid("ACTIVE2");
        final String creditUuid = macros.getObjectUuid("PASSIVE_EMPTY0");
        request.setClientCustomerId("ClientCustomerId must be here");
        request.setCreditAccountUuid(creditUuid);
        request.setAmount(amount);
        request.setRepAmount(repAmount);
        request.setReportedOn(null);
        request.setDescription("Test transfer #111");
        final String req = gson.toJson(request);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final Ledger ledger = gl.getLedger("UAH");
        final Account debit = ledger.getExistingByUuid(debitUuid);
        final AccountAttributes attr = debit.getAttributes().getEntity();
        final String clientUuid = attr.getClientUuid();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(debitUuid).concat("/transfers"))
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(160)
                );
    }

    @Test
    void testRedSaldo() throws Exception {
        final BigDecimal amount = new BigDecimal("5000.00");
        final BigDecimal repAmount = new BigDecimal("200.00");
        final SeparatedDate reportedOn = new SeparatedDate();
        reportedOn.setYear(2020);
        reportedOn.setMonth(2);
        reportedOn.setDay(15);
        final NewTransferRequestBody request = new NewTransferRequestBody();
        request.setTransferRef("TRANSFER-001");
        request.setTransferRef("TRANSFER-001");
        final String debitUuid = macros.getObjectUuid("PASSIVE_EMPTY1");
        final String creditUuid = macros.getObjectUuid("ACTIVE2");
        request.setClientCustomerId("ClientCustomerId must be here");
        request.setCreditAccountUuid(creditUuid);
        request.setAmount(amount);
        request.setRepAmount(repAmount);
        request.setReportedOn(reportedOn);
        request.setDescription("Test transfer #111");
        final String req = gson.toJson(request);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final Ledger ledger = gl.getLedger("UAH");
        final Account debit = ledger.getExistingByUuid(debitUuid);
        final AccountAttributes attr = debit.getAttributes().getEntity();
        final String clientUuid = attr.getClientUuid();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(debitUuid).concat("/transfers"))
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200)
                );
    }

    @Test
    void testAccessDenied() throws Exception {
        final BigDecimal amount = new BigDecimal("5000.00");
        final BigDecimal repAmount = new BigDecimal("200.00");
        final SeparatedDate reportedOn = new SeparatedDate();
        reportedOn.setYear(2020);
        reportedOn.setMonth(2);
        reportedOn.setDay(15);
        final NewTransferRequestBody request = new NewTransferRequestBody();
        request.setTransferRef("TRANSFER-001");
        request.setTransferRef("TRANSFER-001");
        final String debitUuid = macros.getObjectUuid("ACTIVE1");
        final String creditUuid = macros.getObjectUuid("ACTIVE2");
        request.setClientCustomerId("ClientCustomerId must be here");
        request.setCreditAccountUuid(creditUuid);
        request.setAmount(amount);
        request.setRepAmount(repAmount);
        request.setReportedOn(reportedOn);
        request.setDescription("Test transfer #111");
        final String req = gson.toJson(request);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final Ledger ledger = gl.getLedger("UAH");
        final Account debit = ledger.getExistingByUuid(debitUuid);
        final AccountAttributes attr = debit.getAttributes().getEntity();
        final String clientUuid = authorizer.get(3).getKey();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(debitUuid).concat("/transfers"))
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, "wrong")
                .header(config.DATE_HEADER, date)
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUnauthorized() throws Exception {
        final BigDecimal amount = new BigDecimal("5000.00");
        final BigDecimal repAmount = new BigDecimal("200.00");
        final SeparatedDate reportedOn = new SeparatedDate();
        reportedOn.setYear(2020);
        reportedOn.setMonth(2);
        reportedOn.setDay(15);
        final NewTransferRequestBody request = new NewTransferRequestBody();
        request.setTransferRef("TRANSFER-001");
        request.setTransferRef("TRANSFER-001");
        final String debitUuid = macros.getObjectUuid("ACTIVE1");
        final String creditUuid = macros.getObjectUuid("ACTIVE2");
        request.setClientCustomerId("ClientCustomerId must be here");
        request.setCreditAccountUuid(creditUuid);
        request.setAmount(amount);
        request.setRepAmount(repAmount);
        request.setReportedOn(reportedOn);
        request.setDescription("Test transfer #111");
        final String req = gson.toJson(request);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final Ledger ledger = gl.getLedger("UAH");
        final Account debit = ledger.getExistingByUuid(debitUuid);
        final AccountAttributes attr = debit.getAttributes().getEntity();
        final String clientUuid = attr.getClientUuid();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(debitUuid).concat("/transfers"))
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, "wrong")
                .header(config.DATE_HEADER, date)
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}