package the.autarch.tvto_do.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "tvtodo.db";
	private static final int DATABASE_VERSION = 4;
	
	private static DbHelper _instance = null;
	
	public static DbHelper getInstance(Context context) {
		if(_instance == null) {
			_instance = new DbHelper(context.getApplicationContext());
		}
		return _instance;
	}
	
	private DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(ShowDataSource.CREATE_COMMAND);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + ShowDataSource.TABLE_NAME);
		onCreate(db);
	}
	
}
