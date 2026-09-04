package org.devio.rn.splashscreen;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.ref.WeakReference;

/**
 * SplashScreen
 * 启动屏
 * from：http://www.devio.org
 * Author:CrazyCodeBoy
 * GitHub:https://github.com/crazycodeboy
 * Email:crazycodeboy@gmail.com
 */
public class SplashScreen {
    private static Dialog mSplashDialog;
    private static WeakReference<Activity> mActivity;

    /**
     * 打开启动屏
     */
    public static void show(final Activity activity, final int themeResId) {
        if (activity == null) return;
        mActivity = new WeakReference<Activity>(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!activity.isFinishing()) {
                    mSplashDialog = new Dialog(activity, themeResId);
                    mSplashDialog.setContentView(R.layout.launch_screen);
                    mSplashDialog.setCancelable(false);
                    syncSystemUi(activity, mSplashDialog);
                    applyEdgeToEdgeConfig(mSplashDialog);

                    if (!mSplashDialog.isShowing()) {
                        mSplashDialog.show();
                        ViewCompat.requestApplyInsets(mSplashDialog.getWindow().getDecorView());
                    }
                }
            }
        });
    }

    /**
     * A Dialog owns a separate Window and does not inherit the Activity's
     * system UI layout flags. Keep both windows in the same coordinate system
     * so a shared launch_screen layout does not move when the Dialog appears.
     */
    private static void syncSystemUi(Activity activity, Dialog dialog) {
        Window activityWindow = activity.getWindow();
        Window splashWindow = dialog.getWindow();
        if (activityWindow == null || splashWindow == null) return;

        splashWindow.getDecorView().setSystemUiVisibility(
                activityWindow.getDecorView().getSystemUiVisibility());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            splashWindow.setStatusBarColor(activityWindow.getStatusBarColor());
            splashWindow.setNavigationBarColor(activityWindow.getNavigationBarColor());
        }
    }

    /**
     * The Dialog has its own Window, so its edge-to-edge mode must be explicit. Apps that do not
     * define splashScreenEdgeToEdge keep the legacy non-edge-to-edge behavior.
     */
    private static void applyEdgeToEdgeConfig(Dialog dialog) {
        Window window = dialog.getWindow();
        if (window == null) return;

        TypedValue value = new TypedValue();
        boolean edgeToEdge = dialog.getContext().getTheme().resolveAttribute(
                R.attr.splashScreenEdgeToEdge, value, true) && value.data != 0;
        if (!edgeToEdge) return;

        WindowCompat.setDecorFitsSystemWindows(window, false);

        View content = dialog.findViewById(android.R.id.content);
        View splashView = content;
        if (content instanceof ViewGroup && ((ViewGroup) content).getChildCount() > 0) {
            splashView = ((ViewGroup) content).getChildAt(0);
        }
        if (splashView == null) return;

        final View target = splashView;
        target.setFitsSystemWindows(false);
        final int left = target.getPaddingLeft();
        final int top = target.getPaddingTop();
        final int right = target.getPaddingRight();
        final int bottom = target.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(target, (view, insets) -> {
            int navigationBarBottom = insets.getInsets(
                    WindowInsetsCompat.Type.navigationBars()).bottom;
            view.setPadding(left, top, right, bottom + navigationBarBottom);
            return insets;
        });
    }

    /**
     * 打开启动屏
     */
    public static void show(final Activity activity, final boolean fullScreen) {
        int resourceId = fullScreen ? R.style.SplashScreen_Fullscreen : R.style.SplashScreen_SplashTheme;

        show(activity, resourceId);
    }

    /**
     * 打开启动屏
     */
    public static void show(final Activity activity) {
        show(activity, false);
    }

    /**
     * 关闭启动屏
     */
    public static void hide(Activity activity) {
        if (activity == null) {
            if (mActivity == null) {
                return;
            }
            activity = mActivity.get();
        }

        if (activity == null) return;

        final Activity _activity = activity;

        _activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSplashDialog != null && mSplashDialog.isShowing()) {
                    boolean isDestroyed = false;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        isDestroyed = _activity.isDestroyed();
                    }

                    if (!_activity.isFinishing() && !isDestroyed) {
                        mSplashDialog.dismiss();
                    }
                    mSplashDialog = null;
                }
            }
        });
    }
}
