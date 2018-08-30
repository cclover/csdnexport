package com.cc.csdnexport.wordpress

import com.cc.csdnexport.config.Constant
import com.cc.csdnexport.config.ExportConfigManager
import com.cc.csdnexport.csdn.CSDNCommentInfo
import java.math.BigInteger
import java.sql.Timestamp
import java.time.LocalDateTime

class TableComments {

    /**
     * 评论ID，自增
     */
    var comment_ID: BigInteger = BigInteger.ZERO

    /**
     * post文章的ID
     */
    var comment_post_ID: BigInteger = BigInteger.ZERO


    /**
     * 评论的人的名字
     */
    var comment_author: String? = ""


    /**
     * 评论人的网站
     */
    var comment_author_url: String? = ""


    /**
     * 评论时间
     */
    var comment_date: Timestamp? = null
    var comment_date_gmt: Timestamp? = null

    /**
     * 评论内容
     */
    var comment_content: String? = ""


    /**
     * 评论人的IP
     */
    var comment_author_IP: String? = "0.0.0.0"


    /**
     * 评论人的email
     */
    var comment_author_email: String? = ""


    /**
     * 嵌套回复的评论的ID
     */
    var comment_parent: BigInteger = BigInteger.ZERO

    /**
     * 自己恢复是自己的id，其他非注册用户为0
     */
    var user_id: BigInteger = BigInteger.ZERO


    var comment_karma: Int = 0
    var comment_approved: String? = "1"
    var comment_agent: String? = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0"
    var comment_type: String? = ""


    constructor(postID: Long, csdnCommentInfo: CSDNCommentInfo) {

        comment_post_ID = postID.toBigInteger()
        comment_author = csdnCommentInfo.name
        comment_author_url = csdnCommentInfo.link
        comment_content = csdnCommentInfo.comment
        comment_author_IP = csdnCommentInfo.ip
        user_id = if (csdnCommentInfo.isSelf) ExportConfigManager.getWPUserID().toBigInteger() else Constant.NONE_WP_USER.toBigInteger()

        // 获取时间
        var stamp = Timestamp.valueOf(LocalDateTime.now())
        try {
            stamp = Timestamp.valueOf(csdnCommentInfo.time)
        } catch (ex: Exception) {

        } finally {
            comment_date = stamp
            comment_date_gmt = stamp
        }

    }
}
