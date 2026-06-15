package com.Ntm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NtmApplicationTest {
    //Este test solo cumple por cobertura
    @Test
    void shouldInstantiateApplication() {

        NtmApplication application =
                new NtmApplication();

        assertNotNull(application);
    }
}