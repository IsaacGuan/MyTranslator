package com.gyr.translator.onlinetranslate;

public interface TranslateUtil {
	
	//���з��룬�Զ�ʶ��Դ����
	public String translate(final String text, final String targetLang) throws Exception;
	
	//���з��룬�Զ�ʶ��Դ����
	public String[] translate(final String[] texts, final String targetLang) throws Exception;
	
	//���з��룬Դ����->Ŀ������
	public String translate(final String text, final String srcLang, final String targetLang) throws Exception;
	
	//���з��룬Դ����->Ŀ������
	public String[] translate(final String[] texts, final String srcLang, final String targetLang) throws Exception;
}
