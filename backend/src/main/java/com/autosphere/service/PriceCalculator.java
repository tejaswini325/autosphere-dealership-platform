package com.autosphere.service;
import org.springframework.stereotype.Service;
@Service public class PriceCalculator { public long onRoadPrice(long exShowroom,long roadTax,long insurance,long accessories){if(exShowroom<0||roadTax<0||insurance<0||accessories<0)throw new IllegalArgumentException("Price components cannot be negative");return Math.addExact(Math.addExact(exShowroom,roadTax),Math.addExact(insurance,accessories));}}
