package the.autarch.tvto_do.model;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import de.greenrobot.event.EventBus;
import the.autarch.tvto_do.TVTDApplication;
import the.autarch.tvto_do.event.DatabaseInitializedEvent;
import the.autarch.tvto_do.event.UpdateExpiredExtendedInfoEvent;
import the.autarch.tvto_do.model.database.DatabaseHelper;
import the.autarch.tvto_do.model.database.Show;
import the.autarch.tvto_do.model.database.ShowDao;

/**
 * Created by jpierce on 9/20/14.
 */
public class Model {

    public enum ModelState {
        INITIALIZING,
        INITIALIZED
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
        if(_dbHelper == null) {
            return null;
        }

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

            new SearchExpiredExtendedInfoTask().execute();
        }
    }

    class SearchExpiredExtendedInfoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, -2);

            QueryBuilder<Show, Integer> qb = TVTDApplication.model().getShowDao().queryBuilder();
            PreparedQuery<Show> query = null;
            try {
                query = qb.where()
                            .lt(Show.ShowColumns.EXTENDED_INFO_LAST_UPDATE, c.getTime())
                            .and()
                            .eq(Show.ShowColumns.EXTENDED_INFO_STATUS, Show.ExtendedInfoStatus.EXTENDED_INFO_UNKNOWN)
                            .prepare();

                List<Show> shows = TVTDApplication.model().getShowDao().query(query);
                EventBus.getDefault().post(new UpdateExpiredExtendedInfoEvent(shows));

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
