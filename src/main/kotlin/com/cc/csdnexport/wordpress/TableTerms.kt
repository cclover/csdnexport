package com.cc.csdnexport.wordpress

import java.math.BigInteger
import java.net.URLEncoder

/**
 * 术语表，包括文章的分类、tag等
 */
class TableTerms {

    constructor(term: String) {

        name = term
        slug = URLEncoder.encode(term, "UTF-8")
    }

    var term_id: BigInteger = BigInteger.ZERO

    /**
     * 显示的名字
     */
    var name: String = ""

    /**
     * 名字缩写，url encoding
     */
    var slug: String = ""

    /**
     * 分组，默认是0
     */
    var term_group: BigInteger = BigInteger.ZERO
}