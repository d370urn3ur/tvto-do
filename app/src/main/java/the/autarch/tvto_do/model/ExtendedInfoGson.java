package the.autarch.tvto_do.model;

import android.text.TextUtils;
import android.text.format.Time;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ExtendedInfoGson {

    private static final String SHOW_ID_KEY = "Show ID";
	private static final String TITLE_KEY = "Next Episode";
	private static final String DATE_KEY = "GMT+0 NODST";
    private static final String ENDED_KEY = "Ended";

    public String tvRageId;
	public String nextEpisodeTitle;
	public Time nextEpisodeTime;
    public boolean ended;
	
	public static ExtendedInfoGson parseValues(HashMap<String, String> values) {

		ExtendedInfoGson result = new ExtendedInfoGson();

        if(values.containsKey(SHOW_ID_KEY)) {
            result.tvRageId = values.get(SHOW_ID_KEY);
        }
		
		if(values.containsKey(TITLE_KEY)) {
			String value = values.get(TITLE_KEY);
			String[] components = value.split("\\^");
			if(components.length > 1) {
				result.nextEpisodeTitle = components[1] + " (" + components[0] + ")";
			} else {
				result.nextEpisodeTitle = value;
			}
		}
		
		if(values.containsKey(DATE_KEY)) {
			Time time = new Time();
			int seconds = Integer.parseInt(values.get(DATE_KEY));
			long millis = TimeUnit.SECONDS.toMillis(seconds);
			time.set(millis);
            result.nextEpisodeTime = time;
		}

        if(values.containsKey(ENDED_KEY)) {
            // TODO: test ended
        }
		
		return result;
	}

    public boolean hasInfo() {
        return !TextUtils.isEmpty(tvRageId) && (!TextUtils.isEmpty(nextEpisodeTitle) || nextEpisodeTime != null);
    }

    /* EXAMPLES:

        Show ID@22622
        Show Name@Modern Family
        Show URL@http://www.tvrage.com/Modern_Family
        Premiered@2009
        Started@Sep/23/2009
        Ended@
        Latest Episode@05x10^The Old Man & the Tree^Dec/11/2013
        Next Episode@05x11^And One to Grow On^Jan/08/2014
        RFC3339@2014-01-08T21:00:00-5:00
        GMT+0 NODST@1389229200
        Country@USA
        Status@Returning Series
        Classification@Scripted
        Genres@Comedy
        Network@ABC
        Airtime@Wednesday at 09:00 pm
        Runtime@30

        Show ID@16356
        Show Name@Mad Men
        Show URL@http://www.tvrage.com/Mad_Men
        Premiered@2007
        Started@Jul/19/2007
        Ended@
        Latest Episode@06x13^In Care Of^Jun/23/2013
        Next Episode@07x01^Season 7, Episode 1^2014
        GMT+0 NODST@
        Country@USA
        Status@Final Season
        Classification@Scripted
        Genres@Drama
        Network@AMC
        Airtime@Sunday at 10:00 pm
        Runtime@60
    */
}
