package adamhurwitz.github.io.doordashlite;

import android.app.Application;
import android.support.annotation.VisibleForTesting;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import adamhurwitz.github.io.doordashlite.DependencyInjection.DaggerDataComponent;
import adamhurwitz.github.io.doordashlite.DependencyInjection.DataComponent;
import adamhurwitz.github.io.doordashlite.DependencyInjection.DoorDashModule;

/**
 * Created by ahurwitz on 7/8/17.
 */

public class DoorDashLiteApplication extends Application {

    private static DoorDashLiteApplication app;
    DataComponent dataComponent;

    public static DoorDashLiteApplication getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;

        initDataComponent();

        dataComponent.inject(this);

        FlowManager.init(new FlowConfig.Builder(this).build());

    }

    private void initDataComponent() {
        dataComponent = DaggerDataComponent.builder()
                .doorDashModule(new DoorDashModule(this))
                .build();
    }

    public DataComponent getDataComponent() {
        return dataComponent;
    }

    @VisibleForTesting
    public void setComponent(DataComponent component) {
        this.dataComponent = component;
    }

}
