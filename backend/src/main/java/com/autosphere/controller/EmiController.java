package com.autosphere.controller;
import org.springframework.web.bind.annotation.*;import java.util.Map;
@RestController @RequestMapping("/api/emi") public class EmiController {
 @GetMapping public Map<String,Object> calculate(@RequestParam long principal,@RequestParam double annualRate,@RequestParam int months){
  if(principal<=0||annualRate<0||months<=0||months>480)throw new IllegalArgumentException("Enter a positive loan amount, non-negative rate and 1–480 months.");
  double monthly=annualRate/1200.0;double emi=monthly==0?principal/(double)months:principal*monthly*Math.pow(1+monthly,months)/(Math.pow(1+monthly,months)-1);
  return Map.of("monthlyEmi",Math.round(emi),"totalPayment",Math.round(emi*months),"interest",Math.round(emi*months-principal),"disclaimer","Illustrative estimate only; lender terms and fees may differ.");
 }
}
