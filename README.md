pinyinTokenFilter
=================

A TokenFilter plugin for Apache Solr. It add pinyin terms for chinese terms.
Apache Solr搜索引擎插件，用于将为中文词元增加拼音注解词元。

Usage/用法
=================
示例 
    <fieldType name="text_general_rev" class="solr.TextField">
			<analyzer type="index">
				<tokenizer class="solr.StandardTokenizerFactory" />
				<filter class="me.dowen.solr.analyzers.PinyinTransformTokenFilterFactory"
					isOutChinese="true" firstChar="true" minTermLenght="2"/>
				<filter class="me.dowen.solr.analyzers.PinyinTransformTokenFilterFactory"
					isOutChinese="true" firstChar="false" minTermLenght="2"/>
				<filter class="solr.StopFilterFactory" ignoreCase="true"
					words="stopwords.txt" />
				<filter class="solr.LowerCaseFilterFactory" />
				<filter class="solr.RemoveDuplicatesTokenFilterFactory"/>
			</analyzer>
			<analyzer type="query">
				<tokenizer class="solr.StandardTokenizerFactory" />
				<filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt"
					ignoreCase="true" expand="true" />
				<filter class="solr.StopFilterFactory" ignoreCase="true"
					words="stopwords.txt" />
				<filter class="solr.LowerCaseFilterFactory" />
			</analyzer>
		</fieldType>
