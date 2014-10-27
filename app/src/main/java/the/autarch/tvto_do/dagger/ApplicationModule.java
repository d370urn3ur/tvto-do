package the.autarch.tvto_do.dagger;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import the.autarch.tvto_do.TVTDApplication;

/**
 * Created by joshua.pierce on 27/10/14.
 */
@Module(library = true)
public class ApplicationModule {

    private final TVTDApplication application;

    public ApplicationModule(TVTDApplication application) {
        this.application = application;
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link ForApplication @ForApplication} to explicitly differentiate it from an activity context.
     */
    @Provides
    @Singleton @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides @Singleton
    Database provideDatabase() {
        try {
            Manager manager = new Manager(new AndroidContext(application), Manager.DEFAULT_OPTIONS);
            return manager.getDatabase("saved-shows");
        } catch(IOException e) {
//            Ln.e("got exception starting CBL manager: %s", e.toString());
            return null;
        } catch(CouchbaseLiteException e) {
//            Ln.e("got exception getting database: %s", e.toString());
            return null;
        }
    }
}
