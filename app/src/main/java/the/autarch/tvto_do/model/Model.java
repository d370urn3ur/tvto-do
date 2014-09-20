package the.autarch.tvto_do.model;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;

import de.greenrobot.event.EventBus;

/**
 * Created by jpierce on 9/20/14.
 */
public class Model {

    public enum ModelState {
        INITIALIZING,
        INITIALIZED;
    }

    private ModelState _state;
    private Context _appContext;
    private DatabaseHelper _dbHelper;

    public void onCreate(Context context) {
        _appContext = context.getApplicationContext();
        _state = ModelState.INITIALIZING;
        new InitDaoTask().execute();
    }

    public boolean isAvailable() {
        return _state == ModelState.INITIALIZED;
    }

    public ShowDao getShowDao() {
        try {
            return _dbHelper.getShowDao();
        } catch(SQLException e) {
            return null;
        }
    }

    class InitDaoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            _dbHelper = new DatabaseHelper(_appContext);

            try {
                // forward init all DAOs
                _dbHelper.getShowDao();

            } catch(SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            _state = ModelState.INITIALIZED;
            EventBus.getDefault().post(new DatabaseInitializedEvent());
        }
    }

    public class DatabaseInitializedEvent {}

}
