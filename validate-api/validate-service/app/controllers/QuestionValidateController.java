/** */
package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import utils.Attrs;

/**
 * This controller will handler all the request related to learner state.
 *
 * @author Manzarul
 */
public class QuestionValidateController extends Controller {



  /**
   * This method will update learner current state with last store state.
   *
   * @return Result
   */
  public Result assessment(Http.Request httpRequest) {
    JsonNode requestData = httpRequest.body().asJson();
    String loggingHeaders =  httpRequest.attrs().getOptional(Attrs.X_LOGGING_HEADERS).orElse(null);
    String requestedBy = httpRequest.attrs().getOptional(Attrs.USER_ID).orElse(null);
    String requestedFor = httpRequest.attrs().getOptional(Attrs.REQUESTED_FOR).orElse(null);
    String apiDebugLog = "UpdateContentState Request: " + requestData.toString() + " RequestedBy: " + requestedBy + " RequestedFor: " + requestedFor + " ";

    return Results.ok();
  }


}
