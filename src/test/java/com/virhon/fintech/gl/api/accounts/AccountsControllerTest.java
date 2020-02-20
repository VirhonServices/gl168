package com.virhon.fintech.gl.api.accounts;

import com.google.gson.Gson;
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
public class AccountsControllerTest extends AbstractTestNGSpringContextTests {
    private Gson gson = new Gson();

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void openNewAccountSuccessTest() throws Exception {
        final NewAccountRequestBody request = new NewAccountRequestBody();
        request.setAccType("PASSIVE");
        request.setAccNumber("26003000078365");
        request.setIban("UA5630529926003000078365");
        request.setCurrency("UAH");
        final String req = gson.toJson(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/accounts")
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.accNumber").value("26003000078365"));
    }

    @Test
    public void openNewAccountFailedNullTest() throws Exception {
        final NewAccountRequestBody request = new NewAccountRequestBody();
        request.setAccNumber("26003000078365");
        request.setIban("UA5630529926003000078365");
        request.setCurrency("UAH");
        final String req = gson.toJson(request);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/accounts")
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code").value(910));
    }

}