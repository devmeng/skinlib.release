# skinlib.release  API 23 [![](https://jitpack.io/v/devmeng/skinlib.release.svg)](https://jitpack.io/#devmeng/skinlib.release) 

The repository which supports to change application's views skins by dynamic.

## Introduce

The project had customized **Application.ActivityLifecycleCallbacks** for listening activity lifecycle, and let the **LayoutInflater** hold the reference to factory which custom for application's view surfaces by dynamic changing. 

Then, the point is project having overridden the function of **LayoutInflater.Factory2** for creating the application's views with new skins. And when application loads new skin for its views, manager of skins always notifies skin factory to apply skin for views.

How to attach the skin resources when we apply skins? The project had instantiated **Resources** of skins in this version. After that, project will use skin resources entity for getting the resources' id by its name in the skins packages that what you had packaged, so you have to keep the resources' name between application and skin packages to same.

## Config Environment

***Notice: You dont need to import this repository when you had imported baselib repository! Cause baselib has this function!**

First step, you should config the jitpack repository under your project.

```groovy
repositories {
    maven { url "https://jitpack.io" }
}
```

And then configs the dependence under your project. **(latest.release = 1.0.4)**

```groovy
dependencies {
    implementation 'com.github.devmeng:skinlib.release:latest.release'
}
```

## Use in basic

### Initialize

First, you should initialize the skin function in your **Application** which extends **android.app.Application**

```kotlin
SkinManager.init(
            application: Application,
            activities: MutableList<String> = mutableListof(),
            activityLifecycleCallbacks: Application.ActivityLifecycleCallbacks
            = SkinActivityLifecycle(activities),
            isApplicationTypeface: Boolean = false,
            isDebug: Boolean = true
        )
```

Let's check the arguments for **SkinManager**.

| Name                       | Introduce                                                    | Default               | Version |
| -------------------------- | ------------------------------------------------------------ | --------------------- | ------- |
| application                | Attach to application                                        |                       |         |
| activities                 | Which Activities you wanna change skins. **(add activity shortClassName or simpleName)** | mutableListof()       | ＞1.0.4 |
| activityLifecycleCallbacks | Your custom entity for listening activitylifecycle and callback | SkinActivityLifecycle |         |
| isApplicationTypeface      | Switch of application typeface                               | false                 |         |
| isDebug                    | Switch of debug                                              | true                  |         |

So, after checking, you can use it in application easily:

```kotlin
SkinManager.init(this)
```

Before load skin, you should make a skin package for your application.

### How to make a Skin Package

**Step.1.** Creating a new module in your project or you can make a project for making skin package specifically.

**Step.2.** Setting **①the values for views** which need to change skin, and **keep these values' name with skin package values' name to same**

**Step.3.** Build an apk for your skin module, and then upload it in your application storage where what you want.

**Step.4.** After that, you can call a function called **"loadSkin"** in SkinManager for loading skin.

**Annotation:** ①project had configured the attribute set for skin in need.

```kotlin
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
        "skinTypeface"
)
```

So, that's all skin attribute where you can change.

### Load Skin

#### The first way

Loading skin from network link:

```kotlin
SkinManager.instance.loadSkin(skin: Skin, md5: String = EMPTY)
```

Let's check Skin entity:

```kotlin
data class Skin(val md5: String, val name: String, val skinUrl: String)
```

| Name    | Introduce                                                    |
| ------- | ------------------------------------------------------------ |
| md5     | key for skin package file (so you need to keep the rule of calculate md5 between front-end and back-end to the same). **Notice: You can use EMPTY if you dont need md5.** |
| name    | custom skin package file name                                |
| skinUrl | network link for skin package                                |

#### The second way

Loading skin from local storage

```kotlin
SkinManager.instance.loadSkin(skinPath: String = EMPTY)
```

### Restore Skin

Restoring to default skin:

```kotlin
SkinManager.instance.loadSkin()
```

## Extension

### Skinning for Custom Widget

**Step.1.** If you wanna change the widget that you custom, let your widget implements **com.devmeng.skinlib.skin.SkinWidgetSupport** and overrides variant and function what in it.

```kotlin
/**
 * your custom widget's custom attribute set
 */
val attrsList: MutableList<String>

/**
 * changing skin callback skin resources list
 * <1.0.3
 * applySkin(pairList: List<SkinPair>)
 * ≥1.0.3 
 */
fun applySkin(skinResources: SkinResources, pairList: List<SkinPair>)
```

Let's check **SkinPair** entity.

| Name     | Introduce                                   |
| -------- | ------------------------------------------- |
| attrName | Your custom widget attribute name for skins |
| resId    | Resource id for skins in SkinResources      |

**Step.2.** When you need to apply resources of skin, you should judge every attribute with what you config in **attrsList**. **Then using *SkinResources.instance* to get resource about your attribute type to apply resources.**

**Step.3.** This step is important for changing custom widget skins. You should get resources from **SkinResources** when you change them by scope of switch. (After current version, project will import a plugin for creating them to salvage your time)

By the way, remember to refresh the state of your custom widget!

## Best wishes

Updating from time to time, please keep following. ;)
