package me.dowen.solr.analyzers;

import java.io.IOException;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * 拼音转换分词过滤器
 * @author liufl / 2014年7月1日
 */
public class PinyinTransformTokenFilter extends TokenFilter {

	private boolean isOutChinese = true; // 是否输出原中文开关
	private boolean firstChar = false; // 拼音缩写开关，输出编写时不输出全拼音
	private final CharTermAttribute termAtt = (CharTermAttribute) addAttribute(CharTermAttribute.class); // 词元记录
	HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat(); // 拼音转接输出格式
	private int _minTermLenght = 2; // 中文词组长度过滤，默认超过2位长度的中文才转换拼音
	private char[] curTermBuffer; // 底层词元输入缓存
	private int curTermLength; // 底层词元输入长度
	private boolean outChinese = true; // 是否输出当前输入开关

	/**
	 * 构造器。默认长度超过2的中文词元进行转换，转换为全拼音且保留原中文词元
	 * @param input 词元输入
	 */
	public PinyinTransformTokenFilter(TokenStream input) {
		this(input, 2);
	}

	/**
	 * 构造器。默认转换为全拼音且保留原中文词元
	 * @param input 词元输入
	 * @param minTermLenght 中文词组过滤长度
	 */
	public PinyinTransformTokenFilter(TokenStream input, int minTermLenght) {
		this(input, false, minTermLenght);
	}

	/**
	 * 构造器。默认长度超过2的中文词元进行转换，保留原中文词元
	 * @param input 词元输入
	 * @param firstChar 输出拼音缩写还是完整拼音
	 */
	public PinyinTransformTokenFilter(TokenStream input, boolean firstChar) {
		this(input, firstChar, 2);
	}

	/**
	 * 构造器。默认保留原中文词元
	 * @param input 词元输入
	 * @param firstChar 输出拼音缩写还是完整拼音
	 * @param minTermLenght 中文词组过滤长度
	 */
	public PinyinTransformTokenFilter(TokenStream input, boolean firstChar,
			int minTermLenght) {
		this(input, firstChar, minTermLenght, true);
	}

	/**
	 * 构造器
	 * @param input 词元输入
	 * @param firstChar 输出拼音缩写还是完整拼音
	 * @param minTermLenght 中文词组过滤长度
	 * @param isOutChinese 是否输入原中文词元
	 */
	public PinyinTransformTokenFilter(TokenStream input, boolean firstChar,
			int minTermLenght, boolean isOutChinese) {
		super(input);
		this._minTermLenght = minTermLenght;
		if (this._minTermLenght < 1) {
			this._minTermLenght = 1;
		}
		this.isOutChinese = isOutChinese;
		this.outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		this.outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		this.firstChar = firstChar;
	}

	/**
	 * 判断字符串中是否含有中文
	 * XXX 已知问题：含有少于_minTermLenght个中文的混合字符串长度超过_minTermLenght，会进行拼音转换
	 * @param s
	 * @return
	 */
	public static boolean containsChinese(String s) {
		if ((null == s) || ("".equals(s.trim())))
			return false;
		for (int i = 0; i < s.length(); i++) {
			if (isChinese(s.charAt(i)))
				return true;
		}
		return false;
	}

	/**
	 * 判断字符是否是中文
	 * @param a
	 * @return
	 */
	public static boolean isChinese(char a) {
		int v = a;
		return (v >= 19968) && (v <= 171941);
	}

	/**
	 * 分词过滤。<br/>
	 * 该方法在上层调用中被循环调用，直到该方法返回false
	 */
	public final boolean incrementToken() throws IOException {
		while (true) {
			if (this.curTermBuffer == null) { // 开始处理或上一输入词元已被处理完成
				if (!this.input.incrementToken()) { // 获取下一词元输入
					return false; // 没有后继词元输入，处理完成，返回false，结束上层调用
				}
				// 缓存词元输入
				this.curTermBuffer = ((char[]) this.termAtt.buffer().clone());
				this.curTermLength = this.termAtt.length();
			}
			// 处理原输入词元
			if ((this.isOutChinese) && (this.outChinese)) { // 准许输出原中文词元且当前没有输出原输入词元
				this.outChinese = false; // 标记以保证下次循环不会输出
				// 写入原输入词元
				this.termAtt.copyBuffer(this.curTermBuffer, 0,
						this.curTermLength);
				return true; // 继续
			}
			this.outChinese = true; // 下次取词元后输出原词元（如果开关也准许）
			String chinese = this.termAtt.toString();
			// 拼音处理
			if (containsChinese(chinese)) {
				//有中文
				this.outChinese = true;
				if (chinese.length() >= this._minTermLenght) { // 符合长度限制
					try {
						// 输出拼音（缩写或全拼）
						String chineseTerm = this.firstChar ? getPyShort(chinese)
								: GetPyString(chinese);
						this.termAtt.copyBuffer(chineseTerm.toCharArray(), 0,
								chineseTerm.length());
					} catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
						badHanyuPinyinOutputFormatCombination.printStackTrace();
					}
					// 清理缓存，下次取新词元
					this.curTermBuffer = null;
					return true; // 继续
				}
			}
			// 没有中文，不用处理，
			// 清理缓存，下次取新词元
			this.curTermBuffer = null;
		}
	}

	/**
	 * 获取拼音缩写
	 * @param chinese 含中文的字符串，若不含中文，原样输出
	 * @return 转换后的文本
	 * @throws BadHanyuPinyinOutputFormatCombination
	 */
	private String getPyShort(String chinese)
			throws BadHanyuPinyinOutputFormatCombination {
		String chineseTerm;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < chinese.length(); i++) {
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(
					chinese.charAt(i), this.outputFormat);
			if (pinyinArray != null && pinyinArray.length > 0) {
				for (String pinyin : pinyinArray) {
					if (pinyin != null && !"".equals(pinyin.trim())) {
						sb.append(pinyin.toLowerCase().charAt(0));
						break;
					}
				}
			}
		}
		chineseTerm = sb.toString();
		return chineseTerm;
	}

	public void reset() throws IOException {
		super.reset();
	}

	/**
	 * 获取拼音
	 * @param chinese 含中文的字符串，若不含中文，原样输出
	 * @return 转换后的文本
	 * @throws BadHanyuPinyinOutputFormatCombination
	 */
	private String GetPyString(String chinese)
			throws BadHanyuPinyinOutputFormatCombination {
		String chineseTerm;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < chinese.length(); i++) {
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(
					chinese.charAt(i), this.outputFormat);
			if (pinyinArray != null && pinyinArray.length > 0) {
				for (String pinyin : pinyinArray) {
					if (pinyin != null && !"".equals(pinyin.trim())) {
						sb.append(pinyin.toLowerCase().replaceAll("u:", "v"));
						break;
					}
				}
			}
		}
		chineseTerm = sb.toString();
		return chineseTerm;
	}
}