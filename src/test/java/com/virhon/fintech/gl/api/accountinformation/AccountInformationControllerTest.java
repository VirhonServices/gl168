package com.virhon.fintech.gl.api.accountinformation;

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
public class AccountInformationControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGet200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/gl/uah/accounts/03bb9d86-3716-4018-883e-78bd2222ddee")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.accNumber").value("26003000078365"));
    }

    @Test
    public void testGetInvalidCurrency() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/gl/bhd/accounts/03bb9d86-3716-4018-883e-78bd2222ddee")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.code").value(150));
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