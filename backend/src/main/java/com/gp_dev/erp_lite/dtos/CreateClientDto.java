package com.gp_dev.erp_lite.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateClientDto {
    
    @NotBlank(message = "Company name or contact name is required")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    private String companyName;

    @Size(min = 14, max = 14, message = "SIRET must be exactly 14 digits")
    @Pattern(regexp = "^[0-9]{14}$", message = "SIRET must contain only digits")
    private String siret;

    @Size(max = 255, message = "Contact first name must not exceed 255 characters")
    private String contactFirstName;

    @Size(max = 255, message = "Contact last name must not exceed 255 characters")
    private String contactLastName;

    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,9}$", 
             message = "Phone must be a valid phone number")
    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String phone;

    @Size(max = 1000, message = "Address must not exceed 1000 characters")
    private String address;

    @Size(max = 255, message = "City must not exceed 255 characters")
    private String city;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    private Integer paymentTerms; // en jours

    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;

    // Champs de compatibilité pour l'ancien système
    @Size(max = 255, message = "Nom must not exceed 255 characters")
    private String nom;

    @Size(max = 255, message = "Entreprise must not exceed 255 characters")
    private String entreprise;

    @Size(max = 50, message = "Telephone must not exceed 50 characters")
    private String telephone;

    @Size(max = 1000, message = "Adresse must not exceed 1000 characters")
    private String adresse;
}

