package the.autarch.tvto_do;

import android.app.Application;

import the.autarch.tvto_do.model.Model;

public class TVTDApplication extends Application {

    private static Model _model;

	@Override
	public void onCreate() {
		super.onCreate();

        _model = new Model();
        _model.onCreate(this);
	}

    public static Model model() {
        return _model;
    }
}
