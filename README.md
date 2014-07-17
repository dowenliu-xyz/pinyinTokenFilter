pinyinTokenFilter
=================

A TokenFilter plugin for Apache Solr. It add pinyin terms for chinese terms.  
Apache Solr搜索引擎插件，用于将为中文词元增加拼音注解词元。

Usage/用法
-----------------

#Example/示例  

> 		<fieldType name="text_general_rev" class="solr.TextField">
> 			<analyzer type="index">
> 				<tokenizer class="solr.StandardTokenizerFactory" />
> 				<filter class="me.dowen.solr.analyzers.PinyinTransformTokenFilterFactory"
> 					isOutChinese="true" firstChar="true" minTermLength="2"/>
> 				<filter class="me.dowen.solr.analyzers.PinyinTransformTokenFilterFactory"
> 					isOutChinese="true" firstChar="false" minTermLength="2"/>
> 				<filter class="solr.StopFilterFactory" ignoreCase="true"
> 					words="stopwords.txt" />
> 				<filter class="solr.LowerCaseFilterFactory" />
> 				<filter class="solr.RemoveDuplicatesTokenFilterFactory"/>
> 			</analyzer>
> 					<analyzer type="query">
> 				<tokenizer class="solr.StandardTokenizerFactory" />
> 				<filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt"
> 					ignoreCase="true" expand="true" />
> 				<filter class="solr.StopFilterFactory" ignoreCase="true"
> 					words="stopwords.txt" />
> 				<filter class="solr.LowerCaseFilterFactory" />
> 			</analyzer>
> 		</fieldType>

#Filter Class/过滤器类

*me.dowen.solr.analyzers.PinyinTransformTokenFilterFactory*

#Configuration/配置项
##isOutChinese
If original chinese term would keep in output or not.Optional values:*true*(default)/*false*.  
是否保留原输入中文词元。可选值：*true*(默认)/*false*
##firstChar
If output pinyin would be in full format or in short format.The short format is formed by every first character of pinyin of every chinese character.Optional values:*true*(default)/*false*.  
输出完整拼音格式还是输出简拼。简拼输出是由原中文词元的各单字的拼音结果的首字母组成的。可选值：*true*(默认)/*false*
##minTermLength
Only output pinyin term for chinese term which character lenght is greater than or equals *minTermLenght*.The default value is 2.
仅输出字数大于或等于*minTermLenght*的中文词元的拼音结果。默认值为2。
