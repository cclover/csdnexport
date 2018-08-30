package com.cc.csdnexport.wordpress

import java.math.BigInteger

/**
 * TableTerms只记录术语，而这个表记录各种类型的terms
 */
class TableTermTaxonomy {

    companion object {
        const val CATEGORY = "category"
        const val POST_TAG = "post_tag"
        const val NAV_MENU = "nav_menu"
    }


    constructor(termID: Long, taxonomy: String, count: Long) {

        term_id = termID.toBigInteger()
        this.taxonomy = taxonomy
        this.count = count.toBigInteger()
    }

    var term_taxonomy_id: BigInteger = BigInteger.ZERO

    /**
     * 关了TableTerms
     */
    var term_id: BigInteger = BigInteger.ZERO

    /**
     * 术语表分类
     * category 文章分类
     * nav_menu 导航菜单
     * post_tag 文章tag
     */
    var taxonomy: String = ""

    /**
     * 描述，一般是空
     */
    var description: String = ""

    /**
     * 父term，一般为0
     */
    var parent: BigInteger = BigInteger.ZERO


    /**
     * 使用这个term的数量
     */
    var count: BigInteger = BigInteger.ZERO
}