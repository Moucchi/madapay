package com.moucchi.madapayrafidinjanaharyemmanuel.configuration;

import com.moucchi.madapayrafidinjanaharyemmanuel.businessObject.Compte;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class DataConf{
    private final ObjectMapper mapper;

    public DataConf(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Bean
    public List<Compte> loadComptes() throws IOException {
        InputStream seed = new ClassPathResource("contrat/seed.json").getInputStream();

        JsonNode root = mapper.readTree(seed);
        JsonNode comptesNode = root.get("comptes");

        return mapper.convertValue(comptesNode, new TypeReference<List<Compte>>() {});
    }
}
