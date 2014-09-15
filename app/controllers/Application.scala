package controllers

import lib.TwitterScanner
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(
      s"""
         |indyref twitter feed running:
         | ${TwitterScanner.tweetsSeen.get} total tweets with #indyref seen
         | ${TwitterScanner.totalProcessed.get} submitted as either yes or no
         | """.stripMargin)
  }

  def healthCheck() = Action {
    val lastProcessed = TwitterScanner.lastSucessfullyProcessed.get()

    if (lastProcessed.plusMinutes(30).isAfterNow) {
      ServiceUnavailable("Last processed was " + lastProcessed)
    } else {
      Ok("So far, so good - last processed " + lastProcessed)
    }
  }
}