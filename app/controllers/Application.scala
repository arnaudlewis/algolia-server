package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def reportByOrigin(origin: String, duration: Option[Int]) = Action.async {
    Future successful Ok(Json.obj("hello" -> "world"))
  }

  def reportProbeEvent() = Action.async {
    Future successful Ok
  }

}
