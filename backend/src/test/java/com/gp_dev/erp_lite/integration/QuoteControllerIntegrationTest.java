package com.gp_dev.erp_lite.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gp_dev.erp_lite.dtos.QuoteDto;
import com.gp_dev.erp_lite.models.QuoteStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests d'intégration pour QuoteController
 * Nécessite une base de données de test
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class QuoteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
        // Test que le contexte Spring se charge correctement
        assertNotNull(mockMvc);
    }

    // Note: Les tests d'intégration complets nécessitent une configuration de base de données de test
    // et l'authentification. Pour l'instant, ce test vérifie juste que le contexte se charge.
}

