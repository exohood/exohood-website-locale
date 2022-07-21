package com.exohood.locale.hreflang;

import java.util.Map;

public class HreflangConfig {
    private Map<Hreflang, String> hreflangTolocaleMapping;
    private String xDefault;

    public HreflangConfig(Map<Hreflang, String> hreflangTolocaleMap, String xDefault) {
        this.hreflangTolocaleMapping = hreflangTolocaleMap;
        this.xDefault = xDefault;
    }

    public Map<Hreflang, String> getHreflangTolocaleMapping() {
        return hreflangTolocaleMapping;
    }

    public String getxDefault() {
        return this.xDefault;
    }
}
