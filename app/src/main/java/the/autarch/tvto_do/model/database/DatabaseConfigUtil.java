package the.autarch.tvto_do.model.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

/**
 * Created by jpierce on 9/21/14.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[] {
            Show.class,
    };

    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_config.txt", classes);
    }
}
