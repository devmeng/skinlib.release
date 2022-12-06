package com.devmeng.skinlib.skin.utils

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import com.devmeng.skinlib.R

/**
 * Created by devmeng
 * Version : 1
 * Description :
 * 换肤工具类
 */
object SkinThemeUtils {

    private val APPCOMPAT_COLOR_PRIMARY_VARIANT =
        intArrayOf(androidx.appcompat.R.attr.colorPrimaryDark)

    private val SYSTEM_BAR_ATTRS =
        intArrayOf(android.R.attr.statusBarColor, android.R.attr.navigationBarColor)

    private val SYSTEM_STATUS_BAR_LIGHT_MODE = intArrayOf(android.R.attr.windowLightStatusBar)

    private val TYPEFACE_ATTR =
        intArrayOf(R.attr.skinTypeface)

    fun updateStatusBarState(activity: Activity) {
        //获取statusBarColor与navigationBarColor  颜色值
        val statusBarId = getResId(activity, SYSTEM_BAR_ATTRS)
        val statusBarMode = getResId(activity, SYSTEM_STATUS_BAR_LIGHT_MODE)
        if (statusBarId[0] > 0) {
            //如果statusBarColor配置颜色值，就换肤
            activity.window.statusBarColor = SkinResources.instance.getColor(statusBarId[0])
            if (statusBarMode[0] > 0) {
                val isBarLight = SkinResources.instance.getBoolean(statusBarMode[0])
                Log.e("is bar light -> $isBarLight")
                StatusBarUtils.getInstance(activity).initStatusBarState(isBarLight)
            }
        }
    }

    fun updateNavigationBarState(activity: Activity) {
        //获取statusBarColor与navigationBarColor  颜色值
        val statusBarId = getResId(activity, SYSTEM_BAR_ATTRS)
        if (statusBarId[1] > 0) {
            //如果statusBarColor配置颜色值，就换肤
            activity.window.navigationBarColor =
                SkinResources.instance.getColor(statusBarId[0]);
            return
        }
        val resId = getResId(activity, APPCOMPAT_COLOR_PRIMARY_VARIANT)[0]
        if (resId == 0) {
            return
        }
        activity.window.navigationBarColor = SkinResources.instance.getColor(resId);
    }

    fun getSkinTypeface(activity: Activity): Typeface {
        val typefaceId = getResId(activity, TYPEFACE_ATTR)[0]
        return SkinResources.instance.getTypeface(typefaceId)
    }

    /**
     * 获取属性对应的资源 id
     * @param context
     * @param attrs 属性集合
     * @return resArr 资源 id 集合
     */
    fun getResId(context: Context, attrs: IntArray): IntArray {
        val resArr = IntArray(attrs.size)
        val typedArray = context.obtainStyledAttributes(attrs)
        for (index in 0 until typedArray.length()) {
            resArr[index] = typedArray.getResourceId(index, 0)
        }
        typedArray.recycle()
        return resArr

    }

}