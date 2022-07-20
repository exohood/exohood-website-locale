package com.exohood.urllocale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.servlet.http.HttpServletResponse.*;

public class UrlLocaleExtractorFilter implements Filter {

    @Deprecated // Prefer URL_LOCALE_ATTRIBUTE. Not removing as likely to be used in templates that may not be caught in dev.
    static final String LEGACY_LOCALE_ATTRIBUTE = "locale";
    public static final String URL_LOCALE_ATTRIBUTE = "urlLocale";
    private static final Pattern PATH_PATTERN = Pattern.compile("^/([a-z]{2}(-[a-z]{2})?)/.*$");

    private Set<String> supportedUrlLocales;

    public UrlLocaleExtractorFilter(Set<String> supportedUrlLocales) {
        this.supportedUrlLocales = supportedUrlLocales;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        Matcher matcher = PATH_PATTERN.matcher(req.getServletPath());
        if (matcher.matches()) {
            String urlLocale = matcher.group(1);
            if (!supportedUrlLocales.contains(urlLocale)) {
                ((HttpServletResponse) response).sendError(SC_NOT_FOUND);
                return;
            }

            request.setAttribute(URL_LOCALE_ATTRIBUTE, urlLocale);
            request.setAttribute(LEGACY_LOCALE_ATTRIBUTE, urlLocale);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
