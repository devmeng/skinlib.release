package com.devmeng.skinlib.skin.utils

import android.content.Context
import android.content.SharedPreferences
import com.devmeng.skinlib.skin.EMPTY

/**
 * Created by Richard
 * Version : 1
 * Description :
 * 使用 SharedPreference 存储皮肤包路径
 */
class SkinPreference private constructor() {

    private lateinit var context: Context
    private lateinit var sp: SharedPreferences

    companion object {

        const val SP_NAME_SKINS = "sp_skins"

        const val KEY_SKIN_PATH = "sp_skin_path"

        const val KEY_DAY_NIGHT_MODE = "sp_day_night_mode"

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SkinPreference()
        }

        fun init(context: Context): SkinPreference {
            instance.context = context
            instance.sp = context.getSharedPreferences(SP_NAME_SKINS, Context.MODE_PRIVATE)
            return instance
        }

    }

    fun setSkinPath(skinPath: String = EMPTY) {
        sp.edit().putString(KEY_SKIN_PATH, skinPath).apply()
    }

    fun getSkinPath(): String {
        return sp.getString(KEY_SKIN_PATH, EMPTY).toString()
    }

}