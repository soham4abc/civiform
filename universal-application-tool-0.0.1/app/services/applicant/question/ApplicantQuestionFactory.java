package services.applicant.question;

import services.applicant.ApplicantData;
import services.question.QuestionDefinition;

public class ApplicantQuestionFactory {

  public static ApplicantQuestion createQuestion(
      QuestionDefinition questionDefinition, ApplicantData applicantData) {
    switch (questionDefinition.getQuestionType()) {
      case ADDRESS:
        return new AddressQuestion(questionDefinition, applicantData);
      case DROPDOWN:
        return new SingleSelectQuestion(questionDefinition, applicantData);
      case NAME:
        return new NameQuestion(questionDefinition, applicantData);
      case NUMBER:
        return new NumberQuestion(questionDefinition, applicantData);
      case TEXT:
        return new TextQuestion(questionDefinition, applicantData);
      case REPEATER: // fallthrough intended - repeater question is not yet handled.
      default:
        throw new RuntimeException(
            "Unrecognized question type: " + questionDefinition.getQuestionType());
    }
  }
}
