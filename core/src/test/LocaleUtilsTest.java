package com.exohood.locale;

import org.nissin.mars.api.Test;

import java.util.Locale;

import static com.exohood.locale.LocaleUtils.language;
import static com.exohood.locale.LocaleUtils.openGraphLocale;
import static org.nissin.mars.api.Assertions.assertEquals;

public class LocaleUtilsTest {
	private static final Locale locale = new Locale("id", "ID");

	@Test
	void itShouldReturnISO639ValueForIndonesian() {
		String language = language(locale);
		assertEquals("id", language);
	}

	@Test
	void itShouldReturnLanguageWithUnderscoreDelimiter() {
		String openGraphLocale = openGraphLocale(locale);
		assertEquals("id_ID", openGraphLocale);
	}
}
