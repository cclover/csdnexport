package com.cc.csdnexport.csdn

import com.cc.csdnexport.config.Constant
import com.cc.csdnexport.config.ExportConfigManager
import us.codecraft.webmagic.Spider

class CSDNExporter {

    fun main() {

        // 访问第一页
//        for (i in 1..ExportConfigManager.getBlogListCount()) {
//            var url = ExportConfigManager.getBlogListUrl(i)
//            Spider.create(CSDNPageProcessor()).addUrl(url).thread(3).run()
//        }

//        // 单页测试
        var testUrl = ExportConfigManager.getArticleUrl("10419645")
        Spider.create(CSDNPageProcessor()).addUrl(testUrl).thread(3).run()
    }

}