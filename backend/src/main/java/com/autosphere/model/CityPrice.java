package com.autosphere.model;
import jakarta.persistence.*;
@Entity @Table(name="city_prices",uniqueConstraints=@UniqueConstraint(columnNames={"vehicle_id","city"})) public class CityPrice {@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;@ManyToOne(optional=false) public Vehicle vehicle;@Column(nullable=false) public String city;public long roadTax;public long insurance;public long accessories;public CityPrice(){}public CityPrice(Vehicle v,String c,long t,long i,long a){vehicle=v;city=c;roadTax=t;insurance=i;accessories=a;}}
