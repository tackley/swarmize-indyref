import lib.TwitterScanner
import play.Logger
import play.api.{Application, GlobalSettings}

object Global extends GlobalSettings {
  override def onStart(app: Application): Unit = {
    Logger.info("starting!")
    TwitterScanner.start()
  }

  override def onStop(app: Application): Unit = {
    Logger.info("stopping!")
    TwitterScanner.stop()
  }
}
