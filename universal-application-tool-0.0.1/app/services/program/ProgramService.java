package services.program;

import com.google.common.collect.ImmutableList;
import forms.BlockForm;
import java.util.Locale;
import java.util.concurrent.CompletionStage;
import models.Application;
import services.CiviFormError;
import services.ErrorAnd;
import services.question.exceptions.QuestionNotFoundException;
import services.question.types.QuestionDefinition;

/**
 * The service responsible for accessing the Program resource. Admins create programs to represent
 * specific benefits programs that applicants can apply for. Each program consists of a list of
 * sequential {@link BlockDefinition}s that are rendered one per-page for the applicant. A {@link
 * BlockDefinition} contains one or more {@link QuestionDefinition}s defined in the {@link
 * services.question.QuestionService}.
 */
public interface ProgramService {

  /**
   * Get the definition for a given program.
   *
   * @param id the ID of the program to retrieve
   * @return the {@link ProgramDefinition} for the given ID if it exists
   * @throws ProgramNotFoundException when ID does not correspond to a real Program
   */
  ProgramDefinition getProgramDefinition(long id) throws ProgramNotFoundException;

  /** Get the data object about the programs that are in the active or draft version. */
  ActiveAndDraftPrograms getActiveAndDraftPrograms();

  /**
   * Get the definition of a given program asynchronously.
   *
   * @param id the ID of the program to retrieve
   * @return the {@link ProgramDefinition} for the given ID if it exists, or a
   *     ProgramNotFoundException is thrown when the future completes and ID does not correspond to
   *     a real Program
   */
  CompletionStage<ProgramDefinition> getProgramDefinitionAsync(long id);

  /**
   * Create a new program with an empty block.
   *
   * @param adminName a name for this program for internal use by admins - this is immutable once
   *     set
   * @param adminDescription a description of this program for use by admins
   * @param defaultDisplayName the name of this program to display to applicants
   * @param defaultDisplayDescription a description for this program to display to applicants
   * @return the {@link ProgramDefinition} that was created if succeeded, or a set of errors if
   *     failed
   */
  ErrorAnd<ProgramDefinition, CiviFormError> createProgramDefinition(
      String adminName,
      String adminDescription,
      String defaultDisplayName,
      String defaultDisplayDescription);

  /**
   * Update a program's mutable fields: admin description, display name and description for
   * applicants.
   *
   * @param programId the ID of the program to update
   * @param locale the locale for this update - only applies to applicant display name and
   *     description
   * @param adminDescription the description of this program - visible only to admins
   * @param displayName a name for this program
   * @param displayDescription the description of what the program provides
   * @return the {@link ProgramDefinition} that was updated if succeeded, or a set of errors if
   *     failed
   * @throws ProgramNotFoundException when programId does not correspond to a real Program.
   */
  ErrorAnd<ProgramDefinition, CiviFormError> updateProgramDefinition(
      long programId,
      Locale locale,
      String adminDescription,
      String displayName,
      String displayDescription)
      throws ProgramNotFoundException;

  /**
   * Add or update a localization of the program's publicly-visible display name and description.
   *
   * @param programId the ID of the program to update
   * @param locale the {@link Locale} to update
   * @param displayName a localized display name for this program
   * @param displayDescription a localized description for this program
   * @return the {@link ProgramDefinition} that was successfully updated, or a set of errors if the
   *     update failed
   * @throws ProgramNotFoundException if the programId does not correspond to a valid program
   */
  ErrorAnd<ProgramDefinition, CiviFormError> updateLocalization(
      long programId, Locale locale, String displayName, String displayDescription)
      throws ProgramNotFoundException;

  /**
   * Adds an empty {@link BlockDefinition} to the given program.
   *
   * @param programId the ID of the program to update
   * @return the {@link ProgramDefinition} that was updated if succeeded, or a set of errors with
   *     the unmodified program definition if failed
   * @throws ProgramNotFoundException when programId does not correspond to a real Program.
   */
  ErrorAnd<ProgramDefinition, CiviFormError> addBlockToProgram(long programId)
      throws ProgramNotFoundException;

  /**
   * Adds an empty repeated {@link BlockDefinition} to the given program.
   *
   * @param programId the ID of the program to update
   * @param enumeratorBlockId ID of the enumerator block
   * @return the {@link ProgramDefinition} that was updated if succeeded, or a set of errors with
   *     the unmodified program definition if failed
   * @throws ProgramNotFoundException when programId does not correspond to a real Program.
   * @throws ProgramBlockDefinitionNotFoundException when enumeratorBlockId does not correspond to
   *     an enumerator block in the Program.
   */
  ErrorAnd<ProgramDefinition, CiviFormError> addRepeatedBlockToProgram(
      long programId, long enumeratorBlockId)
      throws ProgramNotFoundException, ProgramBlockDefinitionNotFoundException;

  /**
   * Update a {@link BlockDefinition}'s attributes.
   *
   * @param programId the ID of the program to update
   * @param blockDefinitionId the ID of the block to update
   * @param blockForm a {@link BlockForm} object containing the new attributes for the block
   * @return the {@link ProgramDefinition} that was updated if succeeded, or a set of errors with
   *     the unmodified program definition if failed
   * @throws ProgramNotFoundException when programId does not correspond to a real Program.
   * @throws ProgramBlockDefinitionNotFoundException when blockDefinitionId does not correspond to a
   *     real Block.
   */
  ErrorAnd<ProgramDefinition, CiviFormError> updateBlock(
      long programId, long blockDefinitionId, BlockForm blockForm)
      throws ProgramNotFoundException, ProgramBlockDefinitionNotFoundException;

  /**
   * Update a {@link BlockDefinition} with a set of questions.
   *
   * @param programId the ID of the program to update
   * @param blockDefinitionId the ID of the block to update
   * @param programQuestionDefinitions an {@link ImmutableList} of questions for the block
   * @return the updated {@link ProgramDefinition}
   * @throws ProgramNotFoundException when programId does not correspond to a real Program.
   * @throws ProgramBlockDefinitionNotFoundException when blockDefinitionId does not correspond to a
   *     real Block.
   */
  ProgramDefinition setBlockQuestions(
      long programId,
      long blockDefinitionId,
      ImmutableList<ProgramQuestionDefinition> programQuestionDefinitions)
      throws ProgramNotFoundException, ProgramBlockDefinitionNotFoundException;

  /**
   * Update a {@link BlockDefinition} to include additional questions.
   *
   * @param programId the ID of the program to update
   * @param blockDefinitionId the ID of the block to update
   * @param questionIds an {@link ImmutableList} of question IDs for the block
   * @return the updated {@link ProgramDefinition}
   * @throws ProgramNotFoundException when programId does not correspond to a real Program.
   * @throws ProgramBlockDefinitionNotFoundException when blockDefinitionId does not correspond to a
   *     real Block.
   * @throws QuestionNotFoundException when questionIds does not correspond to real Questions.
   * @throws DuplicateProgramQuestionException if the block already contains any of the Questions.
   */
  ProgramDefinition addQuestionsToBlock(
      long programId, long blockDefinitionId, ImmutableList<Long> questionIds)
      throws ProgramNotFoundException, ProgramBlockDefinitionNotFoundException,
          QuestionNotFoundException, DuplicateProgramQuestionException;

  /**
   * Update a {@link BlockDefinition} to remove questions.
   *
   * @param programId the ID of the program to update
   * @param blockDefinitionId the ID of the block to update
   * @param questionIds an {@link ImmutableList} of question IDs to be removed from the block
   * @return the updated {@link ProgramDefinition}
   * @throws ProgramNotFoundException when programId does not correspond to a real Program.
   * @throws ProgramBlockDefinitionNotFoundException when blockDefinitionId does not correspond to a
   *     real Block.
   * @throws QuestionNotFoundException when questionIds does not correspond to real Questions.
   */
  ProgramDefinition removeQuestionsFromBlock(
      long programId, long blockDefinitionId, ImmutableList<Long> questionIds)
      throws ProgramNotFoundException, ProgramBlockDefinitionNotFoundException,
          QuestionNotFoundException;

  /**
   * Set the hide {@link Predicate} for a block. This predicate describes under what conditions the
   * block should be hidden from an applicant filling out the program form.
   *
   * @param programId the ID of the program to update
   * @param blockDefinitionId the ID of the block to update
   * @param predicate the {@link Predicate} for hiding the block
   * @return the updated {@link ProgramDefinition}
   * @throws ProgramNotFoundException when programId does not correspond to a real Program.
   * @throws ProgramBlockDefinitionNotFoundException when blockDefinitionId does not correspond to a
   *     real Block.
   */
  ProgramDefinition setBlockHidePredicate(
      long programId, long blockDefinitionId, Predicate predicate)
      throws ProgramNotFoundException, ProgramBlockDefinitionNotFoundException;

  /**
   * Set the optional {@link Predicate} for a block. This predicate describes under what conditions
   * the block should be optional when filling out the program form.
   *
   * @param programId the ID of the program to update
   * @param blockDefinitionId the ID of the block to update
   * @param predicate the {@link Predicate} for making the block optional
   * @return the updated {@link ProgramDefinition}
   * @throws ProgramNotFoundException when programId does not correspond to a real Program.
   * @throws ProgramBlockDefinitionNotFoundException when blockDefinitionId does not correspond to a
   *     real Block.
   */
  ProgramDefinition setBlockOptionalPredicate(
      long programId, long blockDefinitionId, Predicate predicate)
      throws ProgramNotFoundException, ProgramBlockDefinitionNotFoundException;

  /**
   * Delete a block from a program if the block ID is present. Otherwise, does nothing.
   *
   * @return the updated {@link ProgramDefinition}
   * @throws ProgramNotFoundException when programId does not correspond to a real Program.
   * @throws ProgramNeedsABlockException when trying to delete the last block of a Program.
   */
  ProgramDefinition deleteBlock(long programId, long blockDefinitionId)
      throws ProgramNotFoundException, ProgramNeedsABlockException;

  /**
   * Get all the program's applications.
   *
   * @param programId the program id.
   * @return A list of Application objects for the specified program.
   * @throws ProgramNotFoundException when programId does not correspond to a real Program.
   */
  ImmutableList<Application> getProgramApplications(long programId) throws ProgramNotFoundException;

  /** Create a new draft starting from the program specified by `id`. */
  ProgramDefinition newDraftOf(long id) throws ProgramNotFoundException;

  /**
   * Get the email addresses to send a notification to - the program admins if there are any, or the
   * global admins if none.
   */
  ImmutableList<String> getNotificationEmailAddresses(String programName);
}
