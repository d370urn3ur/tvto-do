package the.autarch.tvto_do.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joshua.pierce on 28/10/14.
 */
public class CBLObject extends HashMap<String, Object> {

    public CBLObject(Map<String, Object> data) {
        super(data);
        annotate();
    }

    protected void annotate() {
        Class c = this.getClass();
        for(Field f : c.getFields()) {
            CBLProperty anno = f.getAnnotation(CBLProperty.class);
            if(anno != null) {
                String key = anno.name();
                try {
                    f.set(this, get(key));
                } catch(IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
