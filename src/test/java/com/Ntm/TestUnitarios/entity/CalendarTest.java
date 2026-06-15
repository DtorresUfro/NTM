package com.Ntm.TestUnitarios.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalendarTest {
    @Test
    void shouldManageIdCorrectly() {
        Calendar calendar = new Calendar();

        calendar.setId(100L);

        assertEquals(100L, calendar.getId());
    }
}