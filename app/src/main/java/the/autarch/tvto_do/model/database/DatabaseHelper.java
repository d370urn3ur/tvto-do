package the.autarch.tvto_do.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import the.autarch.tvto_do.R;

/**
 * Created by jpierce on 9/13/14.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DBNAME = "tv-todo.db";
    private static final int DBVERSION = 5;

    public DatabaseHelper(Context context) {
        this(context, R.raw.ormlite_config);
    }

    public DatabaseHelper(Context context, int configFileId) {
        super(context, DBNAME, null, DBVERSION, configFileId);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Show.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

        try {

            TableUtils.dropTable(connectionSource, Show.class, true);

            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource);

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public ShowDaoImpl getShowDao() throws SQLException {
        return getDao(Show.class);
    }
}
