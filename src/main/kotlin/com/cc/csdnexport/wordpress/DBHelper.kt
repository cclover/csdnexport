package com.cc.csdnexport.wordpress

import com.cc.csdnexport.config.Constant
import com.cc.csdnexport.config.ExportConfigManager
import org.apache.commons.dbcp2.BasicDataSource
import java.net.URLEncoder
import java.sql.Connection
import java.sql.Statement
import java.sql.Types


object DBHelper {


    const val SQL_POST_INSERT = "insert into wp_posts (post_author, post_date, post_date_gmt, post_content, post_title, post_excerpt, post_status, comment_status, ping_status, post_password, post_name, to_ping, pinged, post_modified, post_modified_gmt, post_content_filtered, post_parent, guid, menu_order, post_type, post_mime_type, comment_count) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

    const val SQL_COMMENT_INSERT = "insert into wp_comments (comment_post_ID, comment_author, comment_author_email, comment_author_url, comment_author_IP, comment_date, comment_date_gmt, comment_content, comment_karma, comment_approved, comment_agent, comment_type, comment_parent, user_id) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

    const val SQL_TERM_QUERY_ID = "select term_id from wp_terms where name = ?"

    const val SQL_TERM_INSERT = "insert into wp_terms (name, slug, term_group) values (?,?,?)"

    const val SQL_TERM_TAXONOMY_QUERY_ID = "select  term_taxonomy_id from wp_term_taxonomy where term_id = ?"

    const val SQL_TERM_TAXONOMY_UPDATE_COUNT = "update wp_term_taxonomy set count = count + 1 where term_id = ?"

    const val SQL_TERM_TAXONOMY_INSERT = "insert into wp_term_taxonomy (term_id, taxonomy, description, parent, count) values (?,?,?,?,?)"


    const val SQL_TERM_RELATION_INSERT = "insert into wp_term_relationships value (?,?,?)"

    var dataSource: BasicDataSource? = null

    init {

        // 初始化启动
        initDB()
    }


