package com.example.lsposedmodule; // 改成你的实际包名

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainModule implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        // 只作用于美团众包
        if (!"com.sankuai.meituan.dispatch.crowdsource".equals(lpparam.packageName)) return;

        // 强制头盔协议为“未签署”
        XposedHelpers.findAndHookMethod(
            "com.meituan.banma.lightning.utils.d",
            lpparam.classLoader,
            "b",
            boolean.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    param.args[0] = true; // 强制传入 true（未签署）
                }
            }
        );
    }
}