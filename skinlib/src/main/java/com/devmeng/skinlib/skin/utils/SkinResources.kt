package com.devmeng.skinlib.skin.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import com.devmeng.skinlib.skin.EMPTY

/**
 * Created by devmeng
 * Version : 1
 * Description :
 */
class SkinResources private constructor() {

    private var context: Context? = null
    var skinResources: Resources? = null
    private var skinPkgName: String = EMPTY
    private var isDefaultSkin = true

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SkinResources()
        }

        fun init(context: Context): SkinResources {
            instance.context = context
            return instance
        }

    }

    fun getDimension(resId: Int, resources: Resources = context?.resources!!): Float {
        val skinRes = getIdentifierFromRes(resources, resId)
        if (isDefaultSkin.or(skinRes == 0)) {
            return context?.resources!!.getDimension(resId)
        }
        val dimension = try {
            skinResources?.getDimension(skinRes)!!
        } catch (_: Exception) {
            context?.resources!!.getDimension(skinRes)
        }
        return dimension
    }

    fun getColor(resId: Int, resources: Resources = context?.resources!!): Int {
        val skinRes = getIdentifierFromRes(resources, resId)
        if (isDefaultSkin.or(skinRes == 0)) {
            return context?.getColor(resId)!!
        }
        val color = try {
            skinResources?.getColor(skinRes, null)!!
        } catch (_: Exception) {
            context?.getColor(skinRes)!!
        }
        return color
    }

    fun getColorId(resId: Int, resources: Resources = context?.resources!!): Int {
        val skinRes = getIdentifierFromRes(resources, resId)
        if (isDefaultSkin.or(skinRes == 0)) {
            return resId
        }
        return skinRes
    }

    fun getColorStateList(resId: Int, resources: Resources = context?.resources!!): ColorStateList? {
        val skinRes = getIdentifierFromRes(resources, resId)
        if (isDefaultSkin.or(skinRes == 0)) {
            return context?.getColorStateList(resId)
        }
        val colorStateList = try {
            skinResources?.getColorStateList(skinRes, null)
        } catch (_: Exception) {
            context?.getColorStateList(skinRes)
        }
        return colorStateList
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getDrawable(resId: Int, resources: Resources = context?.resources!!): Drawable? {
        val skinRes = getIdentifierFromRes(resources, resId)
        if (isDefaultSkin.or(skinRes == 0)) {
            return context?.getDrawable(resId)
        }
        val drawable = try {
            skinResources?.getDrawable(skinRes, null)
        } catch (_: Exception) {
            context?.getDrawable(skinRes)
        }
        return drawable
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getDrawableId(resId: Int, resources: Resources = context?.resources!!): Int {
        val skinRes = getIdentifierFromRes(resources, resId)
        if (isDefaultSkin.or(skinRes == 0)) {
            return resId
        }
        return skinRes
    }

    fun getBackground(resId: Int, resources: Resources = context?.resources!!): Any? {
        val resTypeName = resources.getResourceTypeName(resId)

        return if ("color" == resTypeName) {
            getColor(resId, resources)
        } else {
            getDrawable(resId, resources)
        }
    }

    fun getBoolean(resId: Int, resources: Resources = context?.resources!!): Boolean {
        val skinRes = getIdentifierFromRes(resources, resId)
        if (isDefaultSkin.or(skinRes == 0)) {
            return context?.resources!!.getBoolean(resId)
        }
        val bool = try {
            skinResources?.getBoolean(skinRes)!!
        } catch (_: Exception) {
            context?.resources!!.getBoolean(skinRes)
        }
        return bool
    }

    fun getTypeface(resId: Int, resources: Resources = context?.resources!!): Typeface {
        val typefacePath = getTypefaceString(resources, resId)
        if (typefacePath.isEmpty()) {
            return Typeface.DEFAULT
        }
        try {
            if (isDefaultSkin) {
                //???????????????????????????????????????????????????????????????
                // ?????????app -> value.xml / value-night.xml ??? skin_ttf_* ?????????????????????
                return Typeface.createFromAsset(context?.resources!!.assets, typefacePath)
            }
            return Typeface.createFromAsset(skinResources?.assets, typefacePath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Typeface.DEFAULT
    }

    private fun getTypefaceString(
        resources: Resources = context?.resources!!,
        skinTypeFaceId: Int
    ): String {
        if (skinTypeFaceId == 0) {
            throw Resources.NotFoundException("?????? themes ?????? strings ?????????????????????????????????")
        }
        if (isDefaultSkin) {
            return context?.resources!!.getString(skinTypeFaceId)
        }
        val resId = getIdentifierFromRes(resources, skinTypeFaceId)
        if (resId == 0) {
            return context?.resources!!.getString(skinTypeFaceId)
        }
        return skinResources?.getString(resId)!!
    }

    /**
     * ????????????
     * @param resources ??????????????? Resources
     * @param pkgName ?????????????????? package
     * @see com.devmeng.skinlib.skin.SkinManager.loadSkin
     */
    fun applySkinPackage(resources: Resources, pkgName: String) {
        skinResources = resources
        skinPkgName = pkgName
        isDefaultSkin = pkgName.isEmpty()
    }

    /**
     * ???????????????
     * @param resId ??????????????????????????????????????? id ???: @drawable/icon ????????? id: Int
     */
    @SuppressLint("DiscouragedApi")
    private fun getIdentifierFromRes(resources: Resources = context?.resources!!, resId: Int): Int {
        if (isDefaultSkin) {
            return resId
        }
        //??????: @drawable/icon -> drawable
//        val resName = context?.resources!!.getResourceName(resId)
        val typeName = resources.getResourceTypeName(resId)
        //??????: @drawable/icon -> icon
        val entryName = resources.getResourceEntryName(resId)
//        Log.d("resName -> $resName")
        Log.d("typeName -> $typeName")
        Log.d("entryName -> $entryName")
        val defaultIdentifier =
            context?.resources!!.getIdentifier(entryName, typeName, context?.packageName)
        Log.d("default identifier -> $defaultIdentifier")

//        Log.d("skinPkgName -> $skinPkgName")
        val skinIdentifier = skinResources?.getIdentifier(entryName, typeName, skinPkgName)
        Log.d("skin identifier -> $skinIdentifier")
        return skinIdentifier!!
    }

    fun restore() {
        skinResources = null
        skinPkgName = EMPTY
        isDefaultSkin = true
    }

}