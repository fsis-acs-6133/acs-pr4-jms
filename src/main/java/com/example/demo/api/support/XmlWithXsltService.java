package com.example.demo.api.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class XmlWithXsltService {

    private final XmlMapper xmlMapper;

    public String toXmlWithXslt(Object body, String xsltHref) {
        try {
            String xml = xmlMapper.writeValueAsString(body);
            return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<?xml-stylesheet type=\"text/xsl\" href=\"" + xsltHref + "\"?>\n"
                    + xml;
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не удалось сериализовать XML", e);
        }
    }
}
