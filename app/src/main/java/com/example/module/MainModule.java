package com.example.module;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

@SuppressLint({"PrivateApi", "BlockedPrivateApi"})
public class MainModule extends XposedModule {

    @Override
    public void onSystemServerStarting(@NonNull SystemServerStartingParam param) {
        // 系统服务启动时调用，本项目不需要，留空
    }

    @Override
    public void onPackageLoaded(@NonNull PackageLoadedParam param) {
        // 此回调中 classloader 尚不可用，不能在此 Hook 具体类
    }

    @Override
    public void onPackageReady(@NonNull PackageReadyParam param) {
        // 类加载器已就绪，可以在此加载并 Hook 目标类
        if (!param.getPackageName().equals("com.sankuai.meituan.dispatch.crowdsource")) {
            return;
        }

        try {
            var classLoader = param.getClassLoader();
            // 加载目标类
            var clazz = classLoader.loadClass("com.meituan.banma.lightning.utils.d");
            // 获取方法 b(boolean)，返回 void
            var method = clazz.getDeclaredMethod("b", boolean.class);
            // 注册 Hook
            hook(method).intercept(new HelmetHooker());
        } catch (Throwable t) {
            log(android.util.Log.ERROR, "MainModule", "Hook failed", t);
        }
    }

    /**
     * 拦截器：强制将 d.b(boolean) 的参数改为 true（未签署）
     */
    private static class HelmetHooker implements XposedInterface.Hooker {
        @Override
        public Object intercept(@NonNull XposedInterface.Chain chain) throws Throwable {
            // 获取原始参数
            Object[] args = chain.getArgs().toArray();
            if (args.length > 0 && args[0] instanceof Boolean) {
                // 强制改为 true（未签署）
                args[0] = true;
            }
            // 调用原方法（使用修改后的参数）
            return chain.proceed(args);
        }
    }
}