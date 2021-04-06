package services.applicant.question;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import services.Path;
import services.applicant.ApplicantData;
import services.applicant.ValidationErrorMessage;
import services.question.NameQuestionDefinition;
import services.question.QuestionDefinition;
import services.question.QuestionType;

public class NameQuestion extends ApplicantQuestion {

  private Optional<String> firstNameValue;
  private Optional<String> middleNameValue;
  private Optional<String> lastNameValue;

  public NameQuestion(QuestionDefinition questionDefinition, ApplicantData applicantData) {
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
        .addAll(getFirstNameErrors())
        .addAll(getLastNameErrors())
        .build();
  }

  public ImmutableSet<ValidationErrorMessage> getFirstNameErrors() {
    if (firstNameAnswered() && getFirstNameValue().isEmpty()) {
      return ImmutableSet.of(ValidationErrorMessage.create("First name is required."));
    }

    return ImmutableSet.of();
  }

  public ImmutableSet<ValidationErrorMessage> getLastNameErrors() {
    if (lastNameAnswered() && getLastNameValue().isEmpty()) {
      return ImmutableSet.of(ValidationErrorMessage.create("Last name is required."));
    }

    return ImmutableSet.of();
  }

  public boolean hasFirstNameValue() {
    return getFirstNameValue().isPresent();
  }

  public boolean hasMiddleNameValue() {
    return getMiddleNameValue().isPresent();
  }

  public boolean hasLastNameValue() {
    return getLastNameValue().isPresent();
  }

  public Optional<String> getFirstNameValue() {
    if (firstNameValue != null) {
      return firstNameValue;
    }

    firstNameValue = getApplicantData().readString(getFirstNamePath());

    return firstNameValue;
  }

  public Optional<String> getMiddleNameValue() {
    if (middleNameValue != null) {
      return middleNameValue;
    }

    middleNameValue = getApplicantData().readString(getMiddleNamePath());

    return middleNameValue;
  }

  public Optional<String> getLastNameValue() {
    if (lastNameValue != null) {
      return lastNameValue;
    }

    lastNameValue = getApplicantData().readString(getLastNamePath());

    return lastNameValue;
  }

  public void assertQuestionType() {
    if (!getType().equals(QuestionType.NAME)) {
      throw new RuntimeException(
          String.format("Question is not a NAME question: %s (type: %s)", getPath(), getType()));
    }
  }

  public NameQuestionDefinition getQuestionDefinition() {
    assertQuestionType();
    return (NameQuestionDefinition) super.getQuestionDefinition();
  }

  public Path getMiddleNamePath() {
    return getQuestionDefinition().getMiddleNamePath();
  }

  public Path getFirstNamePath() {
    return getQuestionDefinition().getFirstNamePath();
  }

  public Path getLastNamePath() {
    return getQuestionDefinition().getLastNamePath();
  }

  private boolean firstNameAnswered() {
    return getApplicantData().hasPath(getFirstNamePath());
  }

  private boolean lastNameAnswered() {
    return getApplicantData().hasPath(getLastNamePath());
  }
}
