package the.autarch.tvto_do.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import the.autarch.tvto_do.activity.BaseEventActivity;
import the.autarch.tvto_do.activity.ShowsListActivity;
import the.autarch.tvto_do.fragment.ShowsListFragment;
import the.autarch.tvto_do.fragment.ShowsSearchFragment;

/**
 * Created by joshua.pierce on 27/10/14.
 */
@Module(
        injects = {
                ShowsListActivity.class,
                ShowsListFragment.class,
                ShowsSearchFragment.class
        },
        addsTo = ApplicationModule.class,
        library = true
)
public class ActivityModule {

    private final BaseEventActivity activity;

    public ActivityModule(BaseEventActivity activity) {
        this.activity = activity;
    }

    /**
     * Allow the activity context to be injected but require that it be annotated with
     * {@link ForActivity @ForActivity} to explicitly differentiate it from application context.
     */
    @Provides @Singleton @ForActivity
    Context provideActivityContext() {
        return activity;
    }

}
