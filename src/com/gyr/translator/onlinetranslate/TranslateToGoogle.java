package com.gyr.translator.onlinetranslate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class TranslateToGoogle implements TranslateUtil {
	
	private final String ID_RESULTBOX = "result_box";

	public String translate(String text, String targetLang) throws Exception {
		return translate(text, "", targetLang);
	}

	public String[] translate(String[] texts, String targetLang)
			throws Exception {
		return translate(texts, "", targetLang);
	}

	public String translate(String text, String srcLang, String targetLang)
			throws Exception {
		return execute(text, srcLang, targetLang);
	}

	public String[] translate(String[] texts, String srcLang, String targetLang)
			throws Exception {
		
		int size = texts.length;
		String[] temps = new String[size];
		for(int i = 0 ; i < size ; i++) {
			if(texts[i] == null || texts[i].trim().length() <= 0) {
				temps[i] = "";
			}
			else{
				temps[i] = execute(texts[i].toString(),srcLang , targetLang).trim();
			}			
		}
		return temps;
	}
	
	private String execute(final String text, final String srcLang,
			final String targetLang) throws Exception {
		Document document = Jsoup.connect("http://translate.google.cn")
		  .data("sl", srcLang)
		  .data("ie", "UTF-8")
		  .data("oe", "UTF-8")
		  .data("text", text)
		  .data("tl", targetLang)
		  .data("ie", "UTF-8")
		  .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.77 Safari/535.7")
		  .cookie("Cookie", "Cookie	PREF=ID=8daa1f767f10d1fe:U=f5ac701cf7d3f2e0:FF=0:LD=en:CR=2:TM=1277174286:LM=1289370601:S=q7yslRWEZs3uK1H8; NID=39=UO-TWo9HzzjHc-d_wYm7BVR1cH33KpqaN5h5877_i29nERA93FeG1GSuV3ZSvsOx8D-TnHKpB9m0KhZRH8U9uPwoE-arYd0bAyAlILyXZxLO2_TyGQhJpcMiOLVEuCpq; SID=DQAAAHoAAADMlGzeKhnGkbkIJ36tVO0ZPXgmQ6Cth7Oa6geyyE1WJooW8P01uKUHNrsRkjggvFMAWIWB9J5i18z0F6GjC_oV79mSwXEDGuRFGhRnDyJdid3ptjFW0pIyt4_2D6AMIqtOWF71aWdvY7IvAU1AWMNs8fBZHAOgRqtf3aCUkr36ZA; HSID=A6-YJTnhjBdFWukoR")
		  .timeout(20000)
		  .get();
		Element element = document.getElementById(ID_RESULTBOX);
		return element.text();
	}
	
}
