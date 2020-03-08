package com.virhon.fintech.gl.api.accounts;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.api.APIConfig;
import com.virhon.fintech.gl.api.Application;
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

import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
public class AccountsControllerTest extends AbstractTestNGSpringContextTests {
    private Gson gson = GsonConverter.create();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private APIConfig config;

    @Autowired
    private Authorizer authorizer;

    private MockMvc mockMvc;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void openNewAccountSuccessTest() throws Exception {
        final NewAccountRequestBody request = new NewAccountRequestBody();
        request.setAccType("PASSIVE");
        request.setClientCustomerId("CLIENT_CUSTOMER_ID");
        request.setAccNumber("26003000078365");
        request.setIban("UA5630529926003000078365");
        final String req = gson.toJson(request);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final String clientUuid = authorizer.get(1).getKey();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts")
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accNumber").value("26003000078365"));
    }

    @Test
    public void openNewAccountFailedNullTest() throws Exception {
        final NewAccountRequestBody request = new NewAccountRequestBody();
        request.setAccNumber("26003000078365");
        request.setIban("UA5630529926003000078365");
        final String req = gson.toJson(request);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final String clientUuid = authorizer.get(1).getKey();
        final String digest = authorizer.getDigest(clientUuid);
        final String token = SignatureChecker.calculateToken(date, req, digest);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts")
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(160));
    }

    @Test
    public void testUnauthorized() throws Exception {
        final NewAccountRequestBody request = new NewAccountRequestBody();
        request.setAccNumber("26003000078365");
        request.setIban("UA5630529926003000078365");
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final String req = gson.toJson(request);
        final String clientUuid = authorizer.get(1).getKey();
        final String digest = authorizer.getDigest(clientUuid);
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/gl/uah/accounts")
                .header(config.CLIENT_UUID_HEADER, clientUuid)
                .header(config.SIGNATURE_HEADER, "wrong")
                .header(config.DATE_HEADER, "wrong")
                .content(req)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}