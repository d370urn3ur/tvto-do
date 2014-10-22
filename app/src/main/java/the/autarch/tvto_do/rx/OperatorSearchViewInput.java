package the.autarch.tvto_do.rx;

import android.support.v7.widget.SearchView;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.Assertions;
import rx.android.subscriptions.AndroidSubscriptions;
import rx.functions.Action0;

/**
 * Created by jpierce on 10/21/2014.
 */
public class OperatorSearchViewInput<T extends SearchView> implements Observable.OnSubscribe<T> {

    private final T input;
    private final boolean emitInitialValue;

    public OperatorSearchViewInput(final T input, final boolean emitInitialValue) {
        this.input = input;
        this.emitInitialValue = emitInitialValue;
    }

    @Override
    public void call(final Subscriber<? super T> observer) {
        Assertions.assertUiThread();
        final SearchView.OnQueryTextListener watcher = new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                observer.onNext(input);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                observer.onNext(input);
                return true;
            }
        };

        final Subscription subscription = AndroidSubscriptions.unsubscribeInUiThread(new Action0() {
            @Override
            public void call() {
                input.setOnQueryTextListener(null);
            }
        });

        if (emitInitialValue) {
            observer.onNext(input);
        }

        input.setOnQueryTextListener(watcher);
        observer.add(subscription);
    }
}
