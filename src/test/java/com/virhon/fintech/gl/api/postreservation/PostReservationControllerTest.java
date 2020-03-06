package com.virhon.fintech.gl.api.postreservation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.APIConfig;
import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.api.SeparatedDate;
import com.virhon.fintech.gl.api.reservefunds.NewReservationRequest;
import com.virhon.fintech.gl.api.reservefunds.NewReservationResponse;
import com.virhon.fintech.gl.signature.SignatureChecker;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
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
        final String token = SignatureChecker.calculateToken(date, req, "53b179afe1b7e001b3e881a31e0ddee7c2063f71");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                                    .header(config.CLIENT_UUID_HEADER, "9a0fd125-2e7e-486c-8884-97e4275adf90")
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
        final String token1 = SignatureChecker.calculateToken(date1, req1, "53b179afe1b7e001b3e881a31e0ddee7c2063f71");
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/gl/uah/reservations/".concat(response.getUuid()))
                .header(config.CLIENT_UUID_HEADER, "9a0fd125-2e7e-486c-8884-97e4275adf90")
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
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                .header(config.CLIENT_UUID_HEADER, "9a0fd125-2e7e-486c-8884-97e4275adf90")
                .header(config.SIGNATURE_HEADER, "wrong")
                .header(config.DATE_HEADER, "wrong")
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();
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
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/gl/uah/reservations/".concat("null"))
                .header(config.CLIENT_UUID_HEADER, "9a0fd125-2e7e-486c-8884-97e4275adf90")
                .header(config.SIGNATURE_HEADER, "wrong")
                .header(config.DATE_HEADER, date1)
                .content(req1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andReturn();
    }
}