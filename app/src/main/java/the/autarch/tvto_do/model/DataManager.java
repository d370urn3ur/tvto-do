package the.autarch.tvto_do.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * DataManager singleton responsible for instantiating the database and providing access to data sources
 * @author jpierce
 */
public class DataManager {
	
	private static DataManager _instance = null;
	
	private DbHelper _helper;
	private SQLiteDatabase _database;
	private ShowDataSource _dataSource;
	
	private DataManager(Context context) {
		// TODO: move this to background and send notification when ready
		_helper = DbHelper.getInstance(context);
		_database = _helper.getWritableDatabase();
		_dataSource = new ShowDataSource(_database, context);
	}
	
	public static void initialize(Context context) {
		if(_instance == null) {
			_instance = new DataManager(context.getApplicationContext());
		}
	}

	public static DataManager getInstance() {
		if(_instance == null) {
			Log.e("DataManager", "DataManager wasn't initialized");
		}
		return _instance;
	}
	
	public ShowDataSource getShowDataSource() {
		return _dataSource;
	}
}
