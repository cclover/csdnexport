package com.cc.csdnexport.config

object Constant {


    /**
     * blog列表的请求url， t=1是原创, t=0 全部
     */
    const val URL_ARTICLE_LIST_REQUEST_SCHEMA = "https://blog.csdn.net/%s/article/list/%d?t=%d"


    /**
     * blog文章的请求url
     */
    const val URL_ARTICLE_REQUEST_SCHEMA = "https://blog.csdn.net/%s/article/details/%s"


    /**
     * 文章中评论的请求URL，tree_type 0是flat。 1是tree，1的信息比0的全
     */
    const val URL_COMMENT_LIST_REQUEST_SCHEMA = "https://blog.csdn.net/%s/phoenix/comment/list/%s?page=%d&tree_type=1"


    /**
     * 文章列表URL的正则表达式
     */
    const val REGEX_ARTICLE_LIST_URL = "^https://blog\\.csdn\\.net/%s/article/list/\\d+\\?t=.+$";


    /**
     * 文章地址URL的正则表达式
     */
    const val REGEX_ARTICLE_DETAIL_URL = "^https://blog\\.csdn\\.net/%s/article/details/\\d+$"


    /**
     * 文章列表DIV的 xpath
     */
    const val XPATH_ARTICLE_MAIN = "//*[@id=\"mainBox\"]/main/div[2]"


    /**
     * 文章标题的xpath
     */
    const val XPATH_ARTICLE_TITLE = "//*[@id=\"mainBox\"]/main/div[1]/div/div/div[1]/h1/text()"


    /**
     * 文章发布时间的xpath
     */
    const val XPATH_ARTICLE_TIME = "//*[@id=\"mainBox\"]/main/div[1]/div/div/div[2]/div[1]/span[1]/text()"


    /**
     * 文章类别的xpath
     */
    const val XPATH_ARTICLE_CATEGORY = "//*[@id=\"mainBox\"]/main/div[1]/div/div/div[2]/div[1]/div/a/text()"


    /**
     * 文章内容的xpath
     */
    const val XPATH_ARTICLE_CONTENT = "//*[@id=\"article_content\"]/div[2]"


    /**
     * 文章内容中图片的xpath
     */
    const val XPATH_ARTICLE_IMG = "//*[@id=\"article_content\"]/div[2]//img"


    /**
     * 文章标签的xpath
     */
    const val XPATH_ARTICLE_TAGS = "//*[@id=\"mainBox\"]/main/div[1]/div/div/div[2]/div[1]/span[3]/a/text()"


    const val CONTENT_BEGIN = "<div class=\"htmledit_views\">"
    const val CONTENT_END = "</div>"


    /**
     * 评论的用户的连接
     */
    const val COMMENT_USER_LINK_URL = "https://my.csdn.net/%s"


    /**
     * mysql驱动名字
     */
    const val MYSQL_DRIVER = "com.mysql.jdbc.Driver"
    const val MYSQL_DRIVER_NEW = "com.mysql.cj.jdbc.Driver"


    /**
     * mysql数据库连接URL
     */
    const val MYSQL_URL = "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf8"


    /**
     * 非wp注册用户ID
     */
    const val NONE_WP_USER = 0L


    /**
     * CSDN图片下载的目录
     */
    const val DOWNLOAD_IMAGE_DIR = "csdn"

    /**
     * CSDN图片地址正则表达式
     */
    const val REGEX_CSDN_IMG = "https:\\/\\/img-blog\\.csdn\\.net\\/(?<name>\\d+)"


    const val REGEX_CSDN_IMG_PATH = "https:\\/\\/img-blog\\.csdn\\.net\\/"
}