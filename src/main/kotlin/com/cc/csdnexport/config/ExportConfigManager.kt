package com.cc.csdnexport.config

import com.alibaba.fastjson.JSON
import us.codecraft.webmagic.Site
import java.io.File
import java.io.FileReader


object ExportConfigManager {


    /**
     * 爬取数据时的请求信息
     */
    val REQUEST_CONFIG = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000).addHeader("User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")


    /**
     * 配置信息
     */
    var exportConfig: ExportConfig? = null

    /**
     * 解析config.json文件
     */
    fun parserConfig(): Boolean {

        // 获取配置文件
        var configFile = File(System.getProperty("user.dir"), "config.json")
        if (!configFile.exists()) {

            println("Can't find the config file : ${configFile.absoluteFile}")
            return false
        }

        try {
            val fileReader = FileReader(configFile)
            exportConfig = JSON.parseObject(fileReader.readText(), ExportConfig::class.java)
            return checkConfig()

        } catch (ex: Exception) {

            println("Invalid Config Format!")
            ex.printStackTrace()
        }
        return false
    }

    /**
     * 检查配置问下信息
     */
    private fun checkConfig(): Boolean {

        if (exportConfig == null || exportConfig!!.csdn == null || exportConfig!!.wordpress == null) {
            return false
        }

        // 用户名不为空, 页面大于1
        if (exportConfig!!.csdn!!.userName.isEmpty() || exportConfig!!.csdn!!.pageCount < 1) {

            println("Invalid CSDN Config")
            return false
        }

        // db信息
        if (exportConfig!!.wordpress!!.DBIp.isEmpty() ||
                exportConfig!!.wordpress!!.DBPort <= 0 ||
                exportConfig!!.wordpress!!.DBName.isEmpty() ||
                exportConfig!!.wordpress!!.DBUser.isEmpty() ||
                exportConfig!!.wordpress!!.DBPassword.isEmpty()) {

            println("Invalid DB Config")
            return false
        }

        return true
    }


    fun getBlogListCount(): Int {

        return exportConfig!!.csdn!!.pageCount
    }


    /**
     * 获取指定页的文字列表URL
     */
    fun getBlogListUrl(index: Int): String {

        return Constant.URL_ARTICLE_LIST_REQUEST_SCHEMA.format(exportConfig!!.csdn!!.userName, index, if (exportConfig!!.csdn!!.onlyOriginal) 1 else 0)
    }


    /**
     * 获取指定文章的URL
     */
    fun getArticleUrl(articleID: String): String {

        return Constant.URL_ARTICLE_REQUEST_SCHEMA.format(exportConfig!!.csdn!!.userName, articleID)
    }


    /**
     * 获取第index页的评论URL
     */
    fun getCommentListUrl(articleID: String, index: Int): String {

        return Constant.URL_COMMENT_LIST_REQUEST_SCHEMA.format(exportConfig!!.csdn!!.userName, articleID, index)
    }


    /**
     * 获取文章列表的正则表达式
     */
    fun getArticleListUrlRegex(): String {

        return Constant.REGEX_ARTICLE_LIST_URL.format(exportConfig!!.csdn!!.userName)
    }


    /**
     * 获取文章地址的正则表达式
     */
    fun getArticleUrlRegex(): String {

        return Constant.REGEX_ARTICLE_DETAIL_URL.format(exportConfig!!.csdn!!.userName)
    }


    /**
     * 获取CSDN用户名
     */
    fun getCSDNUserName(): String {

        return exportConfig!!.csdn!!.userName
    }


    /**
     * 获取CSDN用户名
     */
    fun getWPUserID(): Long {

        return exportConfig!!.wordpress!!.UserID
    }


    /**
     * 获取wordpress数据库连接信息
     */
    fun getDBInfo(): Triple<String, String, String> {

        return Triple(Constant.MYSQL_URL.format(
                exportConfig!!.wordpress!!.DBIp,
                exportConfig!!.wordpress!!.DBPort,
                exportConfig!!.wordpress!!.DBName),
                exportConfig!!.wordpress!!.DBUser,
                exportConfig!!.wordpress!!.DBPassword)
    }


    /**
     * 获取wordpress图片的路径
     */
    fun getImagePath(): String {

        return exportConfig!!.wordpress!!.ImgUrl
    }
}