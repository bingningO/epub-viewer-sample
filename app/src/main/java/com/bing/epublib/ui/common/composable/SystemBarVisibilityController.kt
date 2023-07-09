package com.bing.epublib.ui.common.composable

import android.app.Activity
import android.content.Context
import android.os.Build
import android.widget.FrameLayout
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

/**
 * Class that standardizes the process of showing/hiding the SystemBar on each screen
 *
 * The lifetime of this instance should be the same as the argument Activity, or shorter. Otherwise,
 * it will cause Activity leaks.
 *
 * WindowInsetsController and WindowInsetsControllerCompat are not intentionally used due to the
 * following problems
 *
 * WindowInsetsController
 * - On certain devices (OPPO Reno5 A Android 11, Android One S8 Android 11, Pixel 3 Android 11,
 * Motorola Razr Android 11) systemBarsBehavior =
 * WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE does not work properly. I
 * think it might be a bug of Android 11). Even if WindowInsetsController is used in the future, it
 * would be better to use on Android 12 and above. systemUiVisibility itself has been deprecated
 * since Android 11, and it is recommended to use WindowInsetsController, but note that the above
 * problem exists.
 *
 * WindowInsetsControllerCompat
 * - On certain devices (Zenfone 3 Android 8, Lenovo IdeaPad Duet Chromebook), some views of
 * continuous playback and title recommendation cannot be displayed. (probably because via Compat,
 * systemUiVisibility would be changed many times in a short period of time)
 *
 * 各画面でのSystemBarの表示/非表示の処理を共通化するクラス
 *
 * このインスタンスの寿命は引数のActivityと同じ、もしくは短くする事 そうしないと、Activityのリークを引き起こす
 *
 * WindowInsetsControllerとWindowInsetsControllerCompatは下記の問題があるため、意図して使っていない
 *
 * WindowInsetsController
 * - 特定の端末（OPPO Reno5 A Android 11, Android One S8 Android 11, Pixel 3 Android 11, Motorola Razr
 * Android 11)で、 Activity生成・表示直後のsystemBarsBehavior =
 * WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPEが効かない
 * 2秒ほど遅延させてsetすると効く(恐らく、Android 11のバグなのでは？という気がする) 将来的にWindowInsetsControllerを使用するとしても、Android
 * 12以上に絞った方が良いと思われる systemUiVisibility自体はAndroid
 * 11からdeprecatedでWindowInsetsControllerを使う事が推奨とされているが、上記の問題がある事に注意
 *
 * WindowInsetsControllerCompat
 * - 特定の端末（Zenfone 3 Android 8, Lenovo IdeaPad Duet Chromebook）で、連続再生・タイトルレコメンドのいくつかのViewが表示出来なくなる
 * (恐らく、Compat経由では短時間にsystemUiVisibilityを何度も変更することになるから、だと思われる)
 *
 * original:
 * https://github.com/u-next/Android/blob/f8fbaa36905ea59df742c9fb019438c0153e2211/U-NEXT/app/src/main/java/jp/unext/mobile/player/ui/common/SystemBarVisibilityController.kt
 */
@Immutable
@ActivityScoped
class SystemBarVisibilityController @Inject constructor(
    @ActivityContext private val context: Context,
) {
    private val activity: Activity = context as Activity

    // private val insetsController = WindowInsetsControllerCompat(activity.window, rootView)
    fun setVisibility(visible: Boolean) {
        if (activity.isInMultiWindow) return

        var newVisibility =
            (FrameLayout.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    FrameLayout.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    FrameLayout.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        if (!visible) {
            newVisibility =
                newVisibility or
                        (FrameLayout.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                                FrameLayout.SYSTEM_UI_FLAG_FULLSCREEN or
                                FrameLayout.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
        activity.window.decorView.systemUiVisibility = newVisibility

        /*
        if (visible) {
            insetsController.run {
                show(WindowInsetsCompat.Type.systemBars())
            }
        } else {
            insetsController.run {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
        */
    }
}

val Activity.isInMultiWindow: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode