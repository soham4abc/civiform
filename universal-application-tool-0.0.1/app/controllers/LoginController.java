package controllers;

import auth.AdOidcClient;
import auth.IdcsOidcClient;
import com.google.common.base.Preconditions;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.inject.Inject;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.http.adapter.HttpActionAdapter;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.http.PlayHttpActionAdapter;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

/**
 * These routes can be hit even if you are logged in already, which is what allows the merge logic -
 * normally you need to be logged out in order to be redirected to a login page.
 */
public class LoginController extends Controller {
  private final OidcClient idcsClient;

  private final OidcClient adClient;

  private final SessionStore sessionStore;

  private final HttpActionAdapter httpActionAdapter;

  @Inject
  public LoginController(
      @AdOidcClient @Nullable OidcClient adClient,
      @IdcsOidcClient @Nullable OidcClient idcsClient,
      SessionStore sessionStore) {
    this.idcsClient = idcsClient;
    this.adClient = adClient;
    this.sessionStore = Preconditions.checkNotNull(sessionStore);
    this.httpActionAdapter = PlayHttpActionAdapter.INSTANCE;
  }

  public Result idcsLogin(Http.Request request) {
    return login(request, idcsClient);
  }

  public Result idcsLoginWithRedirect(Http.Request request, Optional<String> redirectTo) {
    if (redirectTo.isEmpty()) {
      return idcsLogin(request);
    }
    return login(request, idcsClient).addingToSession(request, "redirectTo", redirectTo.get());
  }

  public Result adfsLogin(Http.Request request) {
    return login(request, adClient);
  }

  // Logic taken from org.pac4j.play.deadbolt2.Pac4jHandler.beforeAuthCheck.
  private Result login(Http.Request request, OidcClient client) {
    if (client == null) {
      return badRequest("Identity provider secrets not configured.");
    }
    PlayWebContext webContext = new PlayWebContext(request);
    webContext.setRequestAttribute(OidcConfiguration.SCOPE, client.getConfiguration().getScope());
    Optional<RedirectionAction> redirect = client.getRedirectionAction(webContext, sessionStore);
    if (redirect.isPresent()) {
      return (Result) httpActionAdapter.adapt(redirect.get(), webContext);
    }
    return badRequest("cannot redirect to identity provider");
  }
}
