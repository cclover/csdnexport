package com.cc.csdnexport.wordpress

import com.cc.csdnexport.csdn.CSDNArticleInfo
import com.cc.csdnexport.csdn.CSDNCommentInfo
import com.cc.csdnexport.tool.LogUtils
import java.sql.Connection

object WordPressImporter {


    /**
     * 导入到数据库，同步操作
     */
    fun import(article: CSDNArticleInfo) {

        LogUtils.d("Import to wordpress. Article: ${article.articleId}")

        var conn: Connection? = null
        try {

            conn = DBHelper.getConnection()

            if (conn != null) {

                conn.setAutoCommit(false)

                // 向数据库写入文章
                val postID = importPost(conn, article)

                // postid 无效
                if (postID == -1L) {

                    LogUtils.e("Failed to insert post! Article: ${article.articleId}")
                    conn.rollback() //回滚
                    return
                }
                LogUtils.d("New PostId: $postID. Article: ${article.articleId}")

                // 向数据库插入评论
                if (!importComments(conn, postID, article.comments)) {

                    LogUtils.e("Failed to insert comments! Article: ${article.articleId}")
                    conn.rollback() //回滚
                    return
                }

                // 写入文章的类型
                if (!importCategory(conn, postID, article)) {

                    LogUtils.e("Failed to insert category! Article: ${article.articleId}")
                    conn.rollback() //回滚
                    return
                }


                // 写入文章的tag
                if (!importTags(conn, postID, article)) {

                    LogUtils.e("Failed to insert tags! Article: ${article.articleId}")
                    conn.rollback() //回滚
                    return
                }

                conn.commit();//提交事务

                LogUtils.d("Import success. Article: ${article.articleId} --> Post: $postID")
            }

        } catch (ex: Exception) {

            ex.printStackTrace()
            conn?.rollback() //回滚

            LogUtils.e("MySql exception!!")
            LogUtils.e(ex)

        } finally {
            DBHelper.closeConnection(conn)
        }
    }


    private fun importPost(conn: Connection, article: CSDNArticleInfo): Long {

        var post = TablePosts(article)
        return DBHelper.insertPosts(conn, post)
    }


    private fun importComments(conn: Connection, postID: Long, commentList: ArrayList<CSDNCommentInfo>): Boolean {

        var comments: ArrayList<TableComments> = arrayListOf();
        for (csdnComment in commentList) {
            comments.add(TableComments(postID, csdnComment))
        }

        return DBHelper.insertComments(conn, comments)
    }


    private fun importCategory(conn: Connection, postID: Long, article: CSDNArticleInfo): Boolean {

        if (article.category.isNullOrEmpty()) {

            LogUtils.d("The article do not have category! Article: ${article.articleId}")
            return true
        }

        // 从terms表获取这个分类的ID，没有就创建一个
        val term = TableTerms(article.category!!)
        val termID = DBHelper.queryOrCreateTermsID(conn, term)
        if (termID == -1L) {

            LogUtils.d("Failed to create category terms: ${article.category}! Article: ${article.articleId}")
            return false
        }
        //LogUtils.d("Category TermID: $termID. Term:${article.category} Article: ${article.articleId}")

        // 查看这个terms是否设置为Category，否则创建一个， 目前都是以草稿发布，不更新数量
        val termTaxonomy = TableTermTaxonomy(termID, TableTermTaxonomy.CATEGORY, 0)
        val termTaxonomyID = DBHelper.updateOrCreateTermsTaxonomy(conn, termTaxonomy)
        if (termTaxonomyID == -1L) {

            LogUtils.d("Failed to create category terms taxonomy! Article: ${article.articleId}")
            return false
        }
        //LogUtils.d("Category TermTaxonomyID: $termTaxonomyID. Term:${article.category} Article: ${article.articleId}")


        LogUtils.d("Add Category:${article.category}, TermTaxonomyID: $termTaxonomyID, postID: $postID, Article: ${article.articleId}")

        // termTaxonomy和post文章关联
        val termRelation = TableTermRelation(postID, termTaxonomyID)
        return DBHelper.insertTermsRelation(conn, termRelation)
    }


    private fun importTags(conn: Connection, postID: Long, article: CSDNArticleInfo): Boolean {

        if (article.tags.isEmpty()) {

            LogUtils.d("The article do not have tags! Article: ${article.articleId}")
            return true
        }

        for (tag in article.tags) {

            // 从terms表获取这个分类的ID，没有就创建一个
            val term = TableTerms(tag)
            val termID = DBHelper.queryOrCreateTermsID(conn, term)
            if (termID == -1L) {

                LogUtils.d("Failed to create category terms: $tag! Article: ${article.articleId}")
                return false
            }
            //LogUtils.d("Tag TermID: $termID. Term:${article.category} Article: ${article.articleId}")


            // 查看这个terms是否设置为TAG，否则创建一个， 目前都是以草稿发布，不更新数量
            val termTaxonomy = TableTermTaxonomy(termID, TableTermTaxonomy.POST_TAG, 0)
            val termTaxonomyID = DBHelper.updateOrCreateTermsTaxonomy(conn, termTaxonomy)
            if (termTaxonomyID == -1L) {

                LogUtils.d("Failed to create tags terms taxonomy! Article: ${article.articleId}")
                return false
            }
            //LogUtils.d("Tag TermTaxonomyID: $termTaxonomyID. Term:${article.category} Article: ${article.articleId}")


            LogUtils.d("Add Tag:$tag, TermTaxonomyID: $termTaxonomyID, postID: $postID, Article: ${article.articleId}")

            // TAG termTaxonomy和post文章关联
            val termRelation = TableTermRelation(postID, termTaxonomyID)
            if (!DBHelper.insertTermsRelation(conn, termRelation)) {
                return false
            }
        }

        return true
    }

}