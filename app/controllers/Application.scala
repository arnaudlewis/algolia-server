package controllers

import javax.inject._
import play.api._
import play.api.Logger
import play.api.mvc._
import play.api.libs.json.{Json,JsValue}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import services.db.ProbeEventRepo
import models.ProbeEvent

@Singleton
class Application @Inject() (val probeEventRepo: ProbeEventRepo) extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def reportByOrigin(origin: String, duration: Option[Int]) = Action.async {
    Future successful Ok(Json.obj("hello" -> "world"))
  }

  def reportProbeEvent() = Action.async(parse.json) { implicit request =>
    ProbeEvent.create(request.body) match {
      case Some(probeEvent) =>
        probeEventRepo.insert(probeEvent).map { _ =>
          Ok
        }.recover {
          case e : Exception =>
            Logger.error(e.getMessage)
            BadRequest("Unable to insert Probe event")
        }
      case None => Future successful BadRequest(s"Unable to create ProbeEvent from ${request.body}")
    }
  }

}
