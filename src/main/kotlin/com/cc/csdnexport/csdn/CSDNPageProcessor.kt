package com.cc.csdnexport.csdn

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.cc.csdnexport.config.Constant
import com.cc.csdnexport.config.ExportConfigManager
import com.cc.csdnexport.tool.HttpDownloader
import com.cc.csdnexport.tool.LogUtils
import com.cc.csdnexport.wordpress.WordPressImporter
import us.codecraft.webmagic.Page
import us.codecraft.webmagic.Site
import us.codecraft.webmagic.processor.PageProcessor
import us.codecraft.webmagic.selector.Html
import us.codecraft.webmagic.selector.Selectable
import java.io.File
import java.util.regex.Pattern

class CSDNPageProcessor : PageProcessor {

    private var handleList: ArrayList<String> = arrayListOf()

    override fun getSite(): Site {

        return ExportConfigManager.REQUEST_CONFIG
    }


    override fun process(page: Page) {

        if (isArticleListUrl(page.url)) {

            //当返回了文章列表页的请求，从页面中查找所有文章并处理
            try {
                handleArticleList(page)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (isArticleLink(page.url)) {

            //当返回了文章页的请求，从页面中获取文章信息
            try {
                handleArticle(page)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /**
     * 处理文章列表，访问每一篇文章
     */
    fun handleArticleList(page: Page) {

        LogUtils.d("Handle article list: ${page.url}")

        // 获取文章列表下的的所有的文章连接 ，指定xpath，如果不限定，需要过滤相同的文章地址
        val linkList = page.html.xpath(Constant.XPATH_ARTICLE_MAIN).links()
        for (link in linkList.nodes()) {
            if (isArticleLink(link) && !handleList.contains(link.get())) {

                //println("Article Link: ${link.get()}")
                handleList.add(link.get())
                page.addTargetRequest(link.get())

            }
        }
        //println("Page: ${page.url} \r\n Total Article count: ${handleList.size}")
    }


    /**
     * 是否文章列表
     */
    private fun isArticleListUrl(url: Selectable): Boolean {

        return url.regex(ExportConfigManager.getArticleListUrlRegex()).match()
    }


    /**
     * 是否是文章连接
     */
    private fun isArticleLink(url: Selectable): Boolean {

        return url.regex(ExportConfigManager.getArticleUrlRegex()).match();
    }


    /**
     * 处理文章详细页面数据
     */
    private fun handleArticle(page: Page): Unit {


        LogUtils.d("Handle Article: ${page.url}")

        // 文章id
        val articleUrl = page.url.get()
        var articleId = ""
        val index = articleUrl.lastIndexOf("/")
        if (index > 0) {
            articleId = articleUrl.substring(index + 1)
        }
        if (articleId.isEmpty()) {

            LogUtils.e("The articleId is empty: ${page.url}")
            return
        }

        // 标题
        val title = page.html.xpath(Constant.XPATH_ARTICLE_TITLE).get()
        if (title.isNullOrEmpty()) {
            LogUtils.e("The article title empty: ${page.url}")
            return
        }

        // 内容
        var tmpContent = page.html.xpath(Constant.XPATH_ARTICLE_CONTENT).get()
        if (tmpContent.isNullOrEmpty()) {
            println("The content title empty: ${page.url}")
            return
        }
        // 过滤一下
        tmpContent = tmpContent.substringAfter(Constant.CONTENT_BEGIN).substringBeforeLast(Constant.CONTENT_END)

        // 下载CSDN的图片并替换文章中的链接
        val content = handleCSDNImage(page.html, tmpContent)

        // 时间
        val time = page.html.xpath(Constant.XPATH_ARTICLE_TIME).get()
        val postTime = formatTime(time)

        // 类别
        val category = page.html.xpath(Constant.XPATH_ARTICLE_CATEGORY).get()

        // tag列表
        val tagList = page.html.xpath(Constant.XPATH_ARTICLE_TAGS).all()


        // 生成CSDNArticleInfo对象
        val articleInfo = CSDNArticleInfo()
        articleInfo.articleId = articleId
        articleInfo.url = page.url.get()
        articleInfo.title = title
        articleInfo.time = postTime
        articleInfo.category = category
        articleInfo.content = content
        articleInfo.tags.addAll(tagList)
        articleInfo.comments.addAll(requestComments(articleId))

        LogUtils.d("Parse Article result:\r\n$articleInfo")

        // 写入到数据库
        WordPressImporter.import(articleInfo)
    }


    /**
     * 处理CSDN图片防盗链的问题
     */
    private fun handleCSDNImage(html: Html, content: String): String {

        val imgList = html.xpath(Constant.XPATH_ARTICLE_IMG).all()
        if (imgList == null || imgList.isEmpty()) {
            return content
        }

        // 下载图片
        for (imgDiv in imgList) {

            // 获取img地址，如果是csdn的图片，替换并下载
            val pattern = Pattern.compile(Constant.REGEX_CSDN_IMG)
            val matcher = pattern.matcher(imgDiv)

            // 匹配
            if (matcher.find()) {

                val imgUrl = matcher.group()
                val fileName = matcher.group("name")

                // 创建下载目录
                val imgDir = File(System.getProperty("user.dir"), Constant.DOWNLOAD_IMAGE_DIR)
                if (!imgDir.exists()) {
                    imgDir.mkdirs()
                }
                val imgFile = File(imgDir, fileName)
                if (imgFile.exists()) {
                    continue
                }

                //下载图片
                HttpDownloader.downloadFile(imgUrl, imgFile)
            }
        }

        //替换图片路径
        val contentBuffer = StringBuffer(content)
        return contentBuffer.replace(Regex(Constant.REGEX_CSDN_IMG_PATH), ExportConfigManager.getImagePath())
    }


    /**
     * 获取评论信息
     */
    private fun requestComments(id: String): List<CSDNCommentInfo> {

        val commentList: ArrayList<CSDNCommentInfo> = arrayListOf()

        if (id.isEmpty()) {
            return commentList
        }

        var pageIndex: Int = 1
        var pageCount: Int = 1

        // 评论可能有多页
        while (pageIndex <= pageCount) {

            // 请求评论数据
            val commentUrl = ExportConfigManager.getCommentListUrl(id, pageIndex++)
            val result = HttpDownloader.download(commentUrl)

            // 处理请求结果
            if (!result.isNullOrEmpty()) {

                val info = JSON.parse(result)
                if (info is JSONObject) {

                    // 解析
                    if (info.isEmpty() || info.size == 0) {
                        return commentList
                    }

                    // 数据段
                    val data = info.getJSONObject("data")
                    if (data == null || data.isEmpty()) {
                        return commentList
                    }

                    // 获取评论页数
                    pageCount = data.getIntValue("page_count")

                    // 获取评论信息
                    val list = data.getJSONArray("list")
                    if (list.size > 0) {

                        for (comment in list) {

                            val commentObject = comment as JSONObject

                            // 评论
                            val jsonInfo = commentObject.getJSONObject("info")
                            val commentInfo = parseComment(jsonInfo)
                            if (commentInfo != null) {
                                commentList.add(commentInfo)
                            }

                            // 评论下的子评论
                            val subArray = commentObject.getJSONArray("sub")
                            if (subArray == null || subArray.isEmpty()) {
                                continue
                            }
                            for (subInfo in subArray) {

                                val subCommentInfo = parseComment(subInfo as JSONObject)
                                if (subCommentInfo != null) {

                                    //[reply]Renmingqiang[/reply] 处理一下，改为回复
                                    subCommentInfo.comment = subCommentInfo.comment?.replace("[reply]", "回复：")?.replace("[/reply]", " ")
                                    commentList.add(subCommentInfo)
                                }
                            }
                        }
                    }
                }
            }
        }

        return commentList
    }


    private fun parseComment(jsonInfo: JSONObject): CSDNCommentInfo? {

        try {

            val commentInfo = CSDNCommentInfo()
            commentInfo.articleId = jsonInfo.getString("ArticleId")
            commentInfo.time = jsonInfo.getString("PostTime")
            commentInfo.comment = jsonInfo.getString("Content")
            commentInfo.name = jsonInfo.getString("UserName")
            commentInfo.ip = jsonInfo.getString("IP")
            commentInfo.avatar = jsonInfo.getString("Avatar")
            commentInfo.nickName = jsonInfo.getString("NickName")
            commentInfo.link = Constant.COMMENT_USER_LINK_URL.format(commentInfo.name)
            commentInfo.isSelf = ExportConfigManager.getCSDNUserName().equals(commentInfo.name) //是否自己回复

            // 时间格式变化一下
            if (!commentInfo.time.isNullOrEmpty()) {
                commentInfo.time = formatTime(commentInfo.time!!)
            }

            return commentInfo

        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }


    private fun formatTime(time: String): String {
        return time.replace("年", "-").replace("月", "-").replace("日", "")
    }
}