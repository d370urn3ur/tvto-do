package the.autarch.tvto_do.fragment;

import android.support.v4.app.Fragment;

import the.autarch.tvto_do.activity.BaseEventActivity;

/**
 * Created by jpierce on 10/27/2014.
 */
public class BaseInjectableFragment extends Fragment {

    protected void inject() {
        BaseEventActivity activity = (BaseEventActivity)getActivity();
        activity.inject(this);
    }
}
