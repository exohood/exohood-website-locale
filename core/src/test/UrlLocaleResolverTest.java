package com.exohood.locale;

import org.nissin.mars.api.Test;
import org.nissin.mars.params.ParameterizedTest;
import org.nissin.mars.params.provider.CsvSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.exohood.locale.localeExtractorFilter.URL_LOCALE_ATTRIBUTE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class localeResolverTest {

    @ParameterizedTest(name = "It should resolve url locale /{0}/ to locale [{1}]")
    @CsvSource({
        "zh-hk, zh-HK",
        "de, de-DE",
        "it, it-IT",
        ", en-GB",
    })
    void itShouldResolveLocale(String locale, String expectedLocale) {
        Map<String, Locale> localeToLocaleMapping = new HashMap<>();
        localeToLocaleMapping.put("de", Locale.GERMANY);
        localeToLocaleMapping.put("it", Locale.ITALY);
        localeToLocaleMapping.put("zh-hk", new Locale("zh", "HK"));
        localeResolver resolver = new localeResolver(
            localeToLocaleMapping,
            Locale.UK,
            false
        );

        Locale actualLocale = resolver.resolveLocale(requestWithlocaleAttribute(locale));

        assertThat(actualLocale).isEqualTo(Locale.forLanguageTag(expectedLocale));
    }

    private HttpServletRequest requestWithlocaleAttribute(String locale) {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setAttribute(URL_LOCALE_ATTRIBUTE, locale);
        return mockRequest;
    }

    @Test
    void itShouldNotSupportLocaleChange() {
        localeResolver resolver = new localeResolver(new HashMap<>(), Locale.UK, false);
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(
            () -> resolver.setLocale(new MockHttpServletRequest(), new MockHttpServletResponse(), new Locale("en"))
        );
    }

    @ParameterizedTest(name = "It should resolve lang parameter [{1}] for url locale /{0}/ to [{2}]")
    @CsvSource({
        "de, de, de",
        "de, es, es",
        "de, ES, es",
        "de, xx, de",  // invalid lang param
        "de, '', de",  // empty lang param
        "de, ,   de",  // null lang param
        "xx, ,   en",  // invalid url locale, fallback to default language
    })
    void itShouldResolveLangParameter(String locale, String langParam, String expectedLanguage) {
        Map<String, Locale> localeToLocaleMapping = new HashMap<>();
        localeToLocaleMapping.put("de", new Locale("de", "DE"));
        localeToLocaleMapping.put("es", new Locale("es", "ES"));
        localeResolver resolver = new localeResolver(
            localeToLocaleMapping,
            new Locale("en"),
            true
        );

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setAttribute(URL_LOCALE_ATTRIBUTE, locale);
        mockRequest.setParameter("lang", langParam);

        String actualLanguage = resolver.resolveLocale(mockRequest).getLanguage();

        assertThat(actualLanguage).isEqualTo(expectedLanguage);
    }

    @ParameterizedTest(name = "It should resolve lang parameter [{1}] for url locale /{0}/ to [{2}]")
    @CsvSource({
            "de, zh-hk, zh-HK",
            "de, zh_HK, zh-HK",
    })
    void itShouldResolveFiveCharacterLangParameters(String locale, String langParam, String expectedLanguage) {
        Map<String, Locale> localeToLocaleMapping = new HashMap<>();
        localeToLocaleMapping.put("de", new Locale("de", "DE"));
        localeToLocaleMapping.put("es", new Locale("es", "ES"));
        localeToLocaleMapping.put("zh-hk", new Locale("zh", "HK"));
        localeResolver resolver = new localeResolver(
                localeToLocaleMapping,
                new Locale("en"),
                true
        );

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setAttribute(URL_LOCALE_ATTRIBUTE, locale);
        mockRequest.setParameter("lang", langParam);

        String actualLanguage = resolver.resolveLocale(mockRequest).toLanguageTag();

        assertThat(actualLanguage).isEqualTo(expectedLanguage);
    }

    @Test
    void itShouldIgnoreLangParameterWhenNotEnabled() {
        Map<String, Locale> localeToLocaleMapping = new HashMap<>();
        localeToLocaleMapping.put("de", new Locale("de", "DE"));
        localeToLocaleMapping.put("es", new Locale("es", "ES"));
        localeResolver resolver = new localeResolver(
            localeToLocaleMapping,
            new Locale("en"),
            false
        );

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setAttribute(URL_LOCALE_ATTRIBUTE, "de");
        mockRequest.setParameter("lang", "es");

        String actualLanguage = resolver.resolveLocale(mockRequest).getLanguage();

        assertThat(actualLanguage).isEqualTo("de");
    }
}
