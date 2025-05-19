package com.finance.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GiderTest {

    @Test
    public void testDefaultConstructor() {
        Gider gider = new Gider();
        assertNotNull(gider.getOlusturmaTarihi());
        assertEquals(LocalDate.now(), gider.getTarih());
    }

    @Test
    public void testSettersAndGetters() {
        Gider gider = new Gider();
        gider.setId(1);
        gider.setKategoriId("K001");
        gider.setTutar(100.0);
        gider.setAciklama("Test Gider");
        LocalDate tarih = LocalDate.of(2023, 1, 1);
        gider.setTarih(tarih);
        LocalDateTime olusturmaTarihi = LocalDateTime.now();
        gider.setOlusturmaTarihi(olusturmaTarihi);

        assertEquals(1, gider.getId());
        assertEquals("K001", gider.getKategoriId());
        assertEquals(100.0, gider.getTutar(), 0.001);
        assertEquals("Test Gider", gider.getAciklama());
        assertEquals(tarih, gider.getTarih());
        assertEquals(olusturmaTarihi, gider.getOlusturmaTarihi());
    }
} 