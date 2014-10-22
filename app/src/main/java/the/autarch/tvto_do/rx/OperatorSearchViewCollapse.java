package the.autarch.tvto_do.rx;

import android.support.v4.view.MenuItemCompat;
import android.view.MenuItem;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.Assertions;
import rx.android.subscriptions.AndroidSubscriptions;
import rx.functions.Action0;

/**
 * Created by jpierce on 10/22/2014.
 */
public class OperatorSearchViewCollapse implements Observable.OnSubscribe<Boolean> {

    private final MenuItem input;
    private final boolean initiallyCollapsed;

    public OperatorSearchViewCollapse(final MenuItem input, final boolean initiallyCollapsed) {
        this.input = input;
        this.initiallyCollapsed = initiallyCollapsed;
    }

    @Override
    public void call(final Subscriber<? super Boolean> observer) {
        Assertions.assertUiThread();
        final MenuItemCompat.OnActionExpandListener listener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                observer.onNext(Boolean.FALSE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                observer.onNext(Boolean.TRUE);
                return true;
            }
        };

        final Subscription subscription = AndroidSubscriptions.unsubscribeInUiThread(new Action0() {
            @Override
            public void call() {
                MenuItemCompat.setOnActionExpandListener(input, null);
            }
        });

        if (initiallyCollapsed) {
            MenuItemCompat.collapseActionView(input);
        } else {
            MenuItemCompat.expandActionView(input);
        }

        observer.onNext(initiallyCollapsed);

        MenuItemCompat.setOnActionExpandListener(input, listener);
        observer.add(subscription);
    }
}
