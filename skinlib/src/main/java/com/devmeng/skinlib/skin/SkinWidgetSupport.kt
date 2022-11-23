package com.devmeng.skinlib.skin

import com.devmeng.skinlib.skin.entity.SkinPair

/**
 * Created by Richard -> MHS
 * Version : 1
 * 对于自定义 View 的皮肤应用
 */
interface SkinWidgetSupport {
    /**
     * 所有与皮肤有关的属性 集合
     */
    val attrsList: MutableList<String>

    /**
     * 应用皮肤资源
     */
    fun applySkin(pairList: List<SkinPair>)
}