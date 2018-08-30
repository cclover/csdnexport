package com.cc.csdnexport.csdn

import com.cc.csdnexport.config.ExportConfigManager
import us.codecraft.webmagic.Spider

class CSDNExporter {

    fun main() {

//        // 访问第一页
//        for (i in 1..Constant.PAGE_COUNT) {
//            var url = Constant.ARTICLE_LIST_REQUEST_URL.format(i)
//            Spider.create(CSDNPageProcessor()).addUrl(url).thread(3).run()
//        }

        var testUrl = ExportConfigManager.getArticleUrl("10419645")
        Spider.create(CSDNPageProcessor()).addUrl(testUrl).thread(3).run()
    }

}