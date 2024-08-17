package com.shop.generic.purchasingservice.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.generic.common.dtos.OrderResponseDTO;
import com.shop.generic.common.dtos.PurchaseProductDTO;
import com.shop.generic.common.enums.OrderStatus;
import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.common.rest.response.RestApiResponseFactory;
import com.shop.generic.purchasingservice.exceptions.ValidationException;
import com.shop.generic.purchasingservice.services.PurchasingService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(PurchaseController.class)
@Import(RestApiResponseFactory.class)
@AutoConfigureJsonTesters
@DisplayName("HTTP requests to the purchases controller")
class PurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchasingService purchasingService;

    @Autowired
    private RestApiResponseFactory restApiResponseFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JacksonTester<RestApiResponse<OrderResponseDTO>> jacksonTester;

    @Test
    @DisplayName("Should return a 200 OK for a successful purchase request")
    public void should_createPurchaseRequest_whenProductsAreAvailable() throws Exception {
        //Given
        final PurchaseProductDTO purchaseDTO = new PurchaseProductDTO(1, 10, BigDecimal.TEN);
        final PurchaseProductDTO purchaseDTO2 = new PurchaseProductDTO(2, 50,
                BigDecimal.valueOf(49.99));

        final List<PurchaseProductDTO> productsToPurchase = List.of(purchaseDTO, purchaseDTO2);

        final UUID id = UUID.randomUUID();
        final OrderResponseDTO orderResponseDTO = new OrderResponseDTO(id, OrderStatus.CREATED);

        final RestApiResponse<OrderResponseDTO> mockApiResponse = new RestApiResponse<>(null, null,
                orderResponseDTO,
                LocalDateTime.now());

        given(purchasingService.purchaseProducts(productsToPurchase)).willReturn(
                mockApiResponse);

        //We leave out the timestamp as that will cause the test to fail due to RestApiResponseFactory using LocalDateTime.now()
        //When that is replaced with a clock, we can mock it here instead
        final String expectedApiResponse = """
                {
                    "message": null,
                    "error": null,
                    "result": {
                        "orderId": "%s",
                        "status": "CREATED"
                    }
                }
                """.formatted(id);

        //When
        final MockHttpServletResponse response = this.mockMvc.perform(
                post("/purchase/products").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productsToPurchase))
        ).andReturn().getResponse();

        //Then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        //We use an unstrict JSONAssert here so that we can compare the json without the test failing due to the missing timestamp field in the expectedApiResponse
        JSONAssert.assertEquals(expectedApiResponse, response.getContentAsString(), false);

    }

    @Test
    @DisplayName("Should return a 400 response when a validation exception occurs")
    void should_return400_whenAValidationExceptionOccurs() throws Exception {

        given(purchasingService.purchaseProducts(any())).willThrow(
                new ValidationException("A validation error message"));

        //Another way to create the request body without using the jackson writer
        final String request = """
                [
                    {
                        "productId": "1",
                        "quantity": "10",
                        "price": "9.99"
                    },
                    {
                        "productId": "2",
                        "quantity": "1",
                        "price": "49.99"
                    }
                ]
                """;

        //We leave out the timestamp as that will cause the test to fail due to RestApiResponseFactory using LocalDateTime.now()
        //When that is replaced with a clock, we can mock it here instead
        final String expectedApiResponse = """
                {
                    "message": null,
                    "error": "A validation error message",
                    "result": null
                }
                """;

        //When
        final MockHttpServletResponse actualResponse = this.mockMvc.perform(
                post("/purchase/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        ).andReturn().getResponse();

        //Then
        assertThat(actualResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        //We use an unstrict JSONAssert here so that we can compare the json without the test failing due to the missing timestamp field in the expectedApiResponse
        JSONAssert.assertEquals(expectedApiResponse, actualResponse.getContentAsString(), false);
    }

}