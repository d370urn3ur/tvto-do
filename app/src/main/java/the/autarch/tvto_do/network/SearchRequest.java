package the.autarch.tvto_do.network;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import the.autarch.tvto_do.TVTDConstants;
import the.autarch.tvto_do.model.SearchResultWrapper;

import android.net.Uri;
import android.net.Uri.Builder;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Volley adapter for JSON requests that will be parsed into Java objects by Gson.
 */
public class SearchRequest extends Request<Collection<SearchResultWrapper>> {
	
	//	http://api.trakt.tv/search/shows.format/apikey?query=query&limit=limit&seasons=seasons
	
    private final Gson gson = new Gson();
    private final Map<String, String> headers;
    private final Listener<Collection<SearchResultWrapper>> listener;
    
    public static SearchRequest searchForText(String searchText, Map<String, String> headers, Listener<Collection<SearchResultWrapper>> listener, ErrorListener errorListener) {
    	
    	Builder uriB = new Uri.Builder();
		Uri uri = uriB.scheme(TVTDConstants.SCHEME_HTTP)
				.authority(TVTDConstants.TRAKT_BASE_URL)
				.appendPath("search")
				.appendPath("shows" + TVTDConstants.JSON_FORMAT)
				.appendPath(TVTDConstants.TRAKT_API_KEY)
				.appendQueryParameter("query", searchText)
				.build();
		
		return new SearchRequest(uri.toString(), headers, listener, errorListener);
    }
    
    public SearchRequest(String url, Map<String, String> headers, Listener<Collection<SearchResultWrapper>> listener, ErrorListener errorListener) {
    	super(Method.GET, url, errorListener);
    	this.headers = headers;
    	this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(Collection<SearchResultWrapper> response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<Collection<SearchResultWrapper>> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Type collectionType = new TypeToken<Collection<SearchResultWrapper>>(){}.getType();
            Collection<SearchResultWrapper> collection = gson.fromJson(json, collectionType);
            return Response.success(collection, HttpHeaderParser.parseCacheHeaders(response));
            
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
