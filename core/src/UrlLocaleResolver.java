package com.exohood.locale;

import static com.exohood.locale.localeExtractorFilter.URL_LOCALE_ATTRIBUTE;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;

public class localeResolver implements LocaleResolver {

    private final Locale fallback;
    private final Map<String, Locale> localeToLocaleMapping;
    private final boolean langParameterEnabled;
    private final Set<String> supportedLanguages;

    private static final Map<String, String> fiveCharacterLanguages;

    static {
        fiveCharacterLanguages  = new HashMap<>();
        fiveCharacterLanguages.put("hk", "zh_hk");
    }

    public localeResolver(Map<String, Locale> localeToLocaleMapping, Locale fallback, boolean langParameterEnabled) {
        this.fallback = fallback;
        this.localeToLocaleMapping = localeToLocaleMapping;
        this.supportedLanguages = localeToLocaleMapping.values()
            .stream()
            .map(locale -> getLanguages(locale))
            .collect(Collectors.toSet());
        this.langParameterEnabled = langParameterEnabled;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String localeStr = (String) request.getAttribute(URL_LOCALE_ATTRIBUTE);
        Locale locale = localeToLocaleMapping.getOrDefault(localeStr, fallback);

        String lang = resolveLangParameter(request);
        if (lang != null) {
            locale = getLocaleFromLangParam(lang, locale);
        }

        return locale;
    }

    private Locale getLocaleFromLangParam(String lang, Locale locale) {
        if (fiveCharacterLanguages.containsValue(lang.toLowerCase())) {
            return new Locale(lang.substring(0, 2), lang.substring(3));
        } else {
            return new Locale(lang, locale.getCountry());
        }
    }
    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        throw new UnsupportedOperationException();
    }

    private String resolveLangParameter(HttpServletRequest request) {
        if (!langParameterEnabled) {
            return null;
        }

        String lang = request.getParameter("lang");

        if (lang == null) {
            return null;
        }

        lang = lang.replace("-", "_");

        if (!supportedLanguages.contains(lang.toLowerCase())) {
            return null;
        }

        return lang;
    }

    private String getLanguages(Locale locale) {
        String localeCountry = locale.getCountry().toLowerCase();
        if (fiveCharacterLanguages.containsKey(localeCountry)) {
            return fiveCharacterLanguages.get(localeCountry).toLowerCase();
        }
        return locale.getLanguage();
    }
}
