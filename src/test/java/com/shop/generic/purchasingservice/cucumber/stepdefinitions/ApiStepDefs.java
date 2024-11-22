package com.shop.generic.purchasingservice.cucumber.stepdefinitions;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.shop.generic.common.clock.GsClock;
import com.shop.generic.purchasingservice.cucumber.configurations.CucumberSpringConfiguration;
import com.shop.generic.purchasingservice.cucumber.enums.Service;
import com.shop.generic.purchasingservice.repositories.ProductPurchaseReserveRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Slf4j
public class ApiStepDefs extends CucumberSpringConfiguration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProductPurchaseReserveRepository productPurchaseReserveRepository;

    @MockBean
    private GsClock clock;

    private MvcResult mvcResult;
    private MockHttpServletResponse response;

    @Given("initial setup is complete")
    public void initialSetupIsComplete() {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Given("the service is up and running")
    public void setup() throws Exception {
//        this.mockMvc.perform(get("/actuator/health").contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("UP"));
    }

    @And("now is {string}")
    public void nowIs(final String dateTime) {
        when(this.clock.getClock()).thenReturn(
                Clock.fixed(Instant.parse(dateTime), ZoneId.systemDefault()));
    }

    @When("a POST request is sent to {string} with data")
    public void aPOSTRequestIsSentToWithData(final String resource, final String body)
            throws Exception {
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post(resource)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andReturn();
    }

    @Then("a successful response is generated with a {int} status and a body similar to")
    public void aResponseIsGeneratedWithAStatusAndABodySimilarTo(
            final int expectedStatus,
            final String expectedBody) throws Exception {
        response = mvcResult.getResponse();
        log.info(response.getContentAsString());
        assertEquals(expectedStatus, response.getStatus());
        JSONAssert.assertEquals(expectedBody, response.getContentAsString(), new CustomComparator(
                JSONCompareMode.LENIENT,
                new Customization("result.orderId", (o1, o2) -> true)
        ));
    }

    @And("the purchase reservation is removed from the database")
    public void thePurchaseReservationIsRemovedFromTheDatabase() {
        assertEquals(0, productPurchaseReserveRepository.count());
    }

    @Given("the {string} get API {string} returns")
    public void theProductServiceAPIReturns(final String service, final String api,
            final String data) {

        final Service serviceName = Service.valueOf(service.toUpperCase());
        final String url = getUrl(serviceName, api);

        stubFor(get(urlEqualTo(url)).atPriority(2)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(data.replaceAll("\r", "").replaceAll("\n", ""))
                )
        );
    }

    @And("the {string} post API {string} returns")
    public void thePostAPIReturns(final String service, final String api,
            final String data) {

        final Service serviceName = Service.valueOf(service.toUpperCase());
        final String url = getUrl(serviceName, api);

        stubFor(post(urlEqualTo(url)).atPriority(2)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(data.replaceAll("\r", "").replaceAll("\n", ""))
                )
        );
    }

    private String getUrl(final Service service, final String api) {
        return service.getContextPath() + api;
        //TODO: Use switch statement here instead. This solution is temp for testing purposes
//        if (Objects.requireNonNull(service) == Service.PRODUCTS) {
//            return service.getContextPath() + api;
//        } else {
//            throw new RuntimeException("Could not create the mock url");
//        }
    }
}