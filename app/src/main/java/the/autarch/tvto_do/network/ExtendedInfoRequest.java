package the.autarch.tvto_do.network;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import the.autarch.tvto_do.TVTDConstants;
import the.autarch.tvto_do.model.ExtendedInfoWrapper;

import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;

public class ExtendedInfoRequest extends Request<ExtendedInfoWrapper> {

	private final Map<String, String> headers;
    private final Listener<ExtendedInfoWrapper> listener;
    
    public static ExtendedInfoRequest getExtendedInfoForTvRageId(String tvrageId, Map<String, String> headers, Listener<ExtendedInfoWrapper> listener, ErrorListener errorListener) {
    	Uri.Builder builder = new Uri.Builder();
		Uri uri = builder.scheme(TVTDConstants.SCHEME_HTTP)
					.authority(TVTDConstants.TV_RAGE_BASE_URL)
					.appendPath("tools")
					.appendPath("quickinfo.php")
					.appendQueryParameter("sid", tvrageId)
					.build();
		
		return new ExtendedInfoRequest(uri.toString(), headers, listener, errorListener);
    }
    
    public ExtendedInfoRequest(String url, Map<String, String> headers, Listener<ExtendedInfoWrapper> listener, ErrorListener errorListener) {
    	super(Method.GET, url, errorListener);
    	this.headers = headers;
    	this.listener = listener;
    }
    
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected void deliverResponse(ExtendedInfoWrapper response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<ExtendedInfoWrapper> parseNetworkResponse(NetworkResponse response) {
        try {
            String responseText = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            
            HashMap<String, String> values = new HashMap<String,String>();
            String[] items = responseText.split("\n");
            String trimmed;
            for(String i : items) {
            	trimmed = i.trim();
            	String[] kv = trimmed.split("@");
            	if(kv.length > 1) {
            		values.put(kv[0], kv[1]);
            	}
            }
            ExtendedInfoWrapper extInfo = ExtendedInfoWrapper.parseValues(values);
            return Response.success(extInfo, HttpHeaderParser.parseCacheHeaders(response));
            
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
