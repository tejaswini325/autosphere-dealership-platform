package com.autosphere.model;
import jakarta.persistence.*;import java.time.LocalDate;
@Entity @Table(name="used_car_sales") public class UsedCarSale {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String model;public int year;public long kilometres;public long soldPrice;public LocalDate soldAt;
 public UsedCarSale(){}
 public UsedCarSale(String m,int y,long km,long price,LocalDate date){model=m;year=y;kilometres=km;soldPrice=price;soldAt=date;}
}
