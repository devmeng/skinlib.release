package com.devmeng.skinlib.utils

import android.util.Log
import com.devmeng.skinlib.skin.BUILD_TYPE
import com.devmeng.skinlib.skin.SkinManager

/**
 * Created by devmeng -> MHS
 * Date : 2022/5/30  16:14
 * Version : 1
 */
object Log {
    var TAG: String = "SKIN"

    @JvmStatic
    fun d(msg: String) {
        if (BUILD_TYPE) {
            Log.d(TAG, "msg -> $msg")
        }
    }

    @JvmStatic
    fun e(errorMsg: String) {
        if (BUILD_TYPE) {
            Log.e(TAG, "errorMsg -> $errorMsg")
        }
    }

    @JvmStatic
    fun i(info: String) {
        if (BUILD_TYPE) {
            Log.i(TAG, "<[$info]>")
        }
    }

    @JvmStatic
    fun w(warnMsg: String) {
        if (BUILD_TYPE) {
            Log.w(TAG, "<!![$warnMsg]!!>")
        }
    }
}