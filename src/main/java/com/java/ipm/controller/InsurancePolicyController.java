package com.java.ipm.controller;

import com.java.ipm.entity.InsurancePolicy;
import com.java.ipm.service.InsurancePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
public class InsurancePolicyController {

    @Autowired
    private InsurancePolicyService policyService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<InsurancePolicy> getAllPolicy() {
        return policyService.getAllPolicies();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<InsurancePolicy> getPoliciesById(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(policyService.getPolicyById(id, authentication));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER')") // ✅ Fixed role check
    public ResponseEntity<?> createPolicy(@RequestBody InsurancePolicy insurancePolicy, Authentication authentication) {
        insurancePolicy.setUser(policyService.getAuthenticatedUser(authentication)); // ✅ Ensure user is assigned
        return ResponseEntity.ok(policyService.createPolicy(insurancePolicy, authentication));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> updatePolicy(@PathVariable Long id, @RequestBody InsurancePolicy policy,
                                          Authentication authentication) {
        return ResponseEntity.ok(policyService.updatePolicy(id, policy, authentication));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<?> deletePolicy(@PathVariable Long id, Authentication authentication) {
        policyService.deletePolicy(id, authentication);
        return ResponseEntity.ok("Policy deleted successfully");
    }
}
