package org.sunbird.actors

import org.sunbird.actor.core.BaseActor
import org.sunbird.common.dto.{Request, Response, ResponseHandler}
import org.sunbird.graph.OntologyEngineContext
import org.sunbird.managers.AssessmentManager

import java.util
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import org.sunbird.utils.AssessmentConstants

class QuestionSetAssessActor @Inject()(implicit oec: OntologyEngineContext) extends BaseActor {

  implicit val ec: ExecutionContext = getContext().dispatcher

  @throws[Throwable]
  override def onReceive(request: Request): Future[Response] = request.getOperation match {
    case "assessQuestionSet" => assess(request)
    case _ => ERROR(request.getOperation)
  }

  private def assess(req: Request): Future[Response] = {
    val assessments = req.getRequest.getOrDefault(AssessmentConstants.ASSESSMENTS, new util.ArrayList[util.Map[String, AnyRef]]).asInstanceOf[util.List[util.Map[String, AnyRef]]]
    val quesDoIds = AssessmentManager.validateAssessRequest(req)
    val list: Response = AssessmentManager.questionList(quesDoIds)
    AssessmentManager.calculateScore(list, assessments)
    Future(ResponseHandler.OK.put(AssessmentConstants.QUESTIONS, req.getRequest))
  }
}
