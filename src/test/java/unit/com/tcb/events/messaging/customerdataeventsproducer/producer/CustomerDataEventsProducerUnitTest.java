package com.tcb.events.messaging.customerdataeventsproducer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcb.events.messaging.customerdataeventsproducer.domain.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.types.Field;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;
import scala.Int;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerDataEventsProducerUnitTest {

    @Mock
    KafkaTemplate<Integer,String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    CustomerDataEventsProducer eventProducer;

    @Test
    void sendLibraryEvent_Approach2_failure() throws JsonProcessingException, ExecutionException, InterruptedException {
        //given
        Customer book = Customer.builder()
                .customerId(123)
                .customerAddress("Arindam")
                .customerName("Kafka")
                .build();

        CustomerDataEvent libraryEvent = CustomerDataEvent.builder()
                .customerDataEventId(null)
                .customer(book)
                .build();
        SettableListenableFuture future = new SettableListenableFuture();

        future.setException(new RuntimeException("Exception Calling Kafka"));
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        //when

        assertThrows(Exception.class, ()->eventProducer.sendCustomerDataEvent_Approach2(libraryEvent).get());

    }

    @Test
    void sendLibraryEvent_Approach2_success() throws JsonProcessingException, ExecutionException, InterruptedException {
        //given
        Customer book = Customer.builder()
                .customerId(123)
                .customerAddress("Arindam")
                .customerName("Kafka")
                .build();

        CustomerDataEvent libraryEvent = CustomerDataEvent.builder()
                .customerDataEventId(null)
                .customer(book)
                .build();
        String record = objectMapper.writeValueAsString(libraryEvent);
        SettableListenableFuture future = new SettableListenableFuture();

        ProducerRecord<Integer, String> producerRecord = new ProducerRecord("customer-data-events", libraryEvent.getCustomerDataEventId(),record );
        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("customer-data-events", 1),
                1,1,342,System.currentTimeMillis(), 1, 2);
        SendResult<Integer, String> sendResult = new SendResult<Integer, String>(producerRecord,recordMetadata);

        future.set(sendResult);
        when(kafkaTemplate.send(isA(ProducerRecord.class))).thenReturn(future);
        //when

        ListenableFuture<SendResult<Integer,String>> listenableFuture =  eventProducer.sendCustomerDataEvent_Approach2(libraryEvent);

        //then
        SendResult<Integer,String> sendResult1 = listenableFuture.get();
        assert sendResult1.getRecordMetadata().partition()==1;

    }
}
