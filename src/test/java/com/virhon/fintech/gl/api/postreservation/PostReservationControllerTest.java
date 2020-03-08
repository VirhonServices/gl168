package com.virhon.fintech.gl.api.postreservation;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.APIConfig;
import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.api.SeparatedDate;
import com.virhon.fintech.gl.api.reservefunds.NewReservationRequest;
import com.virhon.fintech.gl.api.reservefunds.NewReservationResponse;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
public class PostReservationControllerTest extends AbstractTestNGSpringContextTests {
    private Gson gson = GsonConverter.create();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TestDataMacros macros;

    private MockMvc mockMvc;

    @Autowired
    APIConfig config;

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
        final BigDecimal amount = new BigDecimal("300.00");
        final NewReservationRequest request = new NewReservationRequest();
        request.setTransferRef("RESERVATION-REF-MANUAL-TEST-1");
        request.setClientCustomerId("ClientCustomerId");
        request.setCreditAccountUuid(macros.getObjectUuid("PASSIVE_EMPTY9"));
        request.setAmount(amount);
        request.setDescription("Manual test reservation");
        final String req = gson.toJson(request);
        final String debitUuid =  macros.getObjectUuid("ACTIVE2");
        final String url = "/v1/gl/uah/accounts/".concat(debitUuid).concat("/reservations");
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final Ledger ledger = gl.getLedger("UAH");
        final Account debit = ledger.getExistingByUuid(debitUuid);
        final AccountAttributes attr = debit.getAttributes().getEntity();
        final String clientUuid = attr.getClientUuid();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                                    .header(config.CLIENT_UUID_HEADER, clientUuid)
                                    .header(config.SIGNATURE_HEADER, token)
                                    .header(config.DATE_HEADER, date)
                                    .content(req)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isCreated())
                                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                    .andExpect(jsonPath("$.transferRef").value("RESERVATION-REF-MANUAL-TEST-1")
                                    ).andReturn();
        final String resultString = result.getResponse().getContentAsString();
        final NewReservationResponse response = gson.fromJson(resultString, NewReservationResponse.class);
        final PostReservationRequest request1 = new PostReservationRequest();
        request1.setRepAmount(new BigDecimal("7443.00"));
        final SeparatedDate reportedOn = new SeparatedDate();
        reportedOn.setYear(2020);
        reportedOn.setMonth(2);
        reportedOn.setDay(23);
        request1.setReportedOn(reportedOn);
        final String req1 = gson.toJson(request1);
        final String date1 = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final Account debit1 = ledger.getExistingByUuid(debitUuid);
        final AccountAttributes attr1 = debit1.getAttributes().getEntity();
        final String clientUuid1 = attr1.getClientUuid();
        final String digest1 = authorizer.getDigest(clientUuid1);
        final String token1 = SignatureChecker.calculateToken(date1, req1, digest1);
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/gl/uah/reservations/".concat(response.getUuid()))
                .header(config.CLIENT_UUID_HEADER, clientUuid1)
                .header(config.SIGNATURE_HEADER, token1)
                .header(config.DATE_HEADER, date1)
                .content(req1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transferRef").value("RESERVATION-REF-MANUAL-TEST-1")
                );
    }

    @Test
    void testUnauthorized() throws Exception {
        final BigDecimal amount = new BigDecimal("300.00");
        final NewReservationRequest request = new NewReservationRequest();
        request.setTransferRef("RESERVATION-REF-MANUAL-TEST-1");
        request.setClientCustomerId("ClientCustomerId");
        request.setCreditAccountUuid(macros.getObjectUuid("PASSIVE_EMPTY9"));
        request.setAmount(amount);
        request.setDescription("Manual test reservation");
        final String req = gson.toJson(request);
        final String debitUuid =  macros.getObjectUuid("ACTIVE2");
        final String url = "/v1/gl/uah/accounts/".concat(debitUuid).concat("/reservations");
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final Ledger ledger = gl.getLedger("UAH");
        final Account debit = ledger.getExistingByUuid(debitUuid);
        final AccountAttributes attr = debit.getAttributes().getEntity();
        final String clientUuid = attr.getClientUuid();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transferRef").value("RESERVATION-REF-MANUAL-TEST-1")
                ).andReturn();
        final String resultString = result.getResponse().getContentAsString();
        final NewReservationResponse response = gson.fromJson(resultString, NewReservationResponse.class);
        final PostReservationRequest request1 = new PostReservationRequest();
        request1.setRepAmount(new BigDecimal("7443.00"));
        final SeparatedDate reportedOn = new SeparatedDate();
        reportedOn.setYear(2020);
        reportedOn.setMonth(2);
        reportedOn.setDay(23);
        request1.setReportedOn(reportedOn);
        final String req1 = gson.toJson(request1);
        final String date1 = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final Account debit1 = ledger.getExistingByUuid(debitUuid);
        final AccountAttributes attr1 = debit1.getAttributes().getEntity();
        final String clientUuid1 = attr1.getClientUuid();
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/gl/uah/reservations/".concat(response.getUuid()))
                .header(config.CLIENT_UUID_HEADER, clientUuid1)
                .header(config.SIGNATURE_HEADER, "wrong")
                .header(config.DATE_HEADER, date1)
                .content(req1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void testAccessDenied() throws Exception {
        final BigDecimal amount = new BigDecimal("300.00");
        final NewReservationRequest request = new NewReservationRequest();
        request.setTransferRef("RESERVATION-REF-MANUAL-TEST-1");
        request.setClientCustomerId("ClientCustomerId");
        request.setCreditAccountUuid(macros.getObjectUuid("PASSIVE_EMPTY9"));
        request.setAmount(amount);
        request.setDescription("Manual test reservation");
        final String req = gson.toJson(request);
        final String debitUuid =  macros.getObjectUuid("ACTIVE2");
        final String url = "/v1/gl/uah/accounts/".concat(debitUuid).concat("/reservations");
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final Ledger ledger = gl.getLedger("UAH");
        final Account debit = ledger.getExistingByUuid(debitUuid);
        final AccountAttributes attr = debit.getAttributes().getEntity();
        final String clientUuid = attr.getClientUuid();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transferRef").value("RESERVATION-REF-MANUAL-TEST-1")
                ).andReturn();
        final String resultString = result.getResponse().getContentAsString();
        final NewReservationResponse response = gson.fromJson(resultString, NewReservationResponse.class);
        final PostReservationRequest request1 = new PostReservationRequest();
        request1.setRepAmount(new BigDecimal("7443.00"));
        final SeparatedDate reportedOn = new SeparatedDate();
        reportedOn.setYear(2020);
        reportedOn.setMonth(2);
        reportedOn.setDay(23);
        request1.setReportedOn(reportedOn);
        final String req1 = gson.toJson(request1);
        final String date1 = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final Account debit1 = ledger.getExistingByUuid(debitUuid);
        final AccountAttributes attr1 = debit1.getAttributes().getEntity();
        final String clientUuid1 = attr.getClientUuid();
        final String digest1 = authorizer.getDigest(clientUuid1);
        final String token1 = SignatureChecker.calculateToken(date, req, digest1);
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/gl/uah/reservations/".concat(response.getUuid()))
                .header(config.CLIENT_UUID_HEADER, "client")
                .header(config.SIGNATURE_HEADER, token1)
                .header(config.DATE_HEADER, date1)
                .content(req1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()).andReturn();
    }

}