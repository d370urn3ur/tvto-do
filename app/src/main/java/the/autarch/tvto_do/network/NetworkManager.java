package the.autarch.tvto_do.network;

import java.util.HashMap;

import the.autarch.tvto_do.model.DataManager;
import the.autarch.tvto_do.model.FileManager;
import the.autarch.tvto_do.model.Show;
import the.autarch.tvto_do.util.OkHttpStack;
import the.autarch.tvto_do.util.TVTDImageCache;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;

/**
 * Singleton that instantiates and provides access to the request queue, image loader, and URL templates
 * @author jpierce
 *
 */
public class NetworkManager {
	
	private static NetworkManager _instance = null;
	
	private static RequestQueue _requestQueue = null;
	private static ImageCache _imageCache = null;
	private static ImageLoader _imageLoader = null;
	
	HashMap<String, ImageContainer> _imageRequests = new HashMap<String, ImageContainer>();
	
	public static void initialize(Context context) {
		_instance = new NetworkManager(context.getApplicationContext());
	}
	
	public static NetworkManager getInstance() {
		if(_instance == null) {
			Log.e("NetworkManager", "NetworkManager wasn't initialized");
		}
		return _instance;
	}
	
	private NetworkManager(Context context) {
		_requestQueue = Volley.newRequestQueue(context.getApplicationContext(), new OkHttpStack());
		_imageCache = new TVTDImageCache();
		_imageLoader = new ImageLoader(_requestQueue, _imageCache);
	}
	
	public RequestQueue getRequestQueue() {
		return _requestQueue;
	}
	
	public ImageLoader getImageLoader() {
		return _imageLoader;
	}
	
	public ImageCache getImageCache() {
		return _imageCache;
	}
	
	public void downloadAndSaveImageForShow(final Show show) {
		
		if(_imageRequests.containsKey(show)) {
			return;
		}
		
		ImageContainer ic = _imageLoader.get(show.poster138Url, new ImageListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				_imageRequests.remove(show);
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				Bitmap b = response.getBitmap();
				if(b != null) {
					String filename = FileManager.getInstance().writeBitmapToFileForShow(response.getBitmap(), show);
					show.poster138filepath = filename;
					DataManager.getInstance().getShowDataSource().update(show);
				}
				_imageRequests.remove(show);
			}
		});
		
		if(ic.getBitmap() == null) {
			_imageRequests.put(show.tvrageId, ic);
		}
	}
}
