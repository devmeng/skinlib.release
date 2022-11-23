package com.devmeng.skinlib.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import java.lang.ref.WeakReference

class StatusBarUtils {

    private lateinit var activity: Activity

    companion object {

        private val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            StatusBarUtils()
        }

        fun getInstance(activity: Activity): StatusBarUtils {
            instance.activity = WeakReference(activity).get()!!
            return instance
        }

    }

    /**
     * 改变状态栏文字颜色
     * 只有2种选择,白色与黑色
     * @param isLight
     */
    fun initStatusBarState(isLight: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = activity.window.decorView.windowInsetsController

            val state = if (isLight) APPEARANCE_LIGHT_STATUS_BARS else 0

            controller?.setSystemBarsAppearance(
                state,
                APPEARANCE_LIGHT_STATUS_BARS
            )
            return
        }
        activity.window.clearFlags(View.SYSTEM_UI_FLAG_FULLSCREEN)
        if (isLight) {
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            activity.window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            activity.window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

}