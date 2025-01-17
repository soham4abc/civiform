import { AdminPrograms, AdminQuestions, AdminTranslations, ApplicantQuestions, endSession, loginAsAdmin, loginAsGuest, logout, selectApplicantLanguage, startSession } from './support'

describe('Admin can manage translations', () => {
  it('create a program and add translation', async () => {
    const { browser, page } = await startSession();

    await loginAsAdmin(page);
    const adminPrograms = new AdminPrograms(page);

    const programName = 'program to be translated';
    await adminPrograms.addProgram(programName);

    // Go to manage translations page.
    await adminPrograms.gotoDraftProgramManageTranslationsPage(programName);
    const adminTranslations = new AdminTranslations(page);

    // Add translations for Spanish and publish
    await adminTranslations.selectLanguage('Spanish');
    const publicName = 'Spanish name';
    await adminTranslations.editProgramTranslations(publicName, 'Spanish description');
    await adminPrograms.publishProgram(programName);

    // View the applicant program page in Spanish and check that the translations are present
    await logout(page);
    await loginAsGuest(page);
    await selectApplicantLanguage(page, 'Español');
    const cardText = await page.innerText('.cf-application-card:has-text("' + publicName + '")');
    expect(cardText).toContain('Spanish name');
    expect(cardText).toContain('Spanish description');

    await endSession(browser);
  });

  it('create a question and add translations', async () => {
    const { browser, page } = await startSession();

    await loginAsAdmin(page);
    const adminQuestions = new AdminQuestions(page);

    // Add a new question to be translated
    const questionName = 'name-translated';
    await adminQuestions.addNameQuestion(questionName);

    // Go to the question translation page and add a translation for Spanish
    await adminQuestions.goToQuestionTranslationPage(questionName);
    const adminTranslations = new AdminTranslations(page);
    await adminTranslations.selectLanguage('Spanish');
    await adminTranslations.editQuestionTranslations('Spanish question text', 'Spanish help text');

    // Add the question to a program and publish
    const adminPrograms = new AdminPrograms(page);
    const programName = 'spanish question';
    await adminPrograms.addProgram(programName);
    await adminPrograms.editProgramBlock(programName, 'block', [questionName]);
    await adminPrograms.publishProgram(programName);
    await logout(page);

    // Log in as an applicant and view the translated question
    await loginAsGuest(page);
    await selectApplicantLanguage(page, 'Español');
    const applicantQuestions = new ApplicantQuestions(page);
    await applicantQuestions.validateHeader('es-US');

    await applicantQuestions.applyProgram(programName);

    expect(await page.innerText('.cf-applicant-question-text')).toContain('Spanish question text');
    expect(await page.innerText('.cf-applicant-question-help-text')).toContain('Spanish help text');
    await endSession(browser);
  });

  it('create a multi-option question and add translations for options', async () => {
    const { browser, page } = await startSession();

    await loginAsAdmin(page);
    const adminQuestions = new AdminQuestions(page);

    // Add a new question to be translated
    const questionName = 'multi-option-translated';
    await adminQuestions.addRadioButtonQuestion(questionName, ['one', 'two', 'three']);

    // Go to the question translation page and add a translation for Spanish
    await adminQuestions.goToQuestionTranslationPage(questionName);
    const adminTranslations = new AdminTranslations(page);
    await adminTranslations.selectLanguage('Spanish');
    await adminTranslations.editQuestionTranslations('hola', 'mundo', ['uno', 'dos', 'tres']);

    // Add the question to a program and publish
    const adminPrograms = new AdminPrograms(page);
    const programName = 'spanish question';
    await adminPrograms.addProgram(programName);
    await adminPrograms.editProgramBlock(programName, 'block', [questionName]);
    await adminPrograms.publishProgram(programName);
    await logout(page);

    // Log in as an applicant and view the translated question
    await loginAsGuest(page);
    await selectApplicantLanguage(page, 'Español');
    const applicantQuestions = new ApplicantQuestions(page);
    await applicantQuestions.applyProgram(programName);

    expect(await page.innerText('form')).toContain('uno');
    expect(await page.innerText('form')).toContain('dos');
    expect(await page.innerText('form')).toContain('tres');
    await endSession(browser);
  });

  it('create an enumerator question and add translations for entity type', async () => {
    const { browser, page } = await startSession();

    await loginAsAdmin(page);
    const adminQuestions = new AdminQuestions(page);

    // Add a new question to be translated
    const questionName = 'enumerator-translated';
    await adminQuestions.addEnumeratorQuestion(questionName);

    // Go to the question translation page and add a translation for Spanish
    await adminQuestions.goToQuestionTranslationPage(questionName);
    const adminTranslations = new AdminTranslations(page);
    await adminTranslations.selectLanguage('Spanish');
    await adminTranslations.editQuestionTranslations('test', 'enumerator', ['family member']);

    // Add the question to a program and publish
    const adminPrograms = new AdminPrograms(page);
    const programName = 'spanish question';
    await adminPrograms.addProgram(programName);
    await adminPrograms.editProgramBlock(programName, 'block', [questionName]);
    await adminPrograms.publishProgram(programName);
    await logout(page);

    // Log in as an applicant and view the translated question
    await loginAsGuest(page);
    await selectApplicantLanguage(page, 'Español');
    const applicantQuestions = new ApplicantQuestions(page);
    await applicantQuestions.applyProgram(programName);

    expect(await page.innerText('form')).toContain('family member');
    await endSession(browser);
  });

  it('updating a question does not clobber translations', async () => {
    const { browser, page } = await startSession();

    await loginAsAdmin(page);
    const adminQuestions = new AdminQuestions(page);

    // Add a new question.
    const questionName = 'translate-no-clobber';
    await adminQuestions.addNumberQuestion(questionName);

    // Add a translation for a non-English language.
    await adminQuestions.goToQuestionTranslationPage(questionName);
    const adminTranslations = new AdminTranslations(page);
    await adminTranslations.selectLanguage('Spanish');
    await adminTranslations.editQuestionTranslations('something different', 'help text different');

    // Edit the question again and update the question.
    await adminQuestions.updateQuestion(questionName);

    // View the question translations and check that the Spanish translations are still there.
    await adminQuestions.goToQuestionTranslationPage(questionName);
    await adminTranslations.selectLanguage('Spanish');
    expect(await page.getAttribute('#localize-question-text', 'value')).toContain('something different');
    await endSession(browser);
  });

  it('Applicant sees toast message warning translation is not complete', async () => {
    const { browser, page } = await startSession();

    // Add a new program with one non-translated question
    await loginAsAdmin(page);
    const adminPrograms = new AdminPrograms(page);
    const adminQuestions = new AdminQuestions(page);

    const programName = 'toast';
    await adminPrograms.addProgram(programName);

    await adminQuestions.addNameQuestion('name-english');
    await adminPrograms.editProgramBlock(programName, 'not translated', ['name-english']);

    await adminPrograms.publishProgram(programName);
    await logout(page);

    // Set applicant preferred language to Spanish
    // DO NOT LOG IN AS TEST USER. We want a fresh guest so we can guarantee
    // the language has not yet been set.
    await loginAsGuest(page);
    await selectApplicantLanguage(page, 'Español');
    const applicantQuestions = new ApplicantQuestions(page);
    await applicantQuestions.applyProgram(programName);

    // Check that a toast appears warning the program is not fully translated
    const toastMessages = await page.innerText('#toast-container')
    expect(toastMessages).toContain('Lo siento, este programa no está completamente traducido al español.');

    await endSession(browser);
  });
})
