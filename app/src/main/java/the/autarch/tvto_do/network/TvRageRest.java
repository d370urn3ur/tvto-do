package the.autarch.tvto_do.network;

import retrofit.http.GET;
import retrofit.http.Query;
import the.autarch.tvto_do.model.ExtendedInfoWrapper;

/**
 * Created by jpierce on 9/11/14.
 */
public interface TvRageRest {

    @GET("/tools/quickinfo.php")
    public ExtendedInfoWrapper getExtendedInfo(@Query("sid") String showId);

}
