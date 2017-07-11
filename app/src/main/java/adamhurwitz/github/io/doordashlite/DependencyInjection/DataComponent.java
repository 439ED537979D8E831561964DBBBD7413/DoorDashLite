package adamhurwitz.github.io.doordashlite.DependencyInjection;

import javax.inject.Singleton;

import adamhurwitz.github.io.doordashlite.DoorDashLiteApplication;
import adamhurwitz.github.io.doordashlite.MainViewModel;
import adamhurwitz.github.io.doordashlite.Network.Repository;
import dagger.Component;

/**
 * Created by ahurwitz on 7/9/17.
 */

@Singleton
@Component(modules = {DoorDashModule.class})
public interface DataComponent {

    void inject(DoorDashLiteApplication doorDashLiteApplication);

    void inject(Repository repository);

    void inject(MainViewModel mainViewModel);

}
