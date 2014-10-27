package the.autarch.tvto_do.dagger;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by joshua.pierce on 27/10/14.
 */
@Qualifier
@Retention(RUNTIME)
public @interface ForApplication {
}
