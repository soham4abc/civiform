package services.applicant.question;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import services.Path;
import services.applicant.ApplicantData;
import services.applicant.ValidationErrorMessage;
import services.question.QuestionDefinition;
import services.question.QuestionType;
import services.question.TextQuestionDefinition;

public class TextQuestion extends ApplicantQuestion {

  private Optional<String> textValue;

  public TextQuestion(QuestionDefinition questionDefinition, ApplicantData applicantData) {
    super(questionDefinition, applicantData);
    assertQuestionType();
  }

  @Override
  public boolean hasQuestionErrors() {
    return !getQuestionErrors().isEmpty();
  }

  public ImmutableSet<ValidationErrorMessage> getQuestionErrors() {
    // TODO(https://github.com/seattle-uat/civiform/issues/634): Fix bug related to hasValue.
    if (!hasValue()) {
      return ImmutableSet.of();
    }

    TextQuestionDefinition definition = getQuestionDefinition();
    int textLength = getTextValue().map(s -> s.length()).orElse(0);
    ImmutableSet.Builder<ValidationErrorMessage> errors = ImmutableSet.builder();

    if (definition.getMinLength().isPresent()) {
      int minLength = definition.getMinLength().getAsInt();
      if (textLength < minLength) {
        errors.add(ValidationErrorMessage.textTooShortError(minLength));
      }
    }

    if (definition.getMaxLength().isPresent()) {
      int maxLength = definition.getMaxLength().getAsInt();
      if (textLength > maxLength) {
        errors.add(ValidationErrorMessage.textTooLongError(maxLength));
      }
    }

    return errors.build();
  }

  @Override
  public boolean hasTypeSpecificErrors() {
    // There are no inherent requirements in a text question.
    return false;
  }

  public boolean hasValue() {
    return getTextValue().isPresent();
  }

  public Optional<String> getTextValue() {
    if (textValue != null) {
      return textValue;
    }

    textValue = getApplicantData().readString(getTextPath());

    return textValue;
  }

  public void assertQuestionType() {
    if (!getType().equals(QuestionType.TEXT)) {
      throw new RuntimeException(
          String.format("Question is not a TEXT question: %s (type: %s)", getPath(), getType()));
    }
  }

  public TextQuestionDefinition getQuestionDefinition() {
    assertQuestionType();
    return (TextQuestionDefinition) super.getQuestionDefinition();
  }

  public Path getTextPath() {
    return getQuestionDefinition().getTextPath();
  }
}
