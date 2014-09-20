package the.autarch.tvto_do;

import android.app.Application;

import the.autarch.tvto_do.model.FileManager;
import the.autarch.tvto_do.network.NetworkManager;

public class TVTDApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();

		FileManager.initialize(this);
        NetworkManager.initialize(this);
	}
}
