package com.tcb.events.messaging.customerdataeventsproducer.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CustomerDataEvent {

    private Integer customerDataEventId;
    private CustomerDataEventsType customerDataEventType;
    @NotNull
    @Valid
    private Customer customer;

}
