package com.autosphere.controller;

import com.autosphere.model.Lead;
import com.autosphere.repository.LeadRepository;
import com.autosphere.service.AiDraftService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/leads")
public class LeadController {
    private final LeadRepository leads;
    private final AiDraftService ai;

    public LeadController(LeadRepository leads, AiDraftService ai) {
        this.leads = leads;
        this.ai = ai;
    }

    public record Create(
            @NotBlank String name,
            @Email String email,
            @NotBlank String phone,
            @NotBlank String message,
            String vehicleModel, String showroom, @jakarta.validation.constraints.AssertTrue boolean consentGiven
    ) {}

    @PostMapping
    public ResponseEntity<Lead> create(@Valid @RequestBody Create request) {
        Lead lead = new Lead();
        lead.name = request.name();
        lead.email = request.email();
        lead.phone = request.phone();
        lead.message = request.message();
        lead.vehicleModel = request.vehicleModel();
        lead.showroom = request.showroom()==null || request.showroom().isBlank() ? "Lucknow" : request.showroom();
        lead.consentGiven = request.consentGiven();
        lead.assignedTo = assign(lead.showroom, lead.vehicleModel);
        lead.acknowledgement = "SIMULATED_SMS: Enquiry received and assigned to " + lead.assignedTo + ". Target response: five minutes.";
        return ResponseEntity.status(201).body(leads.save(lead));
    }

    @GetMapping
    public java.util.List<Lead> all() {
        return leads.findAllByOrderByCreatedAtDesc();
    }

    public record StatusUpdate(
            @Pattern(regexp = "NEW|CONTACTED|QUALIFIED|CLOSED") String status
    ) {}

    @PatchMapping("/{id}/status")
    public Lead status(@PathVariable Long id, @Valid @RequestBody StatusUpdate request) {
        Lead lead = leads.findById(id).orElseThrow();
        lead.status = request.status();
        return leads.save(lead);
    }

    @GetMapping("/{id}/summary")
    public Map<String, String> summary(@PathVariable Long id) {
        Lead lead = leads.findById(id).orElseThrow();
        return Map.of("summary", ai.summary(lead), "provider", ai.providerName());
    }

    @PostMapping("/{id}/ai-draft")
    public Map<String, String> draft(@PathVariable Long id) {
        Lead lead = leads.findById(id).orElseThrow();
        return Map.of(
                "draft", ai.draft(lead),
                "provider", ai.providerName(),
                "sent", "false"
        );
    }

    @PatchMapping("/{id}/opt-out")
    public Lead optOut(@PathVariable Long id) {
        Lead lead = leads.findById(id).orElseThrow();
        lead.optedOut = true;
        return leads.save(lead);
    }

    @PostMapping(value="/import-csv", consumes="text/plain")
    public ResponseEntity<?> importCsv(@RequestBody String csv) {
        int imported=0;
        String[] rows=csv.split("\\r?\\n");
        for(int i=1;i<rows.length;i++) {
            String line=rows[i].trim(); if(line.isEmpty()) continue;
            String[] c=line.split(",",-1);
            if(c.length<5) continue;
            Lead lead=new Lead(); lead.name=c[0].trim(); lead.email=c[1].trim(); lead.phone=c[2].trim();
            lead.vehicleModel=c[3].trim(); lead.message=c[4].trim();
            lead.showroom=c.length>5&&!c[5].isBlank()?c[5].trim():"Lucknow";
            lead.assignedTo=assign(lead.showroom,lead.vehicleModel);
            lead.acknowledgement="SIMULATED_SMS: Enquiry received and assigned to "+lead.assignedTo+".";
            leads.save(lead); imported++;
        }
        return ResponseEntity.ok(Map.of("imported",imported,"note","CSV columns: name,email,phone,vehicleModel,message,showroom. Quote-free CSV rows are supported."));
    }

    private String assign(String showroom, String model) {
        String city=showroom==null?"lucknow":showroom.toLowerCase();
        String car=model==null?"":model.toLowerCase();
        if(car.contains("model c") || car.contains("model s") || car.contains("used")) return "usedcars@autosphere.demo";
        if(city.contains("kanpur")) return "kanpur.sales@autosphere.demo";
        if(city.contains("varanasi")) return "varanasi.sales@autosphere.demo";
        return "sales@autosphere.demo";
    }
}
