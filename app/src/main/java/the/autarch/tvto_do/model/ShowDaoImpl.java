package the.autarch.tvto_do.model;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;

import de.greenrobot.event.EventBus;

/**
 * Created by jpierce on 9/20/14.
 */
public class ShowDaoImpl extends BaseDaoImpl<Show, Integer> implements ShowDao {

    public ShowDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, Show.class);
    }

    @Override
    public void createInBackground(final Show show) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if(create(show) == 1) {
                        EventBus.getDefault().post(new ShowCreatedEvent());
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

    public class ShowCreatedEvent {}
    public class ShowUpdatedEvent {}
    public class ShowDeletedEvent {}

    public class SQLErrorEvent {

        private SQLException error;

        SQLErrorEvent(SQLException e) {
            error = e;
        }

        public SQLException getError() {
            return error;
        }
    }
}
