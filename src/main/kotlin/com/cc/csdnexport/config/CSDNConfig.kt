package com.cc.csdnexport.config

class CSDNConfig {

    /**
     * csdn用户名
     */
    var userName: String = ""

    /**
     * 文章列表的页数
     */
    var pageCount: Int = 1

    /**
     * 是否只抓取原创文章
     */
    var onlyOriginal: Boolean = false


    /**
     * 文章列表，为空表示全部
     */
    var articles: ArrayList<String> = arrayListOf()
}