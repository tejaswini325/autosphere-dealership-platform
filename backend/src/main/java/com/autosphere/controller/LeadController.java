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
            String vehicleModel
    ) {}

    @PostMapping
    public ResponseEntity<Lead> create(@Valid @RequestBody Create request) {
        Lead lead = new Lead();
        lead.name = request.name();
        lead.email = request.email();
        lead.phone = request.phone();
        lead.message = request.message();
        lead.vehicleModel = request.vehicleModel();
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

    @PostMapping("/{id}/ai-draft")
    public Map<String, String> draft(@PathVariable Long id) {
        Lead lead = leads.findById(id).orElseThrow();
        return Map.of(
                "draft", ai.draft(lead),
                "provider", ai.providerName(),
                "sent", "false"
        );
    }
}
