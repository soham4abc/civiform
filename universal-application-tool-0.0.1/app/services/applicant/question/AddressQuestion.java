package services.applicant.question;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import services.Path;
import services.applicant.ApplicantData;
import services.applicant.ValidationErrorMessage;
import services.question.AddressQuestionDefinition;
import services.question.QuestionDefinition;
import services.question.QuestionType;

public class AddressQuestion extends ApplicantQuestion {

  private Optional<String> streetValue;
  private Optional<String> cityValue;
  private Optional<String> stateValue;
  private Optional<String> zipValue;

  public AddressQuestion(QuestionDefinition questionDefinition, ApplicantData applicantData) {
    super(questionDefinition, applicantData);
    assertQuestionType();
  }

  @Override
  public boolean hasQuestionErrors() {
    return !getQuestionErrors().isEmpty();
  }

  public ImmutableSet<ValidationErrorMessage> getQuestionErrors() {
    // TODO: Implement admin-defined validation.
    return ImmutableSet.of();
  }

  @Override
  public boolean hasTypeSpecificErrors() {
    return !getAllTypeSpecificErrors().isEmpty();
  }

  public ImmutableSet<ValidationErrorMessage> getAllTypeSpecificErrors() {
    return ImmutableSet.<ValidationErrorMessage>builder()
        .addAll(getAddressErrors())
        .addAll(getStreetErrors())
        .addAll(getCityErrors())
        .addAll(getStateErrors())
        .addAll(getZipErrors())
        .build();
  }

  public ImmutableSet<ValidationErrorMessage> getAddressErrors() {
    // TODO: Implement address validation.
    return ImmutableSet.of();
  }

  public ImmutableSet<ValidationErrorMessage> getStreetErrors() {
    if (streetAnswered() && getStreetValue().isEmpty()) {
      return ImmutableSet.of(ValidationErrorMessage.create("Street is required."));
    }

    return ImmutableSet.of();
  }

  public ImmutableSet<ValidationErrorMessage> getCityErrors() {
    if (cityAnswered() && getCityValue().isEmpty()) {
      return ImmutableSet.of(ValidationErrorMessage.create("City is required."));
    }

    return ImmutableSet.of();
  }

  public ImmutableSet<ValidationErrorMessage> getStateErrors() {
    // TODO: Validate state further.
    if (stateAnswered() && getStateValue().isEmpty()) {
      return ImmutableSet.of(ValidationErrorMessage.create("State is required."));
    }

    return ImmutableSet.of();
  }

  public ImmutableSet<ValidationErrorMessage> getZipErrors() {
    if (zipAnswered()) {
      Optional<String> zipValue = getZipValue();
      if (zipValue.isEmpty()) {
        return ImmutableSet.of(ValidationErrorMessage.create("Zip code is required."));
      }

      Pattern pattern = Pattern.compile("^[0-9]{5}(?:-[0-9]{4})?$");
      Matcher matcher = pattern.matcher(zipValue.get());
      if (!matcher.matches()) {
        return ImmutableSet.of(ValidationErrorMessage.create("Invalid zip code."));
      }
    }

    return ImmutableSet.of();
  }

  public boolean hasStreetValue() {
    return getStreetValue().isPresent();
  }

  public boolean hasCityValue() {
    return getCityValue().isPresent();
  }

  public boolean hasStateValue() {
    return getStateValue().isPresent();
  }

  public boolean hasZipValue() {
    return getZipValue().isPresent();
  }

  public Optional<String> getStreetValue() {
    if (streetValue != null) {
      return streetValue;
    }

    streetValue = getApplicantData().readString(getStreetPath());
    return streetValue;
  }

  public Optional<String> getCityValue() {
    if (cityValue != null) {
      return cityValue;
    }

    cityValue = getApplicantData().readString(getCityPath());
    return cityValue;
  }

  public Optional<String> getStateValue() {
    if (stateValue != null) {
      return stateValue;
    }

    stateValue = getApplicantData().readString(getStatePath());
    return stateValue;
  }

  public Optional<String> getZipValue() {
    if (zipValue != null) {
      return zipValue;
    }

    zipValue = getApplicantData().readString(getZipPath());
    return zipValue;
  }

  public void assertQuestionType() {
    if (!getType().equals(QuestionType.ADDRESS)) {
      throw new RuntimeException(
          String.format(
              "Question is not an ADDRESS question: %s (type: %s)",
              getQuestionDefinition().getPath(), getQuestionDefinition().getQuestionType()));
    }
  }

  public AddressQuestionDefinition getQuestionDefinition() {
    assertQuestionType();
    return (AddressQuestionDefinition) super.getQuestionDefinition();
  }

  public Path getStreetPath() {
    return getQuestionDefinition().getStreetPath();
  }

  public Path getCityPath() {
    return getQuestionDefinition().getCityPath();
  }

  public Path getStatePath() {
    return getQuestionDefinition().getStatePath();
  }

  public Path getZipPath() {
    return getQuestionDefinition().getZipPath();
  }

  private boolean streetAnswered() {
    return getApplicantData().hasPath(getStreetPath());
  }

  private boolean cityAnswered() {
    return getApplicantData().hasPath(getCityPath());
  }

  private boolean stateAnswered() {
    return getApplicantData().hasPath(getStatePath());
  }

  private boolean zipAnswered() {
    return getApplicantData().hasPath(getZipPath());
  }
}
