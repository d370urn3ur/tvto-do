package the.autarch.tvto_do.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.octo.android.robospice.SpiceManager;

/**
 * Created by jpierce on 9/11/14.
 */
public class BaseSpiceFragment extends Fragment {

    public interface BaseSpiceFragmentHostInterface {
        public SpiceManager getTraktManagerForFragment();
        public SpiceManager getRageManagerForFragment();
    }
    private BaseSpiceFragmentHostInterface _hostInterface;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            _hostInterface = (BaseSpiceFragmentHostInterface)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("hosting activity must implement BaseSpiceFragmentHostInterface!!!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        _hostInterface = null;
    }

    protected SpiceManager getTraktManager() {
        return _hostInterface.getTraktManagerForFragment();
    }

    protected SpiceManager getRageManager() { return _hostInterface.getRageManagerForFragment(); }
}
