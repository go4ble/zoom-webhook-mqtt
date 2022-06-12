package controllers

import models.ZoomWebhookModel
import play.api.http.HeaderNames
import play.api.mvc._
import services.MqttService

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, mqttService: MqttService)(implicit ec: ExecutionContext)
  extends BaseController {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def webhookNotification(): Action[ZoomWebhookModel.Event] = Action.async(parse.json[ZoomWebhookModel.Event]) { request =>
    (for {
      verificationToken <- zoomAccountVerificationToken(request.body.payload.account_id)
      authorizationHeader <- request.headers.get(HeaderNames.AUTHORIZATION)
      if verificationToken == authorizationHeader
    } yield ()) match {
      case None =>
        Future.successful(Results.Unauthorized)
      case Some(()) =>
        mqttService.publish(request.body).map(_ => Results.Created)
    }
  }

  private def zoomAccountVerificationToken(accountId: String): Option[String] =
    sys.env.get(s"ZWM_${accountId}_VERIFICATION_TOKEN")
}
