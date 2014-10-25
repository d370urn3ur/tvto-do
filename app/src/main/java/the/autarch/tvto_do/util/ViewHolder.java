package the.autarch.tvto_do.util;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by jpierce on 10/25/2014.
 */
public class ViewHolder extends SparseArray<View> {

    public static <T extends View> T get(View view,  int id) {
        ViewHolder h = (ViewHolder)view.getTag();
        if(h == null) {
            h = new ViewHolder();
            view.setTag(h);
        }

        View childView = h.get(id);
        if(childView == null) {
            childView = view.findViewById(id);
            h.put(id, childView);
        }

        return (T)childView;
    }

}
