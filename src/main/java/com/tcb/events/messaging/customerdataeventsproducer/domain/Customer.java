package com.tcb.events.messaging.customerdataeventsproducer.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Customer {
    @NotNull
    private Integer customerId;
    @NotBlank
    private String customerName;
    @NotBlank
    private String customerAddress;
 	private Integer customerPhone;
}
