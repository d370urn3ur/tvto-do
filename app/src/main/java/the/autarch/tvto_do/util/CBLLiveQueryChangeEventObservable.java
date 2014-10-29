package the.autarch.tvto_do.util;

import com.couchbase.lite.LiveQuery;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.Assertions;
import rx.android.subscriptions.AndroidSubscriptions;
import rx.functions.Action0;

/**
 * Created by joshua.pierce on 29/10/14.
 */
public class CBLLiveQueryChangeEventObservable implements Observable.OnSubscribe<LiveQuery.ChangeEvent> {

    private LiveQuery _liveQuery;

    public CBLLiveQueryChangeEventObservable(LiveQuery query) {
        _liveQuery = query;
    }

    @Override
    public void call(final Subscriber<? super LiveQuery.ChangeEvent> observer) {

        Assertions.assertUiThread();

        final LiveQuery.ChangeListener changeListener = new LiveQuery.ChangeListener() {
            @Override
            public void changed(LiveQuery.ChangeEvent changeEvent) {
                observer.onNext(changeEvent);
            }
        };

        final Subscription subscription = AndroidSubscriptions.unsubscribeInUiThread(new Action0() {
            @Override
            public void call() {
                _liveQuery.removeChangeListener(changeListener);
            }
        });

        _liveQuery.addChangeListener(changeListener);
        observer.add(subscription);
    }
}
