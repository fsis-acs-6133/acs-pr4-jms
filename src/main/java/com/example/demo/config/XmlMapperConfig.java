package com.example.demo.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class XmlMapperConfig {

    @Bean
    public XmlMapper xmlMapper() {
        XmlMapper mapper = XmlMapper.builder().build();
        mapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, false);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }
}
