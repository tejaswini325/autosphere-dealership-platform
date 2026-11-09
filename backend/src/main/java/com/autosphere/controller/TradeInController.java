package com.autosphere.controller;
import com.autosphere.model.TradeInRequest;import com.autosphere.repository.TradeInRepository;
import jakarta.validation.Valid;import jakarta.validation.constraints.*;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;import java.util.Map;
@RestController @RequestMapping("/api/trade-in") public class TradeInController {
 private final TradeInRepository repo;public TradeInController(TradeInRepository r){repo=r;}
 public record Request(@NotBlank String name,@NotBlank String phone,@Email String email,@NotBlank String makeModel,@NotBlank String year,@NotBlank String kilometres,@NotBlank String condition,Long expectedValue,@AssertTrue boolean consentGiven){}
 @PostMapping public ResponseEntity<?> create(@Valid @RequestBody Request r){TradeInRequest t=new TradeInRequest();t.name=r.name();t.phone=r.phone();t.email=r.email();t.makeModel=r.makeModel();t.year=r.year();t.kilometres=r.kilometres();t.condition=r.condition();t.expectedValue=r.expectedValue();t.consentGiven=r.consentGiven();return ResponseEntity.status(201).body(repo.save(t));}
}
