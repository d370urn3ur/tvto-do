package the.autarch.tvto_do.model;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractDataSource<T> {
	
	public interface DataSourceObserver {
		// TODO: keep track of modified resources HashMap<String, ArrayList<T>> with "inserted", "deleted", "updated" (public static keys)
		public void dataSourceModified();
	}
	
	protected SQLiteDatabase mDatabase;
	protected ArrayList<DataSourceObserver> _observers;
	
	public AbstractDataSource(SQLiteDatabase database) {
		mDatabase = database;
		_observers = new ArrayList<DataSourceObserver>();
	}
	
	public abstract void insert(T entity);
	public abstract void delete(T entity);
	public abstract void update(T entity);
	public abstract List<T> read();
	public abstract List<T> read(String selection, String[] selectionArgs, String groupBy, String having, String orderBy);
	
	public void addObserver(DataSourceObserver observer) {
		_observers.add(observer);
	}
	
	public void removeObserver(DataSourceObserver observer) {
		_observers.remove(observer);
	}

	public void notifyObservers() {
		for(DataSourceObserver observer : _observers) {
			observer.dataSourceModified();
		}
	}
}
