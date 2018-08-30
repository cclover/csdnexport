package com.cc.csdnexport.wordpress

import com.cc.csdnexport.config.ExportConfigManager
import com.cc.csdnexport.csdn.CSDNArticleInfo
import com.cc.csdnexport.csdn.CSDNCommentInfo
import java.sql.Connection

object WordPressImporter {


    /**
     * 导入到数据库，同步操作
     */
    @Synchronized
    fun import(article: CSDNArticleInfo) {

        var conn: Connection? = null
        try {

            conn = DBHelper.getConnection()

            if (conn != null) {

                conn.setAutoCommit(false)

                // 向数据库写入文章
                val postID = importPost(conn, article)

                // postid 无效
                if (postID == -1L) {
                    conn?.rollback() //回滚
                    return
                }

                // 向数据库插入评论
                importComments(conn, postID, article.comments)

                // 写入文章的类型
                if (!importCategory(conn, postID, article)) {
                    conn?.rollback() //回滚
                    return
                }


                // 写入文章的tag
                if (!importTags(conn, postID, article)) {
                    conn?.rollback() //回滚
                    return
                }

                conn.commit();//提交事务
            }

        } catch (ex: Exception) {

            ex.printStackTrace()
            conn?.rollback() //回滚

        } finally {
            DBHelper.closeConnection(conn)
        }
    }


    private fun importPost(conn: Connection, article: CSDNArticleInfo): Long {

        var post = TablePosts(article)
        return DBHelper.insertPosts(conn, post)
    }


    private fun importComments(conn: Connection, postID: Long, commentList: ArrayList<CSDNCommentInfo>) {

        var comments: ArrayList<TableComments> = arrayListOf();
        for (csdnComment in commentList) {
            comments.add(TableComments(postID, csdnComment))
        }

        DBHelper.insertComments(conn, comments)
    }


    private fun importCategory(conn: Connection, postID: Long, article: CSDNArticleInfo): Boolean {

        if (article.category.isNullOrEmpty()) {
            return true
        }

        // 从terms表获取这个分类的ID，没有就创建一个
        var term = TableTerms(article.category!!)
        val termID = DBHelper.queryOrCreateTermsID(conn, term)
        if (termID == -1L) {
            return false
        }

        // 查看这个terms是否设置为Category，否则创建一个， 目前都是以草稿发布，不更新数量
        var termTaxonomy = TableTermTaxonomy(termID, TableTermTaxonomy.CATEGORY, 0)
        val termTaxonomyID = DBHelper.updateOrCreateTermsTaxonomy(conn, termTaxonomy)
        if (termTaxonomyID == -1L) {
            return false
        }

        // termTaxonomy和post文章关联
        var termRelation = TableTermRelation(postID, termTaxonomyID)
        return DBHelper.insertTermsRelation(conn, termRelation)
    }


    private fun importTags(conn: Connection, postID: Long, article: CSDNArticleInfo): Boolean {

        if (article.tags.isEmpty()) {
            return true
        }

        for (tag in article.tags) {

            // 从terms表获取这个分类的ID，没有就创建一个
            var term = TableTerms(tag)
            val termID = DBHelper.queryOrCreateTermsID(conn, term)
            if (termID == -1L) {
                return false
            }

            // 查看这个terms是否设置为TAG，否则创建一个， 目前都是以草稿发布，不更新数量
            var termTaxonomy = TableTermTaxonomy(termID, TableTermTaxonomy.POST_TAG, 0)
            val termTaxonomyID = DBHelper.updateOrCreateTermsTaxonomy(conn, termTaxonomy)
            if (termTaxonomyID == -1L) {
                return false
            }

            // TAG termTaxonomy和post文章关联
            var termRelation = TableTermRelation(postID, termTaxonomyID)
            if (!DBHelper.insertTermsRelation(conn, termRelation)) {
                return false
            }
        }

        return true
    }

}