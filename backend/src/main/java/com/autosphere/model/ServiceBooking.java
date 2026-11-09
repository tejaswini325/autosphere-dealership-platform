package com.autosphere.model;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity @Table(name="service_bookings") public class ServiceBooking {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String name,email,phone,registration,serviceType,workshop,slotTime;
 @Column(nullable=false) public LocalDate bookingDate;
 public String status="REQUESTED"; public boolean consentGiven;
 public ServiceBooking(){}
}
