package the.autarch.tvto_do.activity;

import android.support.v7.app.ActionBarActivity;

import com.octo.android.robospice.SpiceManager;

import de.greenrobot.event.EventBus;
import the.autarch.tvto_do.fragment.BaseSpiceFragment;
import the.autarch.tvto_do.service.TVRageSpiceService;

/**
 * Created by jpierce on 9/10/14.
 */
public class BaseSpiceActivity extends ActionBarActivity implements BaseSpiceFragment.BaseSpiceFragmentHostInterface {

    private SpiceManager _rageManager = new SpiceManager(TVRageSpiceService.class);

    @Override
    protected void onStart() {
        super.onStart();
        _rageManager.start(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        _rageManager.shouldStop();
        EventBus.getDefault().unregister(this);
    }

    protected SpiceManager getTvRageManager() { return _rageManager; }

    @Override
    public SpiceManager getRageManagerForFragment() {
        return getTvRageManager();
    }
}
