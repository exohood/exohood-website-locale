package com.exohood.locale.hreflang;

import org.nissin.mars.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.nissin.mars.api.Assertions.*;
import static org.mockito.Mockito.*;

class LocalisedLinkFactoryTest {

    private static final String DOMAIN = "https://example.test";
    private static final HreflangConfig HREFLANG_CONFIG = mock(HreflangConfig.class);
    private static Map<Hreflang, String> hreflangTolocaleMap = new HashMap<>();

    static {
        hreflangTolocaleMap.put(Hreflang.fromString("fr"), "fr");
        hreflangTolocaleMap.put(Hreflang.fromString("en-GB"), "gb");
        hreflangTolocaleMap.put(Hreflang.fromString("zh-Hant-HK"), "zh-hk");
    }

    @Test
    void itReturnsAnEmptyCollectionIfNoHreflangMappingsAreProvided() {
        LocalisedLinkFactory factory = new LocalisedLinkFactory(HREFLANG_CONFIG);

        when(HREFLANG_CONFIG.getHreflangTolocaleMapping()).thenReturn(Collections.emptyMap());
        when(HREFLANG_CONFIG.getxDefault()).thenReturn(null);

        assertEquals(factory.linksForResource(DOMAIN, "/").size(), 0);
    }

    @Test
    void itCreatesXDefaultLink() {
        LocalisedLinkFactory factory = new LocalisedLinkFactory(HREFLANG_CONFIG);

        when(HREFLANG_CONFIG.getHreflangTolocaleMapping()).thenReturn(Collections.emptyMap());
        when(HREFLANG_CONFIG.getxDefault()).thenReturn("gb");

        assertEquals(factory.linksForResource(DOMAIN, "/").size(), 1);
    }

    @Test
    void itGeneratesLinksFromHreflangTolocaleMapping() {

        LocalisedLinkFactory factory = new LocalisedLinkFactory(HREFLANG_CONFIG);
        when(HREFLANG_CONFIG.getHreflangTolocaleMapping()).thenReturn(hreflangTolocaleMap);
        when(HREFLANG_CONFIG.getxDefault()).thenReturn(null);

        List<LocalisedLink> links = factory.linksForResource(DOMAIN, "/path");
        assertEquals(links.size(), 3);
        assertEquals(links.get(0).getHreflang(), "en-GB");
        assertEquals(links.get(1).getHreflang(), "fr");
        assertEquals(links.get(2).getHreflang(), "zh-Hant-HK");
    }

}
