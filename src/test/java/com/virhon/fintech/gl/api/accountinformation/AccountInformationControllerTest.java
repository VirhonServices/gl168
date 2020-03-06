package com.virhon.fintech.gl.api.accountinformation;

import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.APIConfig;
import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
import com.virhon.fintech.gl.signature.SignatureChecker;
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
public class AccountInformationControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TestDataMacros macros;

    @Autowired
    private MySQLGeneralLedger gl;

    @Autowired
    private APIConfig config;

    private MockMvc mockMvc;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGet200() throws Exception {
        final String accountUuid = macros.getObjectUuid("PASSIVE_SINGLE_HISTORY2");
        final Account account = gl.getLedger("UAH").getExistingByUuid(accountUuid);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final String token = SignatureChecker.calculateToken(date, "", "53b179afe1b7e001b3e881a31e0ddee7c2063f71");
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/gl/uah/accounts/".concat(accountUuid))
                .header(config.CLIENT_UUID_HEADER, "9a0fd125-2e7e-486c-8884-97e4275adf90")
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accNumber").value(account.getAttributes().getEntity().getAccountNumber()));
    }

    @Test
    public void testGetInvalidCurrency() throws Exception {
        final String accountUuid = macros.getObjectUuid("PASSIVE_SINGLE_HISTORY2");
        final Account account = gl.getLedger("UAH").getExistingByUuid(accountUuid);
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final String token = SignatureChecker.calculateToken(date, "", "53b179afe1b7e001b3e881a31e0ddee7c2063f71");
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/gl/bhd/accounts/".concat(accountUuid))
                .header(config.CLIENT_UUID_HEADER, "9a0fd125-2e7e-486c-8884-97e4275adf90")
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetInvalidAccount() throws Exception {
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final String token = SignatureChecker.calculateToken(date, "", "53b179afe1b7e001b3e881a31e0ddee7c2063f71");
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/gl/uah/accounts/wrong-uuid")
                .header(config.CLIENT_UUID_HEADER, "9a0fd125-2e7e-486c-8884-97e4275adf90")
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(300));
    }

    @Test
    public void testUnauthorized() throws Exception {
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/gl/uah/accounts/wrong-uuid")
                .header(config.CLIENT_UUID_HEADER, "wrong client")
                .header(config.SIGNATURE_HEADER, "wrong token")
                .header(config.DATE_HEADER, date)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}