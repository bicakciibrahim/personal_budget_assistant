package com.finance.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDateTime;

public class TasarrufHedefiTest {

    @Test
    public void testDefaultConstructor() {
        TasarrufHedefi hedef = new TasarrufHedefi();
        assertNotNull(hedef.getOlusturmaTarihi());
    }

    @Test
    public void testSettersAndGetters() {
        TasarrufHedefi hedef = new TasarrufHedefi();
        hedef.setId(1);
        hedef.setAd("Test Hedef");
        hedef.setHedefTutar(1000.0);
        hedef.setBirikimTutar(500.0);
        LocalDateTime olusturmaTarihi = LocalDateTime.now();
        hedef.setOlusturmaTarihi(olusturmaTarihi);

        assertEquals(1, hedef.getId());
        assertEquals("Test Hedef", hedef.getAd());
        assertEquals(1000.0, hedef.getHedefTutar(), 0.001);
        assertEquals(500.0, hedef.getBirikimTutar(), 0.001);
        assertEquals(olusturmaTarihi, hedef.getOlusturmaTarihi());
    }
} 