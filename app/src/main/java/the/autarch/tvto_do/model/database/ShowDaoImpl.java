package the.autarch.tvto_do.model.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;
import java.util.List;

import de.greenrobot.event.EventBus;
import the.autarch.tvto_do.event.SQLErrorEvent;
import the.autarch.tvto_do.event.ShowCreatedEvent;
import the.autarch.tvto_do.event.ShowDeletedEvent;
import the.autarch.tvto_do.event.ShowUpdatedEvent;
import the.autarch.tvto_do.model.FileManager;
import the.autarch.tvto_do.model.gson.ExtendedInfoGson;

/**
 * Created by jpierce on 9/20/14.
 */
public class ShowDaoImpl extends BaseDaoImpl<Show, Integer> implements ShowDao {

    public ShowDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Show.class);
    }

    public ShowDaoImpl(ConnectionSource connectionSource, DatabaseTableConfig<Show> tableConfig) throws SQLException {
        super(connectionSource, tableConfig);
    }

    @Override
    public void createInBackground(final Show show) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if(create(show) == 1) {
                        EventBus.getDefault().post(new ShowCreatedEvent(show));
                    } else {
                        EventBus.getDefault().post(new SQLErrorEvent(null));
                    }

                } catch(SQLException e) {
                    EventBus.getDefault().post(new SQLErrorEvent(e));
                }
            }
        }).start();
    }

    @Override
    public void updateInBackground(final Show show) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(update(show) == 1) {
                        EventBus.getDefault().post(new ShowUpdatedEvent());
                    } else {
                        EventBus.getDefault().post(new SQLErrorEvent(null));
                    }
                } catch(SQLException e) {
                    EventBus.getDefault().post(new SQLErrorEvent(e));
                }
            }
        }).start();
    }

    @Override
    public void updateExtendedInfoInBackground(final ExtendedInfoGson extendedInfoGson) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Show> shows = queryForEq(Show.ShowColumns.TVRAGE_ID, extendedInfoGson.tvRageId);
                    if(shows.size() > 0) {
                        Show show = shows.get(0);
                        show.updateWithExtendedInfo(extendedInfoGson);
                        if(update(show) == 1) {
                            EventBus.getDefault().post(new ShowUpdatedEvent());
                            return;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                EventBus.getDefault().post(new SQLErrorEvent(null));
            }
        }).start();
    }

    @Override
    public void deleteInBackground(final Show show) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    FileManager.getInstance().deleteBitmapForFilename(show.getPoster138filepath());

                    if(delete(show) == 1) {
                        EventBus.getDefault().post(new ShowDeletedEvent());
                    } else {
                        EventBus.getDefault().post(new SQLErrorEvent(null));
                    }
                } catch(SQLException e) {
                    EventBus.getDefault().post(new SQLErrorEvent(e));
                }
            }
        }).start();
    }
}
