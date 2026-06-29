package com.ntm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class NtmApplicationTest {

    @Test
    void contextLoads() {
        // Spring Boot fails this smoke test if the application context cannot start.
    }
}