package com.devmeng.skinlib.skin

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.devmeng.skinlib.skin.utils.SkinThemeUtils
import com.devmeng.skinlib.utils.Log
import java.lang.reflect.Constructor
import java.util.*

/**
 * Created by devmeng
 * Version : 1
 * Description :
 * 皮肤工厂:
 * 1.使用 createViewFromTag 方法
 * 通过父类的 onCreateView 方法中获取到的 view 的名称与 viewPrefixList 中的前缀结合找出该控件
 * @see onCreateView
 * 通过 SkinAttribute.load 方法，获取并缓存 view 包含的更换皮肤所需的属性/属性的值
 * @see SkinAttribute.attributeList 更换皮肤所需的属性名称集合
 * @see SkinAttribute.load 用于缓存皮肤所需属性以及属性对应的值
 * @see createViewFromTag
 * 2.使用 createView 方法，以反射的方式获取 View 的构造函数，缓存并创建实例
 * @see createView
 * @see constructorSignature
 * 3.观察者更新状态 -> 切换皮肤
 * @see update
 * @see SkinAttribute.applySkin
 * @param activity
 * @param skinTypeface 对应皮肤包中的特定字体
 *
 */
class SkinLayoutFactory(val activity: Activity, val skinTypeface: Typeface?) :
    LayoutInflater.Factory2,
    Observer {

    private val viewConMap: HashMap<String, Constructor<out View>> = hashMapOf()

    private val viewPrefixList: MutableList<String> = mutableListOf(
        "android.widget.",
        "android.view.",
        "android.webkit.",
        /*"android.gesture.",
        "android.app.",
        "android.appwidget.",
        "android.inputmethodservice.",
        "android.opengl.",
        "android.media.tv.",*/
    )

    private val constructorSignature = arrayOf(
        Context::class.java, AttributeSet::class.java
    )

    private val skinAttribute = SkinAttribute(skinTypeface)

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View {
        val view = createViewFromTag(name, context, attrs) ?: return createView(
            name,
            context,
            attrs
        )!!
        skinAttribute.load(view, attrs)
        return view
    }

    private fun createViewFromTag(name: String, context: Context, attrs: AttributeSet): View? {
        Log.d("SkinLayoutFactory -> view name: $name")
        var view: View? = null
        if (-1 == name.indexOf(".")) {
            for (pack in viewPrefixList) {
                view = createView(pack + name, context, attrs)
                if (null != view) {
                    break
                }
            }
            return view
        }
        return createView(name, context, attrs)
    }

    private fun createView(name: String, context: Context, attrs: AttributeSet): View? {
        var constructor: Constructor<out View>? = viewConMap[name]
        if (null == constructor) {
            try {
                val aClass: Class<out View?> = context.classLoader.loadClass(name).asSubclass(
                    View::class.java
                )
                constructor = aClass.getConstructor(*constructorSignature)
                viewConMap[name] = constructor
            } catch (e: Exception) {
                return null
            }
        }
        return constructor!!.newInstance(context, attrs)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return null
    }

    override fun update(o: Observable?, arg: Any?) {
        SkinThemeUtils.updateStatusBarState(activity)
        SkinThemeUtils.updateNavigationBarState(activity)
        skinAttribute.skinTypeface?.apply {
            val typeface = SkinThemeUtils.getSkinTypeface(activity)
            skinAttribute.skinTypeface = typeface
        }
        //由 SkinManager 通知并应用皮肤，开始根据属性换肤
        skinAttribute.applySkin()
    }
}