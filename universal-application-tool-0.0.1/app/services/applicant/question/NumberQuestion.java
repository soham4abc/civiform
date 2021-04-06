package services.applicant.question;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import services.Path;
import services.applicant.ApplicantData;
import services.applicant.ValidationErrorMessage;
import services.question.NumberQuestionDefinition;
import services.question.QuestionDefinition;
import services.question.QuestionType;

public class NumberQuestion extends ApplicantQuestion {

  private Optional<Long> numberValue;

  public NumberQuestion(QuestionDefinition questionDefinition, ApplicantData applicantData) {
    super(questionDefinition, applicantData);
    assertQuestionType();
  }

  @Override
  public boolean hasQuestionErrors() {
    return !getQuestionErrors().isEmpty();
  }

  public ImmutableSet<ValidationErrorMessage> getQuestionErrors() {
    if (!hasValue()) {
      return ImmutableSet.of();
    }

    NumberQuestionDefinition definition = getQuestionDefinition();
    long answer = getNumberValue().get();
    ImmutableSet.Builder<ValidationErrorMessage> errors = ImmutableSet.builder();

    if (definition.getMin().isPresent()) {
      long min = definition.getMin().getAsLong();
      if (answer < min) {
        errors.add(ValidationErrorMessage.numberTooSmallError(min));
      }
    }

    if (definition.getMax().isPresent()) {
      long max = definition.getMax().getAsLong();
      if (answer > max) {
        errors.add(ValidationErrorMessage.numberTooLargeError(max));
      }
    }

    return errors.build();
  }

  @Override
  public boolean hasTypeSpecificErrors() {
    // There are no inherent requirements in a number question.
    return false;
  }

  public boolean hasValue() {
    return getNumberValue().isPresent();
  }

  public Optional<Long> getNumberValue() {
    if (numberValue != null) {
      return numberValue;
    }

    numberValue = getApplicantData().readLong(getNumberPath());

    return numberValue;
  }

  public void assertQuestionType() {
    if (!getType().equals(QuestionType.NUMBER)) {
      throw new RuntimeException(
          String.format("Question is not a NUMBER question: %s (type: %s)", getPath(), getType()));
    }
  }

  public NumberQuestionDefinition getQuestionDefinition() {
    assertQuestionType();
    return (NumberQuestionDefinition) super.getQuestionDefinition();
  }

  public Path getNumberPath() {
    return getQuestionDefinition().getNumberPath();
  }
}
