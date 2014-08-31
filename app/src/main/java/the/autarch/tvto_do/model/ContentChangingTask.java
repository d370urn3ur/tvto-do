package the.autarch.tvto_do.model;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public abstract class ContentChangingTask<T1, T2, T3> extends AsyncTask<T1, T2, Boolean> {

	private AbstractDataSource<?> _dataSource = null;
	protected volatile String _toastMessage = null;
	private Context _context;
	
	public ContentChangingTask(AbstractDataSource<?> dataSource, Context context) {
		_dataSource = dataSource;
		_context = context;
	}
	
	@Override
	protected void onPostExecute(Boolean param) {
		if(param.booleanValue()) {
			_dataSource.notifyObservers();
		}
		
		if(_toastMessage != null) {
			Toast.makeText(_context, _toastMessage, Toast.LENGTH_SHORT).show();
		}
	}
}
