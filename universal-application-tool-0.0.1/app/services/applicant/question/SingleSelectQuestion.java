package services.applicant.question;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import services.Path;
import services.applicant.ApplicantData;
import services.applicant.ValidationErrorMessage;
import services.question.MultiOptionQuestionDefinition;
import services.question.QuestionDefinition;
import services.question.TranslationNotFoundException;

// TODO(https://github.com/seattle-uat/civiform/issues/396): Implement a question that allows for
// multiple answer selections (i.e. the value is a list)
public class SingleSelectQuestion extends ApplicantQuestion {

  private Optional<String> selectedOptionValue;

  public SingleSelectQuestion(QuestionDefinition questionDefinition, ApplicantData applicantData) {
    super(questionDefinition, applicantData);
    assertQuestionType();
  }

  @Override
  public boolean hasQuestionErrors() {
    return !getQuestionErrors().isEmpty();
  }

  @Override
  public boolean hasTypeSpecificErrors() {
    // There are no inherent requirements in a multi-option question.
    return false;
  }

  public ImmutableSet<ValidationErrorMessage> getQuestionErrors() {
    // TODO(https://github.com/seattle-uat/civiform/issues/416): Implement validation
    return ImmutableSet.of();
  }

  public boolean hasValue() {
    return getSelectedOptionValue().isPresent();
  }

  public Optional<String> getSelectedOptionValue() {
    if (selectedOptionValue != null) {
      return selectedOptionValue;
    }

    selectedOptionValue = getApplicantData().readString(getSelectionPath());

    return selectedOptionValue;
  }

  public void assertQuestionType() {
    if (!getType().isMultiOptionType()) {
      throw new RuntimeException(
          String.format(
              "Question is not a multi-option question: %s (type: %s)", getPath(), getType()));
    }
  }

  public MultiOptionQuestionDefinition getQuestionDefinition() {
    assertQuestionType();
    return (MultiOptionQuestionDefinition) super.getQuestionDefinition();
  }

  public Path getSelectionPath() {
    return getQuestionDefinition().getSelectionPath();
  }

  public ImmutableList<String> getOptions() {
    try {
      return getQuestionDefinition().getOptionsForLocale(getApplicantData().preferredLocale());
    } catch (TranslationNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
