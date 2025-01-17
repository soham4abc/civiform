package auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.ExecutionException;
import javax.inject.Provider;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.oidc.profile.OidcProfile;
import repository.UserRepository;
import repository.WithPostgresContainer;

public class ProfileMergeTest extends WithPostgresContainer {

  private IdcsProfileAdapter profileAdapter;
  private ProfileFactory profileFactory;

  @Before
  public void setupFactory() {
    profileFactory = instanceOf(ProfileFactory.class);
    UserRepository repository = instanceOf(UserRepository.class);
    profileAdapter =
        new IdcsProfileAdapter(
            null,
            null,
            profileFactory,
            new Provider<UserRepository>() {
              @Override
              public UserRepository get() {
                return repository;
              }
            });
  }

  @Test
  public void testProfileCreation() throws ExecutionException, InterruptedException {
    OidcProfile oidcProfile = new OidcProfile();
    oidcProfile.addAttribute("user_emailid", "foo@example.com");

    UatProfileData profileData = profileAdapter.uatProfileFromOidcProfile(oidcProfile);
    UatProfile profile = profileFactory.wrapProfileData(profileData);

    assertThat(profileData.getEmail()).isEqualTo("foo@example.com");
    assertThat(profile.getEmailAddress().get()).isEqualTo("foo@example.com");
  }

  @Test
  public void testSuccessfulProfileMerge() {
    OidcProfile oidcProfile = new OidcProfile();
    oidcProfile.addAttribute("user_emailid", "foo@example.com");

    UatProfileData profileData = profileAdapter.uatProfileFromOidcProfile(oidcProfile);

    assertThat(
            profileAdapter
                .mergeUatProfile(profileFactory.wrapProfileData(profileData), oidcProfile)
                .getEmail())
        .isEqualTo("foo@example.com");
  }

  @Test
  public void testFailedProfileMerge() {
    OidcProfile oidcProfile = new OidcProfile();
    oidcProfile.addAttribute("user_emailid", "foo@example.com");
    OidcProfile conflictingProfile = new OidcProfile();
    oidcProfile.addAttribute("user_emailid", "bar@example.com");

    UatProfileData profileData = profileAdapter.uatProfileFromOidcProfile(oidcProfile);

    assertThatThrownBy(
            () ->
                profileAdapter.mergeUatProfile(
                    profileFactory.wrapProfileData(profileData), conflictingProfile))
        .hasCauseInstanceOf(ProfileMergeConflictException.class);
  }
}
