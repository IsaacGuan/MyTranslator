package com.gyr.translator.onlinetranslate;

public interface TranslateUtil {
	
	//单行翻译，自动识别源语言
	public String translate(final String text, final String targetLang) throws Exception;
	
	//多行翻译，自动识别源语言
	public String[] translate(final String[] texts, final String targetLang) throws Exception;
	
	//单行翻译，源语言->目标语言
	public String translate(final String text, final String srcLang, final String targetLang) throws Exception;
	
	//多行翻译，源语言->目标语言
	public String[] translate(final String[] texts, final String srcLang, final String targetLang) throws Exception;
}
