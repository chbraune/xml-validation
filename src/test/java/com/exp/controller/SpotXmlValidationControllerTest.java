package com.exp.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class SpotXmlValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validateSpotXml_shouldReturnOkWhenXmlIsValid() throws Exception {
        String xmlPath = getClass().getResource("/spot-valid.min.xml").getFile();
        String xsdPath = getClass().getResource("/spot.xsd").getFile();

        mockMvc.perform(post("/api/validation/spot")
                        .param("xmlPath", xmlPath)
                        .param("xsdPath", xsdPath)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"valid\":true,\"message\":\"XML is valid\",\"errors\":null}"));
    }

    @Test
    void validateSpotXml_shouldReturnBadRequestWhenXmlIsInvalid() throws Exception {
        String xmlPath = getClass().getResource("/spot-invalid.min.xml").getFile();
        String xsdPath = getClass().getResource("/spot.xsd").getFile();


        mockMvc.perform(post("/api/validation/spot")
                        .param("xmlPath", xmlPath)
                        .param("xsdPath", xsdPath)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Position 1 - Field <dateto>: cvc-datatype-valid.1.2.1: '2024-11-31' is not a valid value for 'date'.")))
                .andExpect(content().string(containsString("Position 3 - Field <unitprice>: cvc-datatype-valid.1.2.1: '345,80' is not a valid value for 'decimal'.")));
    }

    @Test
    void validateSpotXml_shouldReturnBadRequestWhenXmlPathIsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/validation/spot")
                        .param("xsdPath", "validXsdPath.xsd")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateSpotXml_shouldReturnBadRequestWhenXsdPathIsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/validation/spot")
                        .param("xmlPath", "validXmlPath.xml")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest());
    }

}