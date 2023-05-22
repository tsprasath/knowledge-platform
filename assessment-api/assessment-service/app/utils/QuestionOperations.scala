package utils

object QuestionOperations extends Enumeration {
  val createQuestion, readQuestion, readPrivateQuestion, updateQuestion, reviewQuestion, publishQuestion, retireQuestion, importQuestion, systemUpdateQuestion, listQuestions, listQuestionsDetails, rejectQuestion, copyQuestion = Value
}
