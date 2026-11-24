package com.moucchi.madapayrafidinjanaharyemmanuel;

import com.moucchi.madapayrafidinjanaharyemmanuel.businessObject.Compte;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class MadapayRafidinjanaharyEmmanuelApplicationTests {
	@Autowired
	private ObjectMapper mapper;

	@Resource(name = "loadComptes")
	private List<Compte> comptes;

	@Test
	void deserializeSeed() throws IOException {
		InputStream stream = new ClassPathResource("contrat/seed.json").getInputStream();
		JsonNode seed = mapper.readTree(stream);

		String expectedData = seed.get("comptes").toString();
		String actual = mapper.valueToTree(comptes).toString();

		assertThat(actual).isEqualTo(expectedData);
	}
}
