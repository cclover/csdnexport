package com.cc.csdnexport.wordpress

import java.math.BigInteger

/**
 * 记录使用了term的地方，主要是文章中使用了那些tag，是属于那个分类， 也有顶部菜单
 */
class TableTermRelation {


    constructor(objID: Long, taxonomyID: Long) {

        object_id = objID.toBigInteger()
        term_taxonomy_id = taxonomyID.toBigInteger()
    }

    /**
     * 对于文章tag和分类来说这里是文章postID
     */
    var object_id: BigInteger = BigInteger.ZERO

    var term_taxonomy_id: BigInteger = BigInteger.ZERO

    /**
     * 一般为0
     */
    var term_order: Int = 0

}