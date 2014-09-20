package the.autarch.tvto_do;

import android.app.Application;

import the.autarch.tvto_do.model.FileManager;
import the.autarch.tvto_do.model.Model;
import the.autarch.tvto_do.network.NetworkManager;

public class TVTDApplication extends Application {

    private static Model _model;

	@Override
	public void onCreate() {
		super.onCreate();

		FileManager.initialize(this);
        NetworkManager.initialize(this);

        _model = new Model();
        _model.onCreate(this);
	}

    public static Model model() {
        return _model;
    }
}
