package com.autosphere.controller;
import com.autosphere.model.ServiceBooking;import com.autosphere.repository.ServiceBookingRepository;
import jakarta.validation.Valid;import jakarta.validation.constraints.*;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;import java.util.Map;
@RestController @RequestMapping("/api/service") public class ServiceController {
 private final ServiceBookingRepository repo; public ServiceController(ServiceBookingRepository r){repo=r;}
 public record Request(@NotBlank String name,@Email String email,@NotBlank String phone,@NotBlank String registration,@NotBlank String serviceType,@NotBlank String workshop,@NotNull LocalDate bookingDate,@NotBlank String slotTime,@AssertTrue boolean consentGiven){}
 @PostMapping("/bookings") public ResponseEntity<?> book(@Valid @RequestBody Request r){
  if(r.bookingDate().isBefore(LocalDate.now()))return ResponseEntity.badRequest().body(Map.of("message","Choose a future date."));
  if(repo.countByWorkshopAndBookingDateAndSlotTimeAndStatusNot(r.workshop(),r.bookingDate(),r.slotTime(),"CANCELLED")>=3)return ResponseEntity.status(409).body(Map.of("message","This workshop slot is full. Choose another slot."));
  ServiceBooking b=new ServiceBooking();b.name=r.name();b.email=r.email();b.phone=r.phone();b.registration=r.registration();b.serviceType=r.serviceType();b.workshop=r.workshop();b.bookingDate=r.bookingDate();b.slotTime=r.slotTime();b.consentGiven=r.consentGiven();
  return ResponseEntity.status(201).body(repo.save(b));
 }
}
