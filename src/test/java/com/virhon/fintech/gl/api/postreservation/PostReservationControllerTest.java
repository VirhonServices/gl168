package com.virhon.fintech.gl.api.postreservation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.api.SeparatedDate;
import com.virhon.fintech.gl.api.reservefunds.NewReservationRequest;
import com.virhon.fintech.gl.api.reservefunds.NewReservationResponse;
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
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(url)
                                    .content(req)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON))
                                    .andExpect(status().isCreated())
                                    .andExpect(content().contentType("application/json;charset=UTF-8"))
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
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/gl/uah/reservations/".concat(response.getUuid()))
                .content(req1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.transferRef").value("RESERVATION-REF-MANUAL-TEST-1")
                );
    }
}