package controllers

import models.{ZoomAccountsConfigurationModel, ZoomWebhookModel}
import play.api.Configuration
import play.api.http.HeaderNames
import play.api.mvc._

import javax.inject._
import scala.concurrent.Future

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, config: Configuration) extends BaseController {

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
    val accountId = request.body.payload.account_id
    (for {
      accountConfig <- zoomAccountConfigurations.get(accountId)
      authorizationHeader <- request.headers.get(HeaderNames.AUTHORIZATION)
      if accountConfig.verificationToken == authorizationHeader
    } yield ()) match {
      case None =>
        Future.successful(Results.Unauthorized)
      case Some(()) =>
        Future.successful(Results.NotImplemented)
    }
  }

  private lazy val zoomAccountConfigurations = config.get[ZoomAccountsConfigurationModel.Config](ZoomAccountsConfigurationModel.ConfigKey)
}
