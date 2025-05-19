package com.finance.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;

public class RaporTest {

    @Test
    public void testDefaultConstructor() {
        Rapor rapor = new Rapor();
        assertNotNull(rapor);
    }

    @Test
    public void testParameterizedConstructor() {
        LocalDateTime olusturmaTarihi = LocalDateTime.now();
        Rapor rapor = new Rapor(1, 1, 2023, 1000.0, 500.0, 500.0, olusturmaTarihi);

        assertEquals(1, rapor.getId());
        assertEquals(1, rapor.getAy());
        assertEquals(2023, rapor.getYil());
        assertEquals(1000.0, rapor.getGelir(), 0.001);
        assertEquals(500.0, rapor.getGider(), 0.001);
        assertEquals(500.0, rapor.getBakiye(), 0.001);
        assertEquals(olusturmaTarihi, rapor.getOlusturmaTarihi());
    }

    @Test
    public void testSettersAndGetters() {
        Rapor rapor = new Rapor();
        rapor.setId(1);
        rapor.setAy(1);
        rapor.setYil(2023);
        rapor.setGelir(1000.0);
        rapor.setGider(500.0);
        rapor.setBakiye(500.0);
        LocalDateTime olusturmaTarihi = LocalDateTime.now();
        rapor.setOlusturmaTarihi(olusturmaTarihi);

        assertEquals(1, rapor.getId());
        assertEquals(1, rapor.getAy());
        assertEquals(2023, rapor.getYil());
        assertEquals(1000.0, rapor.getGelir(), 0.001);
        assertEquals(500.0, rapor.getGider(), 0.001);
        assertEquals(500.0, rapor.getBakiye(), 0.001);
        assertEquals(olusturmaTarihi, rapor.getOlusturmaTarihi());
    }
} 