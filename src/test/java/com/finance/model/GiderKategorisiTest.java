package com.finance.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;

public class GiderKategorisiTest {

    @Test
    public void testDefaultConstructor() {
        GiderKategorisi kategori = new GiderKategorisi();
        assertNotNull(kategori.getOlusturmaTarihi());
    }

    @Test
    public void testSettersAndGetters() {
        GiderKategorisi kategori = new GiderKategorisi();
        kategori.setId("K001");
        kategori.setAd("Test Kategori");
        LocalDateTime olusturmaTarihi = LocalDateTime.now();
        kategori.setOlusturmaTarihi(olusturmaTarihi);

        assertEquals("K001", kategori.getId());
        assertEquals("Test Kategori", kategori.getAd());
        assertEquals(olusturmaTarihi, kategori.getOlusturmaTarihi());
    }
} 