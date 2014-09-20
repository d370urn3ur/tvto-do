package the.autarch.tvto_do.service;

import com.octo.android.robospice.retrofit.RetrofitGsonSpiceService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;

import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;
import the.autarch.tvto_do.model.ExtendedInfoWrapper;
import the.autarch.tvto_do.network.TvRageRest;

/**
 * Created by jpierce on 9/10/14.
 */
public class TVRageSpiceService extends RetrofitGsonSpiceService {

    private static final String BASE_URL = "http://services.tvrage.com";
//    private static final String TV_RAGE_API_KEY = "YDtBVboidHSwVGOvrLgK";


    @Override
    public void onCreate() {
        super.onCreate();
        addRetrofitInterface(TvRageRest.class);
    }

    @Override
    protected String getServerUrl() {
        return BASE_URL;
    }

    @Override
    protected RestAdapter.Builder createRestAdapterBuilder() {
        RestAdapter.Builder b = super.createRestAdapterBuilder();
        b.setConverter(_rageExtendedInfoConverter);
        return b;
    }

    private Converter _rageExtendedInfoConverter = new Converter() {
        @Override
        public Object fromBody(TypedInput body, Type type) throws ConversionException {

            Reader r = null;
            try {

                r = new InputStreamReader(body.in(), "UTF-8");

                char[] buf = new char[2048];
                StringBuilder s = new StringBuilder();
                while (true) {
                    int n = r.read(buf);
                    if (n < 0)
                        break;
                    s.append(buf, 0, n);
                }

                HashMap<String, String> values = new HashMap<String,String>();
                String strippedString = s.toString().replace("<pre>", "");
                String[] items = strippedString.split("\n");
                String trimmed;
                for(String i : items) {
                    trimmed = i.trim();
                    String[] kv = trimmed.split("@");
                    if(kv.length > 1) {
                        values.put(kv[0], kv[1]);
                    }
                }
                ExtendedInfoWrapper extInfo = ExtendedInfoWrapper.parseValues(values);
                return extInfo;

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    r.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        public TypedOutput toBody(Object object) {
            // TODO: what does this do?
            return null;
        }
    };
}
