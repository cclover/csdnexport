package com.cc.csdnexport.csdn


/**
 * CSDN文章的信息
 */
class CSDNArticleInfo {

    var articleId: String? = null
    var url: String? = null
    var title: String? = null
    var time: String? = null
    var category: String? = null
    var content: String? = null
    var tags: ArrayList<String> = arrayListOf()
    var comments: ArrayList<CSDNCommentInfo> = arrayListOf()


    override fun toString(): String {

        return "ID: $articleId\r\n" +
                "Title: $title\r\n" +
                "Time: $time\r\n" +
                "Category: $category\r\n" +
                "Content Length:  ${content?.length}\r\n" +
                "Tag: $tags\r\n" +
                "Comments Count: ${comments.size}"
    }
}