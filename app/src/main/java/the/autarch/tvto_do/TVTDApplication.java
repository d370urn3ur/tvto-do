package the.autarch.tvto_do;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import the.autarch.tvto_do.dagger.ApplicationModule;

public class TVTDApplication extends Application {

    ObjectGraph _applicationGraph;

	@Override
	public void onCreate() {
		super.onCreate();

        // Setup Dagger objectGraph
        _applicationGraph = ObjectGraph.create(getModules().toArray());
	}

    private List<Object> getModules() {
        return Arrays.<Object>asList(
                new ApplicationModule(this)
        );
    }

    public ObjectGraph getApplicationGraph() {
        return _applicationGraph;
    }
}
