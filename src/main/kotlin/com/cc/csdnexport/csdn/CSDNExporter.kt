package com.cc.csdnexport.csdn

import com.cc.csdnexport.config.Constant
import com.cc.csdnexport.config.ExportConfigManager
import com.cc.csdnexport.tool.LogUtils
import us.codecraft.webmagic.Spider

class CSDNExporter {

    fun main() {

        val articles = ExportConfigManager.exportConfig!!.csdn!!.articles
        if (articles.isEmpty()) {

            LogUtils.d("The article list is null, will export all articles!")
            exportAll()
        } else {

            LogUtils.d("Export the article list: $articles")
            exportArticle(articles)
        }


    }


    /**
     * 从博客列表页访问每一篇文章
     */
    private fun exportAll() {

        for (i in 1..ExportConfigManager.getBlogListCount()) {
            var url = ExportConfigManager.getBlogListUrl(i)
            Spider.create(CSDNPageProcessor()).addUrl(url).thread(3).run()
        }
    }


    /**
     * 根据文章ID号直接访问
     */
    private fun exportArticle(articles: ArrayList<String>) {

        for (articleID in articles) {
            val articleUrl = ExportConfigManager.getArticleUrl(articleID)
            Spider.create(CSDNPageProcessor()).addUrl(articleUrl).thread(1).run()
        }

    }

}