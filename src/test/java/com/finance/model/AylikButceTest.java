package com.finance.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;

public class AylikButceTest {

    @Test
    public void testDefaultConstructor() {
        AylikButce butce = new AylikButce();
        assertNotNull(butce.getOlusturmaTarihi());
    }

    @Test
    public void testSettersAndGetters() {
        AylikButce butce = new AylikButce();
        butce.setId(1);
        butce.setAy(1);
        butce.setYil(2023);
        butce.setTutar(5000.0);
        LocalDateTime olusturmaTarihi = LocalDateTime.now();
        butce.setOlusturmaTarihi(olusturmaTarihi);

        assertEquals(1, butce.getId());
        assertEquals(1, butce.getAy());
        assertEquals(2023, butce.getYil());
        assertEquals(5000.0, butce.getTutar(), 0.001);
        assertEquals(olusturmaTarihi, butce.getOlusturmaTarihi());
    }
} 