package views.questiontypes;

import j2html.tags.Tag;
import java.time.LocalDate;
import java.util.Optional;
import services.applicant.question.ApplicantQuestion;
import services.applicant.question.DateQuestion;
import views.components.FieldWithLabel;

public class DateQuestionRenderer extends ApplicantQuestionRenderer {

  public DateQuestionRenderer(ApplicantQuestion question) {
    super(question);
  }

  @Override
  public Tag render(ApplicantQuestionRendererParams params) {
    DateQuestion dateQuestion = question.createDateQuestion();

    FieldWithLabel dateField =
        FieldWithLabel.date().setFieldName(dateQuestion.getDatePath().toString());
    if (dateQuestion.getDateValue().isPresent()) {
      Optional<String> value = dateQuestion.getDateValue().map(LocalDate::toString);
      dateField.setValue(value);
    }
    Tag dateQuestionFormContent = dateField.getContainer();

    return renderInternal(params.messages(), dateQuestionFormContent, false);
  }
}
