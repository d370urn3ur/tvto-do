package the.autarch.tvto_do;

import android.app.Application;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import the.autarch.tvto_do.dagger.ApplicationModule;
import the.autarch.tvto_do.model.Model;

public class TVTDApplication extends Application {

    ObjectGraph _applicationGraph;

    private static Model _model;

	@Override
	public void onCreate() {
		super.onCreate();

        _model = new Model();
        _model.onCreate(this);

        // Setup Dagger objectGraph
        _applicationGraph = ObjectGraph.create(getModules().toArray());
	}

    public static Model model() {
        return _model;
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
