package com.devmeng.skinlib.skin

import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.devmeng.skinlib.skin.entity.SkinPair
import com.devmeng.skinlib.skin.entity.SkinView
import com.devmeng.skinlib.skin.utils.SkinThemeUtils
import com.devmeng.skinlib.skin.utils.Log

/**
 * Created by devmeng
 * Version : 1
 * Description :
 * 更换皮肤时所需更换的属性
 *
 * 注意: attributeList 在增加属性个体时
 * @see SkinView.applySkin 需在该方法的 switch 语句增加 case
 * @param skinTypeface 对应皮肤包中的特定字体
 */
class SkinAttribute(var skinTypeface: Typeface? = null) {

    companion object {
        val instance by lazy {
            SkinAttribute()
        }
    }

    val attributeList = mutableListOf(
        "background",
        "backgroundTint",
        "src",
        "textColor",
        "tint",
        "drawableLeft",
        "drawableStart",
        "drawableRight",
        "drawableEnd",
        "drawableTop",
        "drawableBottom",
        "drawableLeftCompat",
        "drawableStartCompat",
        "drawableRightCompat",
        "drawableEndCompat",
        "drawableTopCompat",
        "drawableBottomCompat",
        "drawableTint",

        //需局部更换字体时添加属性
        "skinTypeface"
        /** 注意: attributeList 在增加属性个体时需要对
        #SkinView 中的 applySkin() 方法的 switch 增加 case
         */
    )
    private var widgetAttrList: List<String> = listOf()
    private val skinViews = mutableListOf<SkinView>()

    /**
     * 获取并缓存 view 中换肤所需的属性
     * @param view 控件
     * @param attrs 控件的所有属性
     * 遍历所有属性，将每个元素与 attributeList 中的属性名称对比并缓存至 SkinPair 中
     * 并在应用所有的控件的换肤属性缓存完毕之后 更换皮肤
     * @see attributeList
     * @see SkinPair 缓存 view 所需换肤的所有属性
     * @see SkinView 需要换肤的控件，包含该控件以及属性、属性值
     */
    fun load(view: View, attrs: AttributeSet) {
        val skinPairList = mutableListOf<SkinPair>()

        //添加自定义 View 使用的自定义属性
        view.takeIf { view is SkinWidgetSupport }.let {
            (it as? SkinWidgetSupport)?.apply {
                widgetAttrList = attrsList
                attributeList.addAll(attrsList)
            }
        }

        for (index in 0 until attrs.attributeCount) {
            val attrName = attrs.getAttributeName(index)
            if (attributeList.contains(attrName)) {
                val attrValue = attrs.getAttributeValue(index)
                if (attrValue.startsWith("#").or(attrValue.contains("x"))
                        .or(!attrValue.startsWith("@"))
                ) {
                    //忽略值设置为"#"开头的属性
                    continue
                }
                //此处以 @resId 为场景截取 resourceId  例如: color="@color/color_black"
                var resId = Integer.parseInt(attrValue.substring(1))
                if (attrValue.startsWith("?")) {
                    //截取并获取属性ID, 通常为 Theme 中定义的属性 例如: color="?colorAccent"
                    val attrId = Integer.parseInt(attrValue.substring(1))
                    resId = SkinThemeUtils.getResId(view.context, intArrayOf(attrId))[0]
                }
                skinPairList.add(
                    SkinPair(
                        attrName,
                        resId
                    )
                )
            }
        }

        //应用并缓存 皮肤及view
        if (skinPairList.isNotEmpty().or(view is TextView).or(view is SkinWidgetSupport)) {

            val skinView =
                SkinView(view, skinPairList)
            skinView.applySkin(skinTypeface)
            skinViews.add(skinView)
        }

        //应用后移除自定义 View 使用的自定义属性
        attributeList.takeIf { widgetAttrList.isNotEmpty() }?.removeAll(widgetAttrList)
    }

    /**
     * 应用皮肤
     */
    fun applySkin() {
        //遍历需换肤控件并开始换肤
        for (skinView in skinViews) {
            skinView.applySkin(skinTypeface)
        }
    }

    /**
     * 用于对自定义 View
     */
    fun reflectSkinPair(view: View, attrs: List<String>): MutableList<SkinPair> {
        val skinPairList = mutableListOf<SkinPair>()
        var attrName: String
        var value: Int
        try {
            val viewClass = view.javaClass
            attrs.forEach {
                val attrField = viewClass.getDeclaredField(it)
                attrField.isAccessible = true
                attrName = attrField.name
                if (attrField.get(view) is Int) {
                    value = attrField.get(view) as Int
                    skinPairList.add(SkinPair(attrName, value))
                }
            }
        } catch (e: Exception) {
            Log.e(e.stackTraceToString())
        }
        return skinPairList
    }


}