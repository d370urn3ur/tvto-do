package the.autarch.tvto_do.loader;

import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class AbstractDataLoader<E extends List<?>> extends AsyncTaskLoader<E> {

	protected E _dataList = null;
	
	public AbstractDataLoader(Context context) {
		super(context);
	}
	
	/**
	 * Runs on a worker thread, loading in our data. Delegates the real work to
	 * concrete subclass' buildList() method.
	 */
	@Override
	public E loadInBackground() {
		return buildList();
	}
	
	protected abstract E buildList();	// concrete children override this to perform the actual queries
	
	/**
	 * Runs on the UI thread, routing the results from the background thread to
	 * whatever is using the dataList.
	 */
	@Override
	public void deliverResult(E dataList) {
		
		if (isReset()) {
			// An async query came in while the loader is stopped
			if(dataList != null) {
				onReleaseResources(dataList);
			}
		}
		
		E oldDataList = _dataList;
		_dataList = dataList;
		
		if (isStarted()) {
			super.deliverResult(dataList);
		}
		
		if (oldDataList != null) {
			onReleaseResources(oldDataList);
		}
	}
	
	/**
	 * Starts an asynchronous load of the list data. When the result is ready
	 * the callbacks will be called on the UI thread. If a previous load has
	 * been completed and is still valid the result may be passed to the
	 * callbacks immediately.
	 * 
	 * Must be called from the UI thread.
	 */
	@Override
	protected void onStartLoading() {
		if (_dataList != null) {
			deliverResult(_dataList);
		}
		
		if (takeContentChanged() || _dataList == null || _dataList.size() == 0) {
			forceLoad();
		}
	}
	
	/**
	 * Must be called from the UI thread, triggered by a call to stopLoading().
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}
	
	/**
	 * Must be called from the UI thread, triggered by a call to cancel(). Here,
	 * we make sure our Cursor is closed, if it still exists and is not already
	 * closed.
	 */
	@Override
	public void onCanceled(E dataList) {
		super.onCanceled(dataList);
		
		onReleaseResources(dataList);
	}
	
	/**
	 * Must be called from the UI thread, triggered by a call to reset(). Here,
	 * we make sure our Cursor is closed, if it still exists and is not already
	 * closed.
	 */
	@Override
	protected void onReset() {
		super.onReset();
		
		// Ensure the loader is stopped
		onStopLoading();
		
		if (_dataList != null) {
			onReleaseResources(_dataList);
			_dataList = null;
		}
	}
	
	protected void onReleaseResources(E dataList) {
		// nothing to do for simple List<E>
	}
}