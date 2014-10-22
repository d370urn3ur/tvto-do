package the.autarch.tvto_do.rx;

import android.support.v7.widget.SearchView;
import android.view.MenuItem;

import rx.Observable;

/**
 * Created by jpierce on 10/21/2014.
 */
public class TVTDViewObservable {

    public static <T extends SearchView> Observable<T> searchText(final T input) {
        return searchText(input, false);
    }

    public static <T extends SearchView> Observable<T> searchText(final T input, final boolean emitInitialValue) {
        return Observable.create(new OperatorSearchViewInput<T>(input, emitInitialValue));
    }

    public static Observable<Boolean> collapsed(final MenuItem input, final boolean initallyCollapsed) {
        return Observable.create(new OperatorSearchViewCollapse(input, initallyCollapsed));
    }
}
