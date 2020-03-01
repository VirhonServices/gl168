package com.virhon.fintech.gl.api.accountinformation;

import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.Application;
import com.virhon.fintech.gl.model.Account;
import com.virhon.fintech.gl.repo.mysql.MySQLGeneralLedger;
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
public class AccountInformationControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TestDataMacros macros;

    @Autowired
    private MySQLGeneralLedger gl;

    private MockMvc mockMvc;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGet200() throws Exception {
        final String accountUuid = macros.getObjectUuid("PASSIVE_SINGLE_HISTORY2");
        final Account account = gl.getLedger("UAH").getExistingByUuid(accountUuid);
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/gl/uah/accounts/".concat(accountUuid))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.accNumber").value(account.getAttributes().getEntity().getAccountNumber()));
    }

    @Test
    public void testGetInvalidCurrency() throws Exception {
        final String accountUuid = macros.getObjectUuid("PASSIVE_SINGLE_HISTORY2");
        final Account account = gl.getLedger("UAH").getExistingByUuid(accountUuid);
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/gl/bhd/accounts/".concat(accountUuid))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetInvalidAccount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/gl/uah/accounts/wrong-uuid")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code").value(300));
    }

}