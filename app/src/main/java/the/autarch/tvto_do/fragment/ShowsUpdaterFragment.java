package the.autarch.tvto_do.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import the.autarch.tvto_do.model.ExtendedInfo;
import the.autarch.tvto_do.model.Show;
import the.autarch.tvto_do.network.ApiManager;

/**
 * Created by jpierce on 11/2/2014.
 */
public class ShowsUpdaterFragment extends BaseInjectableFragment {

    @Inject Database _database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();

        checkShowsAndUpdate();
    }

    private void queryShows() {
        Query showsUpdateQuery = _database.getView("shows-update").createQuery();
        try {
            QueryEnumerator result = showsUpdateQuery.run();
            updateResults(result);
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Map<String, Object> info = (Map<String, Object>)row.getValue();
                ApiManager.getEx
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private void updateResults(final QueryEnumerator results) {
        Iterable<QueryRow> iterable = new Iterable<QueryRow>() {
            @Override
            public Iterator<QueryRow> iterator() {
                return results;
            }
        };
        Subscription subscription = AndroidObservable.bindFragment(this, Observable.from(iterable)
                .map(new Func1<QueryRow, Map<String, Object>>() {
                    @Override
                    public Map<String, Object> call(QueryRow queryRow) {
                        return (Map<String, Object>)queryRow.getValue();
                    }
                })
                .filter(new Func1<Map<String, Object>, Boolean>() {
                    @Override
                    public Boolean call(Map<String, Object> stringObjectMap) {
                        // TODO: check last updated
                        return true;
                    }
                })
                .flatMap(new Func1<Map<String, Object>, Observable<ExtendedInfo>>() {
                    @Override
                    public Observable<ExtendedInfo> call(Map<String, Object> stringObjectMap) {
                        String showId = (String)stringObjectMap.get(Show.KEY_TVRAGE_ID);
                        return ApiManager.getExtendedInfo(showId);
                    }
                }))
                .subscribe(new Action1<ExtendedInfo>() {
                    @Override
                    public void call(ExtendedInfo extendedInfo) {
                        _database.
                    }
                });
    }
}
