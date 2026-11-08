package com.autosphere.model;
import jakarta.persistence.*;
@Entity @Table(name="app_users") public class AppUser {@Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;@Column(nullable=false) public String name;@Column(nullable=false,unique=true) public String email;@Column(nullable=false) public String password;@Enumerated(EnumType.STRING) @Column(nullable=false) public Role role;public AppUser(){}public AppUser(String n,String e,String p,Role r){name=n;email=e;password=p;role=r;}}
