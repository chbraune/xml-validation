package com.exp.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class XmlValidationService {

    public record ValidationConfig(
            String rootElement,
            String repeatingElement,
            String identifierTag
    ) {
    }

    public List<String> validateEachElement(String xmlPath, String xsdPath, ValidationConfig config) throws Exception {
        List<String> invalidPosList = new ArrayList<>();

        // Read the complete document
        File xmlFile = new File(xmlPath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);

        NodeList spotNodes = doc.getElementsByTagName(config.repeatingElement);

        // Prepare schema
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new File(xsdPath));
        Validator validator = schema.newValidator();

        for (int i = 0; i < spotNodes.getLength(); i++) {
            Element spot = (Element) spotNodes.item(i);

            // Generate single SPOT as string
            String spotXml = nodeToString(spot, config.repeatingElement);

            try {
                // Pre-construct the XML string using StringBuilder
                StringBuilder xmlBuilder = new StringBuilder()
                        .append('<').append(config.rootElement).append('>')
                        .append(spotXml)
                        .append("</").append(config.rootElement).append('>');

                // Validate using the constructed string
                validator.validate(new StreamSource(new StringReader(xmlBuilder.toString())));

            } catch (SAXException e) {
                // On error: get position and extract failing tag from error message
                String pos = getTextContentSafe(spot, config.identifierTag);
                String errorMessage = e.getMessage();
                String problematicValue = findProblematicValue(errorMessage);
                String problematicTag = findTagWithValue(spot, problematicValue);


                // Add both position and failing tag to the error message
                invalidPosList.add(String.format("Position %s - Field <%s>: %s",
                        pos,
                        problematicTag != null ? problematicTag : "unknown",
                        errorMessage));

            }
        }
        return invalidPosList;
    }


    private String findProblematicValue(String errorMessage) {
        // Extract value from error message that's typically enclosed in quotes
        int start = errorMessage.indexOf("'");
        int end = errorMessage.indexOf("'", start + 1);
        if (start >= 0 && end > start) {
            return errorMessage.substring(start + 1, end);
        }
        return null;
    }

    private String findTagWithValue(Element parent, String value) {
        if (value == null) return null;

        NodeList children = parent.getElementsByTagName("*");
        for (int i = 0; i < children.getLength(); i++) {
            Element child = (Element) children.item(i);
            if (value.equals(child.getTextContent().trim())) {
                return child.getTagName();
            }
        }
        return null;
    }


    private String getTextContentSafe(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent().trim();
        }
        return "[unknown]";
    }

    private String nodeToString(Element element, String repeatingElement) {
        StringBuilder sb = new StringBuilder();
        sb.append("<" + repeatingElement + ">");
        NodeList children = element.getChildNodes();
        for (int j = 0; j < children.getLength(); j++) {
            if (children.item(j) instanceof Element) {
                Element child = (Element) children.item(j);
                sb.append("<").append(child.getTagName()).append(">");
                sb.append(child.getTextContent().trim());
                sb.append("</").append(child.getTagName()).append(">");
            }
        }
        sb.append("</" + repeatingElement + ">");
        return sb.toString();
    }
}