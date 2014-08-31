package the.autarch.tvto_do;

import the.autarch.tvto_do.model.DataManager;
import the.autarch.tvto_do.model.FileManager;
import the.autarch.tvto_do.network.NetworkManager;
import android.app.Application;

import com.activeandroid.ActiveAndroid;

public class TVTDApplication extends com.activeandroid.app.Application {
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		
		NetworkManager.initialize(this);
		DataManager.initialize(this);
		FileManager.initialize(this);
	}
}
