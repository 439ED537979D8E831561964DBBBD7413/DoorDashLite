package adamhurwitz.github.io.doordashlite;

import android.app.Application;

import adamhurwitz.github.io.doordashlite.DependencyInjection.DoorDashModule;
import adamhurwitz.github.io.doordashlite.Network.Repository;
import adamhurwitz.github.io.doordashlite.Network.Service;

/**
 * Created by ahurwitz on 7/10/17.
 */

public class TestDataModule extends DoorDashModule {
    public TestDataModule(Application app) {
        super(app);
    }

    @Override
    public Repository provideRepository(Service service) {
        return new RepositoryMock(service);
    }
}
