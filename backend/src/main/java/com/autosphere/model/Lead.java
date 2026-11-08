package com.autosphere.model;
import jakarta.persistence.*;import java.time.Instant;
@Entity @Table(name="leads") public class Lead {@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;@Column(nullable=false) public String name;@Column(nullable=false) public String email;public String phone;@Column(length=2000) public String message;public String vehicleModel;public String status="NEW";public String assignedTo="sales@autosphere.demo";public Instant createdAt=Instant.now();public Lead(){} }
