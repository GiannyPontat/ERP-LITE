package com.gp_dev.erp_lite.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailRequest {

    @Email(message = "Email invalide")
    @NotBlank(message = "Email destinataire requis")
    private String recipientEmail;

    private String message; // Message personnalisé optionnel
}
