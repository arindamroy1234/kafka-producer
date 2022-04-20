package com.tcb.events.messaging.customerdataeventsproducer.controller;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcb.events.messaging.customerdataeventsproducer.domain.Customer;
import com.tcb.events.messaging.customerdataeventsproducer.domain.CustomerDataEvent;
import com.tcb.events.messaging.customerdataeventsproducer.producer.CustomerDataEventsProducer;

@WebMvcTest(CustomerDataEventsController.class)
@AutoConfigureMockMvc
public class CustomerDataEventsControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    CustomerDataEventsProducer customerDataEventProducer;

    @Test
    void postLibraryEvent() throws Exception {
        //given
        Customer customer = Customer.builder()
                .customerId(123)
                .customerName("Kafka using Spring Boot")
                .customerAddress("Richardson")
                .customerPhone(123456)
                .build();

        CustomerDataEvent libraryEvent = CustomerDataEvent.builder()
                .customerDataEventId(null)
                .customer(customer)
                .build();

        String json = objectMapper.writeValueAsString(libraryEvent);
        when(customerDataEventProducer.sendCustomerDataEvent_Approach2(isA(CustomerDataEvent.class))).thenReturn(null);

        //expect
        mockMvc.perform(post("/v1/customerdataevent")
        .content(json)
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }
/*
    @Test
    void postLibraryEvent_4xx() throws Exception {
        //given

        Customer customer = Customer.builder()
                .customerId(null)
                .customerAddress(null)
                .customerName("Kafka using Spring Boot")
                .customerPhone(123456)
                .build();

        CustomerDataEvent libraryEvent = CustomerDataEvent.builder()
                .customerDataEventId(null)
                .customer(customer)
                .build();

        String json = objectMapper.writeValueAsString(libraryEvent);
        when(customerDataEventProducer.sendCustomerDataEvent_Approach2(isA(CustomerDataEvent.class))).thenReturn(null);
        //expect
        String expectedErrorMessage = "customer.customerAddress - must not be blank, customer.customerId - must not be null";
        mockMvc.perform(post("/v1/customerdataevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
        .andExpect(content().string(expectedErrorMessage));

    }*/

    @Test
    void updateLibraryEvent() throws Exception {

        //given
        Customer customer = new Customer().builder()
                .customerId(123)
                .customerAddress("Richardson")
                .customerName("Kafka Using Spring Boot")
                .customerPhone(123456)
                .build();

        CustomerDataEvent libraryEvent = CustomerDataEvent.builder()
                .customerDataEventId(123)
                .customer(customer)
                .build();
        String json = objectMapper.writeValueAsString(libraryEvent);
        when(customerDataEventProducer.sendCustomerDataEvent_Approach2(isA(CustomerDataEvent.class))).thenReturn(null);

        //expect
        mockMvc.perform(
                put("/v1/customerdataevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void updateLibraryEvent_withNullLibraryEventId() throws Exception {

        //given
        Customer customer = new Customer().builder()
                .customerId(123)
                .customerAddress("Richardson")
                .customerName("Kafka Using Spring Boot")
                .customerPhone(123456)
                .build();

        CustomerDataEvent libraryEvent = CustomerDataEvent.builder()
                .customerDataEventId(null)
                .customer(customer)
                .build();
        String json = objectMapper.writeValueAsString(libraryEvent);
        when(customerDataEventProducer.sendCustomerDataEvent_Approach2(isA(CustomerDataEvent.class))).thenReturn(null);

        //expect
        mockMvc.perform(
                put("/v1/customerdataevent")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
        .andExpect(content().string("Please pass the CustomerDataEventId"));

    }
}
