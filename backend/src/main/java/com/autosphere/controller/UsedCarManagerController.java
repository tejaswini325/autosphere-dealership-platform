package com.autosphere.controller;
import com.autosphere.model.Vehicle;import com.autosphere.repository.VehicleRepository;import com.autosphere.repository.UsedCarSaleRepository;import org.springframework.web.bind.annotation.*;import java.util.*;
@RestController @RequestMapping("/api/used-cars") public class UsedCarManagerController {
 private final VehicleRepository vehicles;private final UsedCarSaleRepository sales;
 public UsedCarManagerController(VehicleRepository v,UsedCarSaleRepository s){vehicles=v;sales=s;}
 @GetMapping("/{id}/price-suggestion") public Map<String,Object> suggestion(@PathVariable Long id){
  Vehicle v=vehicles.findById(id).orElseThrow();
  if(!"USED".equalsIgnoreCase(v.type))throw new IllegalArgumentException("Price suggestions are only available for used vehicles.");
  var comps=sales.findByModelIgnoreCaseAndYearBetweenAndKilometresBetween(v.model,v.year-1,v.year+1,Math.max(0,v.kilometres-20000),v.kilometres+20000);
  if(comps.isEmpty())return Map.of("available",false,"message","No comparable historical sales found for this model, year and kilometre range.");
  long[] values=comps.stream().mapToLong(x->x.soldPrice).sorted().toArray();
  return Map.of("available",true,"basis","Synthetic demo historical sales; human review required","sampleCount",values.length,"low",values[0],"high",values[values.length-1],"median",values[values.length/2]);
 }
}
