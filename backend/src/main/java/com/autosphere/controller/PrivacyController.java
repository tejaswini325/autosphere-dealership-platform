package com.autosphere.controller;
import com.autosphere.repository.LeadRepository;import jakarta.validation.Valid;import org.springframework.web.bind.annotation.*;import java.util.Map;
@RestController @RequestMapping("/api/privacy") public class PrivacyController {
 private final LeadRepository leads;public PrivacyController(LeadRepository r){leads=r;}
 public record OptOut(String email,String phone){}
 @PostMapping("/opt-out") public Map<String,Object> optOut(@Valid @RequestBody OptOut r){if((r.email()==null||r.email().isBlank())&&(r.phone()==null||r.phone().isBlank()))return Map.of("updated",0,"message","Provide an email or phone number.");var matches=leads.findByEmailIgnoreCaseOrPhone(r.email()==null?"":r.email(),r.phone()==null?"":r.phone());matches.forEach(l->l.optedOut=true);leads.saveAll(matches);return Map.of("updated",matches.size(),"message","Opt-out preference saved for matching enquiries.");}
}
