package the.autarch.tvto_do.dagger;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import the.autarch.tvto_do.TVTDApplication;
import the.autarch.tvto_do.model.Show;

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
    @Provides @Singleton @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides @Singleton Manager provideCBLManager(@ForApplication Context appContext) {
        try {
            return new Manager(new AndroidContext(application), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            throw new RuntimeException("error instantiating CBLManager", e);
        }
    }

    @Provides @Singleton
    Database provideDatabase(Manager manager) {
        try {

            Database db = manager.getDatabase("saved-shows");
            createCBLViews(db);
            return db;

        } catch(CouchbaseLiteException e) {
            throw new RuntimeException("error instantiating CBLDatabase", e);
        }
    }

    private void createCBLViews(Database db) {

        // shows view
        db.getView("shows")
        .setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> documentProperties, Emitter emitter) {
                Object name = documentProperties.get(Show.KEY_TITLE);
                emitter.emit(name, documentProperties);
            }
        }, "6");

        // update shows view
        db.getView("shows-update")
                .setMap(new Mapper() {
                    @Override
                    public void map(final Map<String, Object> stringObjectMap, Emitter emitter) {

                        Map<String, Object> update = new HashMap<String, Object>() {{
                            put(Show.KEY_TVRAGE_ID, stringObjectMap.get(Show.KEY_TVRAGE_ID));
                            put("_id", stringObjectMap.get("_id"));
                            put(Show.KEY_NEXT_EPISODE_DATE, stringObjectMap.get(Show.KEY_NEXT_EPISODE_DATE);
                        }};

                        emitter.emit(stringObjectMap.get(Show.KEY_TVRAGE_ID), update);
                    }
                }, "2");
    }
}