    private fun initDB() {
        try {

            Class.forName(Constant.MYSQL_DRIVER_NEW)

            //设置DB信息
            val dbInfo = ExportConfigManager.getDBInfo()
            dataSource = BasicDataSource()
            dataSource?.driverClassName = Constant.MYSQL_DRIVER_NEW
            dataSource?.url = dbInfo.first
            dataSource?.username = dbInfo.second
            dataSource?.password = dbInfo.third

            //设置连接池
            dataSource?.initialSize = 2
            dataSource?.maxTotal = 5
            dataSource?.maxIdle = 2

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 获取一个连接
     *
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getConnection(): Connection? {

        try {
            return dataSource?.getConnection();
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }

    }

    /**
     * 关闭给定的连接
     */
    fun closeConnection(conn: Connection?) {

        try {
            conn?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 向数据库插入文章，没有检查重复
     */
    fun insertPosts(conn: Connection, post: TablePosts): Long {

        // 插入数据库
        val insertStatement = conn.prepareStatement(SQL_POST_INSERT, Statement.RETURN_GENERATED_KEYS)
        insertStatement.setObject(1, post.post_author, Types.BIGINT)
        insertStatement.setTimestamp(2, post.post_date)
        insertStatement.setTimestamp(3, post.post_date_gmt)
        insertStatement.setString(4, post.post_content)
        insertStatement.setString(5, post.post_title)
        insertStatement.setString(6, post.post_excerpt)
        insertStatement.setString(7, post.post_status)
        insertStatement.setString(8, post.comment_status)
        insertStatement.setString(9, post.ping_status)
        insertStatement.setString(10, post.post_password)
        insertStatement.setString(11, post.post_name)
        insertStatement.setString(12, post.to_ping)
        insertStatement.setString(13, post.pinged)
        insertStatement.setTimestamp(14, post.post_modified)
        insertStatement.setTimestamp(15, post.post_modified_gmt)
        insertStatement.setString(16, post.post_content_filtered)
        insertStatement.setObject(17, post.post_parent, Types.BIGINT)
        insertStatement.setString(18, post.guid)
        insertStatement.setInt(19, post.menu_order)
        insertStatement.setString(20, post.post_type)
        insertStatement.setString(21, post.post_mime_type)
        insertStatement.setObject(22, post.comment_count, Types.BIGINT)
        insertStatement.executeUpdate()

        // 获取插入的ID
        var articleId = -1L;
        val rs = insertStatement.generatedKeys
        if (rs.next()) {
            articleId = rs.getLong(1)
            println("生成记录的key为 ：$articleId")
        }

        return articleId
    }


    /**
     * 向数据库插入评论
     */
    fun insertComments(conn: Connection, comments: List<TableComments>) {

        val pst = conn.prepareStatement(SQL_COMMENT_INSERT)
        for (comment in comments) {
            pst.setObject(1, comment.comment_post_ID, Types.BIGINT)
            pst.setString(2, comment.comment_author)
            pst.setString(3, comment.comment_author_email)
            pst.setString(4, comment.comment_author_url)
            pst.setString(5, comment.comment_author_IP)
            pst.setTimestamp(6, comment.comment_date)
            pst.setTimestamp(7, comment.comment_date_gmt)
            pst.setString(8, comment.comment_content)
            pst.setInt(9, comment.comment_karma)
            pst.setString(10, comment.comment_approved)
            pst.setString(11, comment.comment_agent)
            pst.setString(12, comment.comment_type)
            pst.setObject(13, comment.comment_parent, Types.BIGINT)
            pst.setObject(14, comment.user_id, Types.BIGINT)
            pst.addBatch()
        }

        // 执行批量更新
        pst.executeBatch()
    }


    /**
     * 查询term，如果没有插入一条，返回ID
     */
    fun queryOrCreateTermsID(conn: Connection, term: TableTerms): Long {

        val pst = conn.prepareStatement(SQL_TERM_QUERY_ID)
        pst.setString(1, term.name)
        val rs = pst.executeQuery()
        var termID = -1L
        if (rs.next()) {

            // 获取当前的ID
            termID = rs.getLong(1)

        } else {

            // 插入一条新的
            val pst2 = conn.prepareStatement(SQL_TERM_INSERT, Statement.RETURN_GENERATED_KEYS)
            pst2.setString(1, term.name)
            pst2.setString(2, term.slug)
            pst2.setObject(3, term.term_group, Types.BIGINT)

            if (pst2.executeUpdate() > 0) {

                val rs2 = pst2.generatedKeys
                if (rs2.next()) {
                    termID = rs2.getLong(1)
                }
            }
        }
        return termID
    }


    /**
     * 如果有对应的taxonomy记录，如果没有创建一个, (目前发布的是草稿，不需要更新Taxonomy 的count)
     */
    fun updateOrCreateTermsTaxonomy(conn: Connection, termTaxonomy: TableTermTaxonomy): Long {

        var termTaxonomyID = -1L
        val pst = conn.prepareStatement(SQL_TERM_TAXONOMY_QUERY_ID)
        pst.setObject(1, termTaxonomy.term_id, Types.BIGINT)
        val rs1 = pst.executeQuery()

        if (rs1.next()) {
            termTaxonomyID = rs1.getLong(1)

        } else {

            val pst2 = conn.prepareStatement(SQL_TERM_TAXONOMY_INSERT, Statement.RETURN_GENERATED_KEYS)
            pst2.setObject(1, termTaxonomy.term_id, Types.BIGINT)
            pst2.setString(2, termTaxonomy.taxonomy)
            pst2.setString(3, termTaxonomy.description)
            pst2.setObject(4, termTaxonomy.parent, Types.BIGINT)
            pst2.setObject(5, termTaxonomy.count, Types.BIGINT)

            if (pst2.executeUpdate() > 0) {

                val rs2 = pst2.generatedKeys
                if (rs2.next()) {
                    termTaxonomyID = rs2.getLong(1)
                }
            }
        }

        return termTaxonomyID
    }


    /**
     * 建立term和文章的关系
     */
    fun insertTermsRelation(conn: Connection, termRelation: TableTermRelation): Boolean {

        val pst = conn.prepareStatement(SQL_TERM_RELATION_INSERT)
        pst.setObject(1, termRelation.object_id, Types.BIGINT)
        pst.setObject(2, termRelation.term_taxonomy_id, Types.BIGINT)
        pst.setInt(3, termRelation.term_order)
        return pst.executeUpdate() > 0
    }
}