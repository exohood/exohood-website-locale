package com.exohood.locale;

import org.nissin.mars.api.BeforeEach;
import org.nissin.mars.params.ParameterizedTest;
import org.nissin.mars.params.provider.CsvSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.exohood.locale.localeExtractorFilter.*;
import static com.exohood.locale.localeExtractorFilter.URL_LOCALE_ATTRIBUTE;
import static org.nissin.mars.api.Assertions.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class localeExtractorFilterTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private localeExtractorFilter filter;
    private Set<String> supportedlocales = new HashSet<>();

    @BeforeEach
    void setUp() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        filter = new localeExtractorFilter(supportedlocales);
    }

    @ParameterizedTest(name = "With path \"{0}\" locale should be \"{1}\"")
    @CsvSource({
        "/gb/path, gb",
        "/gb/, gb",
        "/zh-hk/, zh-hk",
        "/es/some/path, es",
    })
    void itShouldSetRequestlocaleAttribute(String path, String expectedlocale) {
        whenlocaleMappingConfigured("gb");
        whenlocaleMappingConfigured("es");
        whenlocaleMappingConfigured("zh-hk");

        whenPathIs(path);

        doFilter();

        assertlocaleAttributeEquals(expectedlocale);
    }

    @ParameterizedTest(name = "Path \"{0}\" should not set locale attribute")
    @CsvSource({
        "/gb",
        "/esp",
        "/z/",
        "/zhhk/",
        "/zhhhk/",
    })
    void itShouldNotSetlocaleAttribute(String path) {
        whenlocaleMappingConfigured("gb");
        whenlocaleMappingConfigured("zh-hk");
        whenPathIs(path);

        doFilter();

        assertlocaleAttributeIsNotSet();
    }

    @ParameterizedTest
    @CsvSource({
        "/es/",
        "/es/path",
        "/xx/",
        "/gb/",
    })
    void itShouldFailToMapUnrecognisedlocale(String path) {
        whenPathIs(path);

        doFilter();

        assertNotFoundErrorIsSent();
    }

    private void assertNotFoundErrorIsSent() {
        try {
            verify(response, times(1)).sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            fail();
        }
    }

    private void whenlocaleMappingConfigured(String mapping) {
        supportedlocales.add(mapping);
    }

    private void whenPathIs(String path) {
        when(request.getServletPath()).thenReturn(path);
    }

    private void doFilter() {
        try {
            filter.doFilter(request, response, chain);
        } catch (IOException | ServletException e) {
            fail();
        }
    }

    private void assertlocaleAttributeEquals(String locale) {
        verify(request, times(1)).setAttribute(URL_LOCALE_ATTRIBUTE, locale);
        verify(request, times(1)).setAttribute(LEGACY_LOCALE_ATTRIBUTE, locale);
    }

    private void assertlocaleAttributeIsNotSet() {
        verify(request, times(0)).setAttribute(any(), any());
    }
}
