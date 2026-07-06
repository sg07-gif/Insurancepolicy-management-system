package com.java.ipm.entity;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "insurance_policy")
public class InsurancePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String policyName;
    private String policyType;
    private Float premiumAmount;

    @JsonBackReference // Prevent infinite recursion in JSON serialization
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public InsurancePolicy() {}

    public InsurancePolicy(Long id, String policyName, String policyType, Float premiumAmount, User user) {
        this.id = id;
        this.policyName = policyName;
        this.policyType = policyType;
        this.premiumAmount = premiumAmount;
        this.user = user;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getPolicyName() { return policyName; }

    public void setPolicyName(String policyName) { this.policyName = policyName; }

    public String getPolicyType() { return policyType; }

    public void setPolicyType(String policyType) { this.policyType = policyType; }

    public Float getPremiumAmount() { return premiumAmount; }

    public void setPremiumAmount(Float premiumAmount) { this.premiumAmount = premiumAmount; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    @Override
    public int hashCode() {
        return Objects.hash(id, policyName, policyType, premiumAmount, user);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        InsurancePolicy other = (InsurancePolicy) obj;
        return Objects.equals(id, other.id) && Objects.equals(policyName, other.policyName)
                && Objects.equals(policyType, other.policyType) && Objects.equals(premiumAmount, other.premiumAmount)
                && Objects.equals(user, other.user);
    }

    @Override
    public String toString() {
        return "InsurancePolicy [id=" + id + ", policyName=" + policyName + ", policyType=" + policyType
                + ", premiumAmount=" + premiumAmount + ", user=" + user + "]";
    }
}
