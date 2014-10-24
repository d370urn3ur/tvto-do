package the.autarch.tvto_do.activity;

import android.support.v7.app.ActionBarActivity;

import de.greenrobot.event.EventBus;

/**
 * Created by jpierce on 9/10/14.
 */
public class BaseEventActivity extends ActionBarActivity {

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
