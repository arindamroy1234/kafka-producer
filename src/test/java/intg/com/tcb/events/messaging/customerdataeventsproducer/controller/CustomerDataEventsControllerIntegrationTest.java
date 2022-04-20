package com.tcb.events.messaging.customerdataeventsproducer.controller;

import com.tcb.events.messaging.customerdataeventsproducer.domain.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"customer-data-events"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class CustomerDataEventsControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @Timeout(5)
    void postcustomerdataevent() throws InterruptedException {
        //given
        Customer customer = Customer.builder()
                .customerId(123)
                .customerName("Kafka using Spring Boot")
                .customerAddress("Richardson")
                .customerPhone(1234567812)
                .build();

        CustomerDataEvent customerdataevent = CustomerDataEvent.builder()
                .customerDataEventId(null)
                .customer(customer)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<CustomerDataEvent> request = new HttpEntity<>(customerdataevent, headers);

        //when
        ResponseEntity<CustomerDataEvent> responseEntity = restTemplate.exchange("/v1/customerdataevent", HttpMethod.POST, request, CustomerDataEvent.class);

        //then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "customer-data-events");
        //Thread.sleep(3000);
        String expectedRecord = "{\"customerDataEventId\":null,\"customerDataEventType\":\"NEW\",\"customer\":{\"customerId\":123,\"customerName\":\"Kafka using Spring Boot\",\"customerAddress\":\"Richardson\",\"customerPhone\":1234567812}}";
        String value = consumerRecord.value();
        assertEquals(expectedRecord, value);

    }

    @Test
    @Timeout(5)
    void putcustomerdataevent() throws InterruptedException {
        //given
        Customer book = Customer.builder()
                .customerId(456)
                .customerAddress("Richardson")
                .customerName("Kafka using Spring Boot")
                .customerPhone(1234567812)
                .build();

        CustomerDataEvent customerdataevent = CustomerDataEvent.builder()
                .customerDataEventId(123)
                .customer(book)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<CustomerDataEvent> request = new HttpEntity<>(customerdataevent, headers);

        //when
        ResponseEntity<CustomerDataEvent> responseEntity = restTemplate.exchange("/v1/customerdataevent", HttpMethod.PUT, request, CustomerDataEvent.class);

        //then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ConsumerRecord<Integer, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "customer-data-events");
        //Thread.sleep(3000);
        String expectedRecord = "{\"customerDataEventId\":123,\"customerDataEventType\":\"UPDATE\",\"customer\":{\"customerId\":456,\"customerName\":\"Kafka using Spring Boot\",\"customerAddress\":\"Richardson\",\"customerPhone\":1234567812}}";
        String value = consumerRecord.value();
        assertEquals(expectedRecord, value);

    }
}