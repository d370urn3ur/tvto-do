package the.autarch.tvto_do.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import de.greenrobot.event.EventBus;
import the.autarch.tvto_do.TVTDApplication;
import the.autarch.tvto_do.dagger.ActivityModule;

/**
 * Created by jpierce on 9/10/14.
 */
public class BaseEventActivity extends ActionBarActivity {

    private ObjectGraph _activityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TVTDApplication app = (TVTDApplication)getApplication();
        _activityGraph = app.getApplicationGraph().plus(getModules().toArray());

        _activityGraph.inject(this);
    }

    @Override
    protected void onDestroy() {

        _activityGraph = null;

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * A list of modules to use for the individual activity graph. Subclasses can override this
     * method to provide additional modules provided they call and include the modules returned by
     * calling {@code super.getModules()}.
     */
    protected List<Object> getModules() {
        return Arrays.<Object>asList(new ActivityModule(this));
    }

    public void inject(Object object) {
        _activityGraph.inject(object);
    }
}
