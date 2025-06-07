package com.exp.controller;

import com.exp.service.XmlValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/validation")
public class SpotXmlValidationController {
    private static final String VALIDATION_SUCCESS_MESSAGE = "XML is valid";

    private final XmlValidationService validationService;

    @Autowired
    public SpotXmlValidationController(XmlValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping("/spot")
    public ResponseEntity<ValidationResponse> validateSpotXml(
            @RequestParam("xmlPath") String xmlPath,
            @RequestParam("xsdPath") String xsdPath) {
        validateInputPaths(xmlPath, xsdPath);

        try {
            List<String> errors = validationService.validateEachElement(xmlPath, xsdPath,
                    new XmlValidationService.ValidationConfig("FACS", "SPOT", "pos"));
            return createValidationResponse(errors);
        } catch (Exception e) {
            throw new XmlValidationException("Failed to validate XML", e);
        }
    }

    private void validateInputPaths(String xmlPath, String xsdPath) {
        if (!StringUtils.hasText(xmlPath) || !StringUtils.hasText(xsdPath)) {
            throw new IllegalArgumentException("XML and XSD paths must not be empty");
        }
    }

    private ResponseEntity<ValidationResponse> createValidationResponse(List<String> errors) {
        if (errors.isEmpty()) {
            return ResponseEntity.ok(new ValidationResponse(true, VALIDATION_SUCCESS_MESSAGE, null));
        }
        return ResponseEntity.badRequest().body(new ValidationResponse(false, null, errors));
    }

    record ValidationResponse(boolean valid, String message, List<String> errors) {}

    static class XmlValidationException extends RuntimeException {
        public XmlValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}