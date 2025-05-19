package com.finance.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GelirTest {

    @Test
    public void testDefaultConstructor() {
        Gelir gelir = new Gelir();
        assertNotNull(gelir.getOlusturmaTarihi());
        assertEquals(LocalDate.now(), gelir.getTarih());
    }

    @Test
    public void testSettersAndGetters() {
        Gelir gelir = new Gelir();
        gelir.setId(1);
        gelir.setKategoriId("K001");
        gelir.setTutar(100.0);
        gelir.setAciklama("Test Gelir");
        LocalDate tarih = LocalDate.of(2023, 1, 1);
        gelir.setTarih(tarih);
        LocalDateTime olusturmaTarihi = LocalDateTime.now();
        gelir.setOlusturmaTarihi(olusturmaTarihi);

        assertEquals(1, gelir.getId());
        assertEquals("K001", gelir.getKategoriId());
        assertEquals(100.0, gelir.getTutar(), 0.001);
        assertEquals("Test Gelir", gelir.getAciklama());
        assertEquals(tarih, gelir.getTarih());
        assertEquals(olusturmaTarihi, gelir.getOlusturmaTarihi());
    }
} 