package services.applicant.question;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import services.Path;
import services.applicant.ApplicantData;
import services.question.QuestionDefinition;
import services.question.QuestionType;
import services.question.TranslationNotFoundException;

/**
 * Represents a question in the context of a specific applicant. Classes that extend this one
 * represent the question as a specific question type (e.g. {@link NameQuestion}). These child
 * classes provide access to the applicant's answer for the question. They also implement
 * server-side validation logic.
 */
public abstract class ApplicantQuestion {

  private final QuestionDefinition questionDefinition;
  private final ApplicantData applicantData;

  protected ApplicantQuestion(QuestionDefinition questionDefinition, ApplicantData applicantData) {
    this.questionDefinition = checkNotNull(questionDefinition);
    this.applicantData = checkNotNull(applicantData);
  }

  protected ApplicantData getApplicantData() {
    return this.applicantData;
  }

  protected QuestionDefinition getQuestionDefinition() {
    return this.questionDefinition;
  }

  public QuestionType getType() {
    return questionDefinition.getQuestionType();
  }

  public String getQuestionText() {
    try {
      return questionDefinition.getQuestionText(applicantData.preferredLocale());
    } catch (TranslationNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public String getQuestionHelpText() {
    try {
      return questionDefinition.getQuestionHelpText(applicantData.preferredLocale());
    } catch (TranslationNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public Path getPath() {
    return questionDefinition.getPath();
  }

  /** Returns true if values do not meet conditions defined by admins. */
  public abstract boolean hasQuestionErrors();

  /**
   * Returns true if there is any type specific errors. The validation does not consider
   * admin-defined conditions.
   */
  public abstract boolean hasTypeSpecificErrors();

  public boolean hasErrors() {
    return hasQuestionErrors() || hasTypeSpecificErrors();
  }

  public Optional<Long> getUpdatedInProgramMetadata() {
    return applicantData.readLong(questionDefinition.getProgramIdPath());
  }

  public Optional<Long> getLastUpdatedTimeMetadata() {
    return applicantData.readLong(questionDefinition.getLastUpdatedTimePath());
  }

  @Override
  public boolean equals(@Nullable Object object) {
    if (object instanceof ApplicantQuestion) {
      ApplicantQuestion that = (ApplicantQuestion) object;
      return this.questionDefinition.equals(that.questionDefinition)
          && this.applicantData.equals(that.applicantData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(questionDefinition, applicantData);
  }
}
