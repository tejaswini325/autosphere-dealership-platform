package com.autosphere.model;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="trade_in_requests") public class TradeInRequest {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String name,phone,makeModel,year, kilometres,condition;
 public String email; public Long expectedValue; public boolean consentGiven; public Instant createdAt=Instant.now();
 public TradeInRequest(){}
}
