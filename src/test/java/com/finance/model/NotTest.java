package com.finance.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;

public class NotTest {

    @Test
    public void testDefaultConstructor() {
        Not not = new Not();
        assertNotNull(not.getOlusturmaTarihi());
    }

    @Test
    public void testSettersAndGetters() {
        Not not = new Not();
        not.setId(1);
        not.setBaslik("Test Not");
        not.setIcerik("Test İçerik");
        LocalDateTime olusturmaTarihi = LocalDateTime.now();
        not.setOlusturmaTarihi(olusturmaTarihi);

        assertEquals(1, not.getId());
        assertEquals("Test Not", not.getBaslik());
        assertEquals("Test İçerik", not.getIcerik());
        assertEquals(olusturmaTarihi, not.getOlusturmaTarihi());
    }
} 