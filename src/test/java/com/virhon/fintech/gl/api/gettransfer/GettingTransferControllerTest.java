package com.virhon.fintech.gl.api.gettransfer;

import com.google.gson.Gson;
import com.virhon.fintech.gl.GsonConverter;
import com.virhon.fintech.gl.TestDataMacros;
import com.virhon.fintech.gl.api.APIConfig;
import com.virhon.fintech.gl.api.Application;
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

import javax.print.attribute.standard.Media;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Application.class)
public class GettingTransferControllerTest extends AbstractTestNGSpringContextTests {
    private Gson gson = GsonConverter.create();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    TestDataMacros macros;

    @Autowired
    APIConfig config;

    private MockMvc mockMvc;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void test200() throws Exception {
        final String uuid = macros.getObjectUuid("TRANSFER");
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final String token = SignatureChecker.calculateToken(date, "", "53b179afe1b7e001b3e881a31e0ddee7c2063f71");
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/gl/uah/transfers/".concat(uuid))
                .header(config.CLIENT_UUID_HEADER, "9a0fd125-2e7e-486c-8884-97e4275adf90")
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount").value(new BigDecimal("10.99"))
                );
    }

    @Test
    void testInvalidTransfer() throws Exception {
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final String token = SignatureChecker.calculateToken(date, "", "53b179afe1b7e001b3e881a31e0ddee7c2063f71");
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/gl/uah/transfers/wrong-transfer")
                .header(config.CLIENT_UUID_HEADER, "9a0fd125-2e7e-486c-8884-97e4275adf90")
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(330)
                );
    }

    @Test
    void testUnauthorized() throws Exception {
        final String date = ZonedDateTime.now().format(config.DATE_HEADER_FORMAT);
        final String token = SignatureChecker.calculateToken(date, "", "53b179afe1b7e001b3e881a31e0ddee7c2063f71");
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/gl/uah/transfers/wrong-transfer")
                .header(config.CLIENT_UUID_HEADER, "wrong")
                .header(config.SIGNATURE_HEADER, token)
                .header(config.DATE_HEADER, date)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


}