package me.dowen.solr.analyzers;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(com.carrotsearch.randomizedtesting.RandomizedRunner.class)
public class TestPinyinTransformTokenFilter {

	private PinyinTransformTokenFilter filter;

	@Before
	public void before() {
		MockTokenizer tokenizer = new MockTokenizer(new StringReader("和平 重量 and 中国"));
		this.filter = new PinyinTransformTokenFilter(tokenizer);
	}

	@Test
	public void test() throws IOException {
		this.filter.reset();
		while (this.filter.incrementToken()) {
			String token = this.filter.getAttribute(CharTermAttribute.class).toString();
			System.out.println(token);
		}
	}

}
