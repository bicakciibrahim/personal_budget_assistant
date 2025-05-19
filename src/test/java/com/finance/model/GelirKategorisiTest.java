package com.finance.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;

public class GelirKategorisiTest {

    @Test
    public void testDefaultConstructor() {
        GelirKategorisi kategori = new GelirKategorisi();
        assertNotNull(kategori.getOlusturmaTarihi());
    }

    @Test
    public void testSettersAndGetters() {
        GelirKategorisi kategori = new GelirKategorisi();
        kategori.setId("K001");
        kategori.setAd("Test Kategori");
        LocalDateTime olusturmaTarihi = LocalDateTime.now();
        kategori.setOlusturmaTarihi(olusturmaTarihi);

        assertEquals("K001", kategori.getId());
        assertEquals("Test Kategori", kategori.getAd());
        assertEquals(olusturmaTarihi, kategori.getOlusturmaTarihi());
    }
} 