package adamhurwitz.github.io.doordashlite.dbflow;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by ahurwitz on 7/8/17.
 */

@Database(version = DoorDashDb.VERSION, name = DoorDashDb.NAME)
public class DoorDashDb extends BaseModel {
    public static final String NAME = "doorDashDb";
    public static final int VERSION = 1;
}
