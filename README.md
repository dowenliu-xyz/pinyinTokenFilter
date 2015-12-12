pinyinTokenFilter
=================

A TokenFilter plugin for Apache Solr. It add pinyin terms for chinese terms.  
Apache Solr搜索引擎插件，用于将为中文词元增加拼音注解词元。

Tested with Solr Version 5.1.0.  
在Solr 5.1.0版本中测试通过。

Usage/用法
-----------------

#Example/示例  

    <fieldType name="text_general_rev" class="solr.TextField">
        <analyzer type="index">
            <tokenizer class="solr.StandardTokenizerFactory" />
            <filter class="xyz.dowenwork.lucene.analyzer.PinyinTransformTokenFilterFactory"
                isOutChinese="true" firstChar="true" minTermLength="1"/>
            <filter class="xyz.dowenwork.lucene.analyzer.PinyinTransformTokenFilterFactory"
                isOutChinese="true" firstChar="false" minTermLength="1"/>
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

#Filter Class/过滤器类

*PinyinTransformTokenFilterFactory*

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

Dependency&Build&Deploy/关于依赖＆构建＆部署
-----------------

This project depends on project [pinyin4j](http://pinyin4j.sourceforge.net/),and it seems not a maven project because there is no maven artifact info given on it's website.*Yuo'd better hold the artifact of pinyin4j in your local or proxy maven repository such as Nexus*.
这个工程依赖[pinyin4j](http://pinyin4j.sourceforge.net/)项目。而后者似乎不是一个maven项目，因为我没有在它的网站上找到任何maven artifact的说明信息。*我觉得你最好把pinyin4j项目放到你本地或类似Nexus一类的代理maven资源库中*

#*Don't forget to copy pinyin4j jar package to lib path of solr when deploying!*
#*部署的时候别忘了把pinyin4j的jar包也拷贝到solr项目的lib路径下！*
