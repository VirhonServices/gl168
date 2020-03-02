package com.virhon.fintech.gl.api.balanceat;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.Application;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
public class BalanceAtControllerTest extends AbstractTestNGSpringContextTests {
    private Gson gson = GsonConverter.create();

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private TestDataMacros macros;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void test200() throws Exception {
        final BalanceAtRequestBody request = new BalanceAtRequestBody();
        request.setYear(2020);
        request.setMonth(2);
        request.setDay(21);
        request.setHour(15);
        request.setMinute(43);
        request.setSecond(59);
        request.setNanoOfSecond(0);
        final String req = gson.toJson(request);
        final String accNumber = macros.getObjectUuid("PASSIVE_EMPTY5");
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/".concat(accNumber).concat("/posting/balance"))
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.balance").value(0)
        );
    }

    @Test
    void testWrongCurrency() throws Exception {
        final BalanceAtRequestBody request = new BalanceAtRequestBody();
        request.setYear(2020);
        request.setMonth(2);
        request.setDay(21);
        request.setHour(15);
        request.setMinute(43);
        request.setSecond(59);
        request.setNanoOfSecond(0);
        final String req = gson.toJson(request);
        final String accNumber = macros.getObjectUuid("PASSIVE_EMPTY0");
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/bhd/accounts/".concat(accNumber).concat("/posting/balance"))
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code").value(150)
                );
    }

    @Test
    void testWrongAccount() throws Exception {
        final BalanceAtRequestBody request = new BalanceAtRequestBody();
        request.setYear(2020);
        request.setMonth(2);
        request.setDay(21);
        request.setHour(15);
        request.setMinute(43);
        request.setSecond(59);
        request.setNanoOfSecond(0);
        final String req = gson.toJson(request);
        final String accNumber = macros.getObjectUuid("PASSIVE_EMPTY0");
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts/wrong-account/posting/balance")
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code").value(300));
    }

}