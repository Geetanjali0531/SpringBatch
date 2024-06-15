package com.springbatch.config;


import com.springbatch.entity.TicketBooking;
import org.springframework.batch.item.ItemProcessor;

public class TicketBookingProcessor implements ItemProcessor <TicketBooking,TicketBooking>{


    @Override
    public TicketBooking process(TicketBooking ticketBooking) throws Exception {
        return ticketBooking;
    }

}
