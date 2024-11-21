package com.shop.generic.purchasingservice.cucumber.configurations;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("cucumber")
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0) //Random port
public class CucumberSpringConfiguration {

}
