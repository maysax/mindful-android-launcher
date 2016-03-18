package minium.co.launcher.app;

import org.androidannotations.annotations.EApplication;

import minium.co.core.BuildConfig;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.Tracer;

/**
 * Concrete implementation of {@link CoreApplication}
 * This class will implement specific behaviors that differs with core library
 *
 * Created by shahab on 3/17/16.
 */
@EApplication
public class App extends CoreApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Tracer.i("Version code: " + minium.co.launcher.BuildConfig.VERSION_CODE);
        Tracer.i("Version name: " + minium.co.launcher.BuildConfig.VERSION_NAME);
        Tracer.i("Build time: " + BuildConfig.BUILD_TIME);
        Tracer.i("Application Id: " + minium.co.launcher.BuildConfig.APPLICATION_ID);
        Tracer.i("Build type: " + minium.co.launcher.BuildConfig.BUILD_TYPE);
        Tracer.i("Git SHA: " + BuildConfig.GIT_SHA);
        Tracer.i("Debug? : " + minium.co.launcher.BuildConfig.DEBUG);
    }
}
