package com.autosphere.model;
import jakarta.persistence.*;
@Entity @Table(name="dealer_content") public class DealerContent {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String type,title;
 @Column(length=3000) public String body;
 public String imageUrl; public boolean active=true;
 public DealerContent(){}
}
