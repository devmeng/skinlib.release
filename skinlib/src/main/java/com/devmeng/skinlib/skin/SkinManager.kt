package com.devmeng.skinlib.skin

import android.app.Application
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import com.devmeng.skinlib.skin.entity.Skin
import com.devmeng.skinlib.skin.utils.Log
import com.devmeng.skinlib.skin.utils.SkinPreference
import com.devmeng.skinlib.skin.utils.SkinResources
import com.devmeng.skinlib.skin.utils.md5ForFile
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Created by devmeng
 * Version : 1
 * Description :
 * 皮肤管理类:
 * 1.通过反射 AssetManager 将皮肤的 asset 资源文件路径添加到 mApkAssets[] 该集合中
 * 并与新建的 Resources 类做关联（ Resources constructor 已经弃用建议后期修改为《/*注释内容*/》并处理有关 Bug）
 * @see AssetManager
 * @see Resources
 * 2.通过 PackageManager 获取皮肤包所在的 apk 的包名，以获取皮肤包内的资源
 * @see PackageManager
 * 3.通知观察者
 *
 */
class SkinManager private constructor() : Observable() {

    private lateinit var application: Application

    companion object {

        @JvmStatic
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            SkinManager()
        }

        @JvmStatic
        fun init(
            application: Application,
            activityLifecycleCallbacks: Application.ActivityLifecycleCallbacks
            = SkinActivityLifecycle(),
            isApplicationTypeface: Boolean = false,
            isDebug: Boolean = true
        ): SkinManager {
            application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
            SkinPreference.init(application.applicationContext)
            SkinResources.init(application.applicationContext)
            instance.application = application
            IS_APPLICATION_TYPEFACE = isApplicationTypeface
            BUILD_TYPE = isDebug
            return instance
        }
    }

    /**
     * 加载皮肤包
     * @param skinPath 如果皮肤包路径不为空则加载皮肤，反之还原皮肤
     * 1.使用 SkinResources 通过自定义的 Resources 和 AssetManager
     * 加载 PackageManager 获取的外部 apk 皮肤包
     * @see SkinResources.applySkinPackage
     * 2.使用 SkinPreference 储存皮肤包路径
     * @see SkinPreference
     * 3.通知观察者
     */
    fun loadSkin(skinPath: String = EMPTY) {
        if (skinPath.isNotEmpty()) {
            try {
                Log.d("skinPath -> $skinPath")
                val assetManager = AssetManager::class.java.newInstance()
                /*val method =
                    assetManager.javaClass.getMethod(
                        "addAssetPathInternal",
                        String::class.java,
                        Boolean::class.java,
                        Boolean::class.java
                    )*/
                val method = assetManager.javaClass.getMethod(
                    "addAssetPath",
                    String::class.java
                )
//                method.invoke(assetManager, skinPath, false, false)
                method.invoke(assetManager, skinPath)
                val resources = application.resources
                //通过反射获取 ResourceImpl 并将 mAssets 等变量赋值

                /*
                val resourcesImpl =
                    Resources::class.java.classLoader!!
                        .loadClass("android.content.res.ResourcesImpl")

                val assets = resourcesImpl.getDeclaredField("mAssets")
                val metrics = resourcesImpl.getDeclaredField("mMetrics")
                val config = resourcesImpl.getDeclaredField("mConfiguration")
                metrics.isAccessible = true
                config.isAccessible = true

                assets.set(resourcesImpl, assetManager)
                metrics.set(resourcesImpl, resources.displayMetrics)
                config.set(resourcesImpl, resources.configuration)

                val mResourcesImpl = Resources::class.java.getField("mResourcesImpl")

                mResourcesImpl.set(resources, resourcesImpl)
*/
                val skinResources =
                    Resources(assetManager, resources.displayMetrics, resources.configuration)

                //获取 skinPath 所在的 Apk 包名
                val pkgManager = application.packageManager
                val packageArchiveInfo =
                    pkgManager.getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES)
                if (packageArchiveInfo != null) {
                    val pkgName = packageArchiveInfo.packageName
                    //存储并应用皮肤包资源，此时还没有进行皮肤的切换
                    SkinPreference.instance.setSkinPath(skinPath)
                    SkinResources.instance.applySkinPackage(skinResources, pkgName)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            //还原皮肤
            SkinPreference.instance.setSkinPath()
            SkinResources.instance.reset()
        }
        //通知观察者并在观察者的 update 方法中进行皮肤的应用
        setChanged()
        notifyObservers()
    }

    /**
     * 加载网络皮肤
     * @param skin 皮肤实体类
     */
    fun loadSkin(skin: Skin, md5: String = EMPTY) {
        val skins = File(application.applicationContext.filesDir, "skins")
        if (skins.exists().and(skins.isFile)) {
            skins.delete()
        }
        skins.mkdir()
        val skinFile = skin.getSkinFile(skins)
        var cmd5 = md5
        if (skin.md5.isNotEmpty().and(cmd5.isEmpty())) {
            cmd5 = md5ForFile(skinFile)!!
        }
        if (skinFile.exists()) {
            //有就应用
            if (skin.md5 == cmd5) {
                Log.d("skin 文件存在")
                loadSkin(skin.path)
                Log.d("load skin")
            } else {
                throw IllegalArgumentException("皮肤包已存在，但 MD5 值不匹配，请检查 MD5 计算规则是否相同")
            }
            return
        }
        val tempFile = File(skinFile.parentFile, "${skin.name}.temp")

        //下载皮肤包
        val request = Request.Builder().url(skin.skinUrl).build()
        OkHttpClient.Builder().build().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                val bs = response.body.byteStream()
                val fileOut = FileOutputStream(tempFile)
                try {
                    var length: Int
                    val bytes = ByteArray(1024)
                    while (bs.read(bytes).also { length = it } != -1) {
                        fileOut.write(bytes, 0, length)
                    }
                    if (skin.md5 == cmd5) {
                        tempFile.renameTo(skinFile)
                        Log.d("md5 相同")
                    } else {
                        throw IllegalArgumentException("MD5 值不匹配，请检查 MD5 计算规则是否相同")
                    }
                    loadSkin(skin.path)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    fileOut.close()
                    bs.close()
                }
            }
        })
    }

}