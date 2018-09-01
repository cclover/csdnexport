# CSDNExport
从CSDN博客导出文章到Wordpress。支持文章内容、标签、评论、以及图片。


# 原理

主要使用了WebMagic爬虫框架爬取博客的内容。CSDN也提供了API获取，但是要注册开发者，而且API文档有些打不开，非常麻烦，所以采用爬虫方式。（测试时不要大量爬取，会被CSDN封IP，然后需要登陆）

爬取到的原始数据经过解析之后直接存入到Wordpress对应的表中。文章导入后都是草稿的状态。


# 使用
使用非常简单，通过配置文件配置要爬取的CSDN博客的信息，以及自己Wordpress的相关信息。


## config

源码中提供了一个config.template.json的文件，使用时修改成config.json， 和jar包放在同一个目录下。

``` json
Pullsoft ImageProtect
{
  "wordpress": {
    "DBIP": "127.0.0.1",
    "DBPort": 3306,
    "DBName": "wordpress",
    "DBUser": "wordpress",
    "DBPassword": "wordpress",
    "UserID": 1,
    "ImgUrl": "/wp-content/uploads/csdn/"
  },
  "csdn": {
    "userName": "csdn",
    "pageCount": 1,
    "onlyOriginal": true,
    "articles": [111,123,124]
  }
}
```

| Key  | Desc |
| ------  | ------ |
|DBIP|Wordpress 数据库服务器的IP地址|
|DBPort|Wordpress 数据库的端口号，MySQL默认3306|
|DBName|Wordpress数据库用户名（默认创建的用户可能没有远程连接的权限，需要在MySQL中授权）|
|DBPassword|Wordpress数据库用户密码|
| UserID | Wordpress用户的ID，如果只有自己一个注册用户，一般是1，可以从wp_users表查询 |
| ImgUrl | CSDN图片导入Wordpress文章是替换的图片的路径。因为CSDN图片有防盗链，所以下载下来，并把文章中的图片地址都替换成自己服务器的。所以这个是你文章图片在服务器的路径。Wordpress默认是绝对路径，所以前面要域名或IP，我的是使用相对路径，所以没有加域名|
|userName|要到如的CSDN博客的用户名|
|pageCount | CSDN博客文章列表的页数 （Web不是太熟悉，没找到页数怎么获取到的，后面找到的话就不用配置了）|
|onlyOriginal | 是否只导入原创文章|
|articles|可以导入指定文章的编号，如果不指定，默认导入全部文章|


## 运行
源码编译后会生成一个jar包 /csdnexport/out/artifacts/csdnexport_jar/csdnexport_main.jar

和config.json放在同一个目录下执行就可以了
```shell
java -jar csdnexport_main.jar
```

## 结果

运行目录下log目录会纪录运行结果和错误，csdn目录保存的是防盗链的图片。复制到服务器对应的目录就可以了。

每一篇文章导入都有相关log
```log
2018-09-01 14:39:17 [33369][pool-3-thread-1][DEBUG] Handle Article: https://blog.csdn.net/cc_net/article/details/560494
2018-09-01 14:39:17 [33415][pool-3-thread-1][DEBUG] Parse Article result:
ID: 560494
Title: CSDN!!我又来了
Time: 2005-12-23 20:04:00
Category: Others
Content Length:  1912
Tag: [vb , sql server , 编程 , javascript , asp.net , 数据结构 ]
Comments Count: 5
2018-09-01 14:39:17 [33415][pool-3-thread-1][DEBUG] Import to wordpress. Article: 560494
2018-09-01 14:39:17 [33439][pool-3-thread-1][DEBUG] New PostId: 345. Article: 560494
2018-09-01 14:39:17 [33505][pool-3-thread-1][DEBUG] Add Category:Others , TermTaxonomyID: 175, postID: 345, Article: 560494
2018-09-01 14:39:17 [33529][pool-3-thread-1][DEBUG] Add Tag:vb , TermTaxonomyID: 203, postID: 345, Article: 560494
2018-09-01 14:39:17 [33554][pool-3-thread-1][DEBUG] Add Tag:sql server , TermTaxonomyID: 149, postID: 345, Article: 560494
2018-09-01 14:39:17 [33581][pool-3-thread-1][DEBUG] Add Tag:编程 , TermTaxonomyID: 144, postID: 345, Article: 560494
2018-09-01 14:39:17 [33624][pool-3-thread-1][DEBUG] Add Tag:javascript , TermTaxonomyID: 207, postID: 345, Article: 560494
2018-09-01 14:39:17 [33669][pool-3-thread-1][DEBUG] Add Tag:asp.net , TermTaxonomyID: 208, postID: 345, Article: 560494
2018-09-01 14:39:17 [33691][pool-3-thread-1][DEBUG] Add Tag:数据结构 , TermTaxonomyID: 126, postID: 345, Article: 560494
2018-09-01 14:39:17 [33712][pool-3-thread-1][DEBUG] Import success. Article: 560494 --> Post: 345
```

50篇文章全部导入到服务器大概用了30秒。检查了一下没什么问题。

CSDN

![image](https://github.com/cclover/csdnexport/blob/master/images/csdn.png)

导入得到wordpress后，草稿列表

![image](https://github.com/cclover/csdnexport/blob/master/images/wordpress_articles.png)

数据库

![image](https://github.com/cclover/csdnexport/blob/master/images/wordpress_db.png)

防盗链的图片正常显示

![image](https://github.com/cclover/csdnexport/blob/master/images/worpress.png)


# 注意

1. 因为是爬虫，大量请求可能会被封IP
2. 采用XPATH来抓取网页的内容，CSDN页面布局可能导致失效
3. Wordpress版本是4.9.8
4. 使用前请做好数据库备份



