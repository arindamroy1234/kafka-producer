package com.tcb.events.messaging.customerdataeventsproducer.controller;

import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tcb.events.messaging.customerdataeventsproducer.domain.CustomerDataEvent;
import com.tcb.events.messaging.customerdataeventsproducer.domain.CustomerDataEventsType;
import com.tcb.events.messaging.customerdataeventsproducer.producer.CustomerDataEventsProducer;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CustomerDataEventsController {

    @Autowired
    CustomerDataEventsProducer customerDataEventProducer;

    @PostMapping("/v1/customerdataevent")
    public ResponseEntity<CustomerDataEvent> postCustomerDataEvent(@RequestBody @Valid CustomerDataEvent customerDataEvent) throws JsonProcessingException, ExecutionException, InterruptedException {

        //invoke kafka producer
    	customerDataEvent.setCustomerDataEventType(CustomerDataEventsType.NEW);
    	//log.info("customerDataEvent : {} ", customerDataEvent.toString());
    	customerDataEventProducer.sendCustomerDataEvent_Approach2(customerDataEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(customerDataEvent);
    }

    //PUT
    @PutMapping("/v1/customerdataevent")
    public ResponseEntity<?> putCustomerDataEvent(@RequestBody @Valid CustomerDataEvent customerDataEvent) throws JsonProcessingException, ExecutionException, InterruptedException {


        if(customerDataEvent.getCustomerDataEventId()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the CustomerDataEventId");
        }
        //log.info("customerDataEvent : {} ", customerDataEvent.toString());
        

        customerDataEvent.setCustomerDataEventType(CustomerDataEventsType.UPDATE);
        customerDataEventProducer.sendCustomerDataEvent_Approach2(customerDataEvent);
        return ResponseEntity.status(HttpStatus.OK).body(customerDataEvent);
    }
}
