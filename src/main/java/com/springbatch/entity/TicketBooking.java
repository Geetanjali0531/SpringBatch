package com.springbatch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ticket_booking")
public class TicketBooking {

    @Id
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String tolocation;
    private String fromlocation;
    private Date date;
    private String time;
    private Double price;
    private Integer busno;


}