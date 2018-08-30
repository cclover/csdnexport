package com.cc.csdnexport.wordpress

import com.cc.csdnexport.config.ExportConfigManager
import com.cc.csdnexport.csdn.CSDNArticleInfo
import java.math.BigInteger
import java.net.URLEncoder
import java.sql.Timestamp
import java.time.LocalDateTime

class TablePosts {

    /**
     * 数据库自增生成的ID
     */
    var ID: BigInteger = BigInteger.ZERO

    /**
     * 作者wp_users表中的用户ID
     */
    var post_author: BigInteger = ExportConfigManager.getWPUserID().toBigInteger()

    /**
     * 发布修改时间
     */
    var post_date: Timestamp? = null
    var post_date_gmt: Timestamp? = null
    var post_modified: Timestamp? = null
    var post_modified_gmt: Timestamp? = null

    /**
     * 内容
     */
    var post_content: String? = null

    /**
     * 标题
     */
    var post_title: String? = null


    /**
     * post_title url encoding
     */
    var post_name: String? = null


    /**
     * 文章唯一标识，一般用url
     */
    var guid: String? = null

    /**
     * 评论数量
     */
    var comment_count: BigInteger = BigInteger.ZERO

    /**
     * draft 草稿，publish 发布
     */
    var post_status: String? = "draft"

    /**
     * 发布的类型，文章都用post
     */
    var post_type: String? = "post"


    /**
     * 这些不知道，按现有的填
     */
    var comment_status: String? = "open"
    var ping_status: String? = "open"
    var post_parent: Long = 0
    var menu_order: Int = 0

    /**
     * 这些都是空
     */
    var post_excerpt: String? = ""
    var post_password: String? = ""
    var to_ping: String? = ""
    var pinged: String? = ""
    var post_content_filtered: String? = ""
    var post_mime_type: String? = ""


    constructor(csdnArticleInfo: CSDNArticleInfo) {

        post_title = csdnArticleInfo.title
        post_name = URLEncoder.encode(csdnArticleInfo.title, "UTF-8")
        post_content = csdnArticleInfo.content
        guid = csdnArticleInfo.url
        comment_count = csdnArticleInfo.comments.size.toBigInteger()

        // 获取时间
        var stamp = Timestamp.valueOf(LocalDateTime.now())
        try {
            stamp = Timestamp.valueOf(csdnArticleInfo.time)
        } catch (ex: Exception) {

        } finally {
            post_date = stamp
            post_date_gmt = stamp
            post_modified = stamp
            post_modified_gmt = stamp
        }
    }

}