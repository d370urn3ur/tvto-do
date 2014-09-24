package the.autarch.tvto_do.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import the.autarch.tvto_do.TVTDApplication;
import the.autarch.tvto_do.event.DatabaseInitializedEvent;
import the.autarch.tvto_do.event.ShowCreatedEvent;
import the.autarch.tvto_do.event.ShowDeletedEvent;
import the.autarch.tvto_do.event.ShowUpdatedEvent;
import the.autarch.tvto_do.model.database.Show;
import the.autarch.tvto_do.model.database.ShowDao;

/**
 * Created by jpierce on 9/20/14.
 */
public class ShowLoader extends AsyncTaskLoader<List<Show>> {

    private List<Show> _data;

    public ShowLoader(Context context) {
        super(context);
    }

    @Override
    public List<Show> loadInBackground() {

        List<Show> data = new ArrayList<Show>();
        try {
            ShowDao dao = TVTDApplication.model().getShowDao();

            if(dao == null) {
                return null;
            }

            QueryBuilder<Show, Integer> qb = dao.queryBuilder();
            qb.orderBy(Show.ShowColumns.DEFAULT_SORT_ORDER, Show.ShowColumns.DEFAULT_SORT_ASCENDING);
            PreparedQuery<Show> query = qb.prepare();
            data = dao.query(query);

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void deliverResult(List<Show> data) {

        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        List<Show> oldData = _data;
        _data = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {

        if (_data != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(_data);
        }

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (takeContentChanged() || _data == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (_data != null) {
            releaseResources(_data);
            _data = null;
        }
    }

    @Override
    public void onCanceled(List<Show> data) {
        // Attempt to cancel the current asynchronous load.
        super.onCanceled(data);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseResources(data);
    }

    private void releaseResources(List<Show> data) {
        // For a simple List, there is nothing to do. For something like a Cursor, we
        // would close it in this method. All resources associated with the Loader
        // should be released here.
    }

    public void onEventMainThread(ShowCreatedEvent event) {
        onContentChanged();
    }

    public void onEventMainThread(ShowUpdatedEvent event) {
        onContentChanged();
    }

    public void onEventMainThread(ShowDeletedEvent event) {
        onContentChanged();
    }

    public void onEventMainThread(DatabaseInitializedEvent event) {
        onContentChanged();
    }
}
