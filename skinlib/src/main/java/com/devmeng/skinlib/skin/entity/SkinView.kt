package com.devmeng.skinlib.skin.entity

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.widget.TextViewCompat
import com.devmeng.skinlib.skin.SkinWidgetSupport
import com.devmeng.skinlib.skin.utils.SkinResources.Companion.init

/**
 * Created by Richard
 * Version : 1
 * Description :
 * 需要换肤的 View
 * 换肤所需的属性、属性值 pairList
 *
 * @see .pairList
 * 应用皮肤: 遍历 pairList 获取换肤使用的属性及属性值
 * 并与 SkinAttribute.attributeList 中的元素一一对应
 *
 * @see .applySkin
 * @see com.devmeng.skinlib.skin.SkinAttribute
 *
 *
 * 换肤时通过 SkinResources 获取
 * 注: 如 SkinAttribute.attributeList 增加元素，
 * 则需要在 applySkin 方法的 switch 语句中增加方案 case
 */
data class SkinView(var view: View, var pairList: List<SkinPair>) {
    /**
     * 应用皮肤
     * 1.遍历 #pairList 获取换肤属性及其换肤前的属性值
     * 2.由 SkinResources 对象获取皮肤属性值
     * 皮肤属性对应 SkinAttribute.attributeList 中的元素
     *
     * @see com.devmeng.skinlib.skin.SkinAttribute
     * 注: 如 SkinAttribute.attributeList 增加元素，
     * 则需要在 switch 语句中增加方案 case 并从 SkinResources 中获取对应资源
     * @param typeface 用于对皮肤包中定义特定字体的切换
     */
    fun applySkin(typeface: Typeface?) {
        //全局更改字体
        typeface?.apply {
            applyTypeface(this)
        }
        applyWidgetSkin(pairList)
        for ((attrName, resId) in pairList) {
            val skinResources = init(view.context.applicationContext)
            var top: Drawable? = null
            var bottom: Drawable? = null
            var left: Drawable? = null
            var right: Drawable? = null
            when (attrName) {
                "background" -> {
                    val background = skinResources.getBackground(resId)
                    if (background is Int) {
                        view.setBackgroundColor(background)
                    } else {
                        ViewCompat.setBackground(view, background as Drawable)
                    }
                }
                "backgroundTint" -> {
                    val colorStateList = skinResources.getColorStateList(resId)
                    view.backgroundTintList = colorStateList
                }
                "android:src" -> {
                    val drawable = skinResources.getDrawable(resId)
                    (view as ImageView).setImageDrawable(drawable)
                }
                "textColor" -> (view as TextView).setTextColor(skinResources.getColorStateList(resId))
                "tint" -> (view as ImageView).imageTintList = skinResources.getColorStateList(resId)
                "drawableLeft", "drawableLeftCompat", "drawableStart", "drawableStartCompat" -> left =
                    skinResources.getDrawable(resId)
                "drawableRight", "drawableRightCompat", "drawableEnd", "drawableEndCompat" -> right =
                    skinResources.getDrawable(resId)
                "drawableTop", "drawableTopCompat" -> top = skinResources.getDrawable(resId)
                "drawableBottom", "drawableBottomCompat" -> bottom =
                    skinResources.getDrawable(resId)
                "drawableTint" -> TextViewCompat.setCompoundDrawableTintList(
                    (view as TextView), skinResources.getColorStateList(resId)
                )
                //局部更改字体
//                "skinTypeface" -> applyTypeface(skinResources.getTypeface(resId))

            }
            view.takeIf { view is TextView }.apply {
                (view as? TextView)?.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    left,
                    top,
                    right,
                    bottom
                )
            }
        }
    }

    /**
     * 应用皮肤包字体
     */
    private fun applyTypeface(skinTypeface: Typeface) {
        view.takeIf { view is TextView }.apply {
            (view as? TextView)?.typeface = skinTypeface
        }
    }

    private fun applyWidgetSkin(pairList: List<SkinPair>) {
        view.takeIf { view is SkinWidgetSupport }.apply {
            (view as? SkinWidgetSupport)?.applySkin(pairList)
        }
    }

}