package com.github.hmdev.util;
/**
 * 文字変換と判別関連の関数定義クラス
 * Licence: Non-commercial use only.
 */
public class CharUtils
{
	/** 全角英数字を半角に変換
	 * @param src 全角文字列
	 * @return 半角文字列 */
	static public String fullToHalf(String src)
	{
		char[] c = src.toCharArray();
		for (int i=c.length-1; i>=0; i--) {
			if (c[i] >= '０' && c[i] <= '９') c[i] = (char)(c[i]-'０'+'0');
			else if (c[i] >= 'Ａ' && c[i] <= 'ｚ') c[i] = (char)(c[i]-'ａ'+'a');
		}
		return new String(c);
	}
	
	/** すべて同じ文字かチェック */
	static public boolean isSameChars(char[] ch, int begin, int end)
	{
		for (int i=begin+1; i<end; i++) {
			if (ch[begin] != ch[i]) return false;
		}
		return true;
	}
	
	/** 半角数字かチェック */
	static public boolean isNum(char ch)
	{
		switch (ch) {
			case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9': return true;
		}
		return false;
	}
	
	/** 英字かどうかをチェック 拡張ラテン文字含む
	 * 半角スペースは含まない */
	static public boolean isHalf(char ch)
	{
		return (0x21 <= ch && ch <= 0x02AF);
	}
	/** 英字かどうかをチェック 拡張ラテン文字含む
	 * 半角スペースは含まない */
	static public boolean isHalf(char[] chars)
	{
		for (char ch : chars) {
			if (!isHalf(ch)) return false;
		}
		return true;
	}
	
	/** 英字かどうかをチェック 拡張ラテン文字含む
	 * 半角スペースを含む */
	static public boolean isHalfSpace(char ch)
	{
		return (0x20 <= ch && ch <= 0x02AF);
	}
	/** 英字かどうかをチェック 拡張ラテン文字含む
	 * 半角スペースを含む */
	static public boolean isHalfSpace(char[] chars)
	{
		for (char ch : chars) {
			if (!isHalfSpace(ch)) return false;
		}
		return true;
	}
	
	static public boolean isFullAlpha(char ch)
	{
		return ('Ａ' <= ch && ch <= 'Ｚ') || ('ａ' <= ch && ch <= 'ｚ') || ('０' <= ch && ch <= '９') || '＠' == ch || '＿' == ch;
	}
	/** 半角数字かチェック */
	static public boolean isFullNum(char ch)
	{
		switch (ch) {
			case '０': case '１': case '２': case '３': case '４': case '５': case '６': case '７': case '８': case '９': return true;
		}
		return false;
	}
	
	/** ひらがなかチェック */
	static public boolean isHiragana(char ch)
	{
		return ('ぁ'<=ch && ch<='ん') || 'ゖ'==ch || 'ー'==ch || 'ゝ'==ch || 'ゞ'==ch || 'ヽ'==ch || 'ヾ'==ch || '゛'==ch || 'ﾞ'==ch || '゜'==ch || 'ﾟ'== ch;
	}
	/** カタカナかチェック */
	static public boolean isKatakana(char ch)
	{
		return ('ァ'<=ch && ch<='ヶ') || 'ー'==ch || 'ゝ'==ch || 'ゞ'==ch || 'ヽ'==ch || 'ヾ'==ch || '゛'==ch || 'ﾞ'==ch || '゜'==ch || 'ﾟ'== ch;
	}
	
	static public boolean isSpace(String line)
	{
		char c;
		for (int i=line.length()-1; i>=0; i--) {
			c = line.charAt(i);
			if (c != ' ' && c != '　' && c != ' ') return false;
		}
		return true;
	}
	
	/** 英字かどうかをチェック 拡張ラテン文字含む */
	static public boolean isAlpha(char ch)
	{
		return ('A' <= ch  && ch <= 'Z') || ('a' <= ch && ch <= 'z') || (0x80 <= ch && ch <= 0x02AF);
	}
	
	/** 漢字かどうかをチェック
	 * 4バイト文字のも対応
	 * 漢字の間の「ノカケヵヶ」も漢字扱い */
	static public boolean isKanji(char[] ch, int i)
	{
		switch (ch[i]) {
		case '゛':
			return (i>0 && ch[i-1]=='〻');
		case 'ノ': case 'カ': case 'ケ': case 'ヵ': case 'ヶ':
			//漢字の間にある場合だけ漢字扱い
			if (i==0 || i+1==ch.length) return false;
			return _isKanji(ch, i-1) && _isKanji(ch, i+1);
		}
		return _isKanji(ch, i);
	}
	/** カタカナ以外の漢字チェック */
	static private boolean _isKanji(char[] ch, int i)
	{
		char pre = i==0?(char)-1:ch[i-1];
		char c = ch[i];
		char suf = i+1>=ch.length?(char)-1:ch[i+1];
		switch (c) {
		case '〓': case '〆': case '々': case '〻':
			return true;
		}
		if (0x4E00 <= c && c <= 0x9FFF) return true;//'一' <= ch && ch <= '龠'
		if (0xF900 <= c && c <= 0xFAFF) return true;//CJK互換漢字
		//0x20000-0x2A6DF UTF-32({d840,dc00}-{d869,dedf})
		//0x2A700-0x2B81F UTF-32({d869,df00}-{d86e,dc1f})
		//0x2F800-0x2FA1F UTF-32({d87e,dc00}-{d87e,de1f})
		if (pre >= 0) {
			int code = pre<<16|c&0xFFFF;
			if (0xD840DC00 <= code && code <= 0xD869DEDF) return true;
			if (0xD869DF00 <= code && code <= 0xD86EDC1F) return true;
			if (0xD87EDc00 <= code && code <= 0xD87EDE1F) return true;
		}
		if (suf >= 0) {
			int code = c<<16|suf&0xFFFF;
			if (0xD840DC00 <= code && code <= 0xD869DEDF) return true;
			if (0xD869DF00 <= code && code <= 0xD86EDC1F) return true;
			if (0xD87EDc00 <= code && code <= 0xD87EDE1F) return true;
		}
		return false;
	}
	
	////////////////////////////////////////////////////////////////
	/** ファイル名に使えない文字を'_'に置換 */
	static public String escapeUrlToFile(String str)
	{
		return str.replaceAll("(\\?|\\&)", "/").replaceAll("(:|\\*|\\||\\<|\\>|\"|\\\\)", "_");
	}
	
	////////////////////////////////////////////////////////////////
	/** 前後の空白を除外 */
	static public String removeSpace(String text)
	{
		return text.replaceFirst("^[ |　]+", "").replaceFirst("[ |　]+$", "");
	}
	/** タグを除外 */
	static public String removeTag(String text)
	{
		return text.replaceAll("［＃.+?］", "").replaceAll("<[^>]+>", "");
	}
	
	/** ルビを除去 特殊文字はエスケープされている */
	static public String removeRuby(String text)
	{
		return text.replaceAll("([^※])《.*?[^※]》", "$1").replaceFirst("^｜", "").replaceAll("([^※])｜", "$1");
	}
	
	/** HTML特殊文字をエスケープ */
	static public String escapeHtml(String text)
	{
		return text.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
	
	/** 目次やタイトル用の文字列を取得 ルビ関連の文字 ｜《》 は除外済で他の特殊文字は'※'エスケープ
	 * @param maxLength 文字制限 これより大きい文字は短くして...をつける
	 * @prram 記号文字を短縮する */
	static public String getChapterName(String line, int maxLength, boolean reduce)
	{
		String name = line.replaceAll("［＃.+?］", "").replaceAll("<[^>]+>", "")//注記とタグ除去
				.replaceAll("※(《|》|［|］|〔|〕|〔|〕|〔|〕|｜)", "$1") //エスケープ文字から※除外
				.replaceFirst("^[\t| |　]+", "").replaceFirst("[\t| |　]+$","") //前後の不要な文字所除去
				.replaceAll("〳〵", "く").replaceAll("〴〵", "ぐ").replaceAll("〻", "々");
				//printLineBuffer内だと以下の変換が必要
				/*.replaceAll("<span class=\"fullsp\"> </span>", "　").replaceAll(String.valueOf((char)(0x2000))+(char)(0x2000), "　")
				.replaceAll("<rt>[^<]+</rt>", "")*/
		if (reduce) name = name.replaceAll("(=|＝|-|―|─)+", "$1");//連続する記号は1つに
		if (maxLength == 0) return name;
		return name.length()>maxLength ? name.substring(0, maxLength)+"..." : name;
	}
	static public String getChapterName(String line, int maxLength)
	{
		return getChapterName(line, maxLength, true);
	}
	
	/** BOMが文字列の先頭にある場合は除去 */
	static public String removeBOM(String str)
	{
		if (str != null && str.length() > 0) {
			if (str.charAt(0) == 0xFEFF) {
				return str.substring(1);
			} else {
				return str;
			}
		} else {
			return null;
		}
	}
	
	////////////////////////////////////////////////////////////////
	/** Test用 */
	/*public static void main(String[] args)
	{
		try {
			//漢字チェック
			int utf32Code = 0x2B73F;
			byte[] b = new byte[]{0, (byte)(utf32Code>>16), (byte)(utf32Code>>8), (byte)(utf32Code)};
			String s = "あ"+new String(b, "UTF-32")+"漢あああ";
			System.out.println(Integer.toHexString(s.charAt(0))+","+Integer.toHexString(s.charAt(1)));
			int length = s.length();
			for (int i=0; i<length; i++) {
				System.out.println(isKanji(i==0?(char)-1:s.charAt(i-1), s.charAt(i), i+1<length?s.charAt(i+1):(char)-1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
}
