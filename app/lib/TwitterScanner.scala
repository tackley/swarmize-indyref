package lib

import java.util.concurrent.atomic.{AtomicLong, AtomicReference}

import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.ws.WS
import twitter4j._

object TwitterScanner {
  import play.api.Play.current
  import scala.concurrent.ExecutionContext.Implicits.global

  private val log = org.slf4j.LoggerFactory.getLogger(getClass)

  val lastSucessfullyProcessed = new AtomicReference[DateTime](DateTime.now)
  val totalProcessed = new AtomicLong()
  val tweetsSeen = new AtomicLong()

  val yesTags = Set("yes", "voteyes", "yesplease")
  val noTags = Set("no", "voteno", "nothanks", "labourno")

  object listener extends StatusListener {
    override def onStatus(status: Status): Unit = {
      if (!status.isRetweet) {
        tweetsSeen.incrementAndGet()

        log.info(s"@${status.getUser.getScreenName}: ${status.getText}")
        log.info(s"HASHTAGS: ${status.getHashtagEntities.map(_.getText).mkString(", ")}")

        val hashTags = status.getHashtagEntities.toList.map(_.getText.toLowerCase).toSet

        val isYes = hashTags.intersect(yesTags).nonEmpty
        val isNo = hashTags.intersect(noTags).nonEmpty

        log.info(s"isYes = $isYes isNo = $isNo")

        if ( isYes && isNo ) {
          // ignore - probably just an instruction but certainly very undecided!
        } else if ( !isYes && !isNo ) {
          // ignore - no opinion!
        } else {
          val geoAsGeoJson = Option(status.getGeoLocation).map(g => Array(g.getLongitude, g.getLatitude))

          val obj = Json.obj(
            "are_you_for_an_independent_scotland" -> isYes,
            "screen_name" -> status.getUser.getScreenName,
            "full_text_of_tweet" -> status.getText,
            "where_was_your_tweet_from" -> geoAsGeoJson
          )

          log.info("posting " + obj)

          WS.url("http://collector.swarmize.com/swarms/jtjsvwdc")
            .post(obj)
            .map { r =>
              log.info(r.status + " " + r.statusText)
              if (r.status != 200)
                log.info(r.body)
              else {
                lastSucessfullyProcessed.set(DateTime.now)
                totalProcessed.incrementAndGet()
              }
            }
        }
      }
    }

    override def onStallWarning(warning: StallWarning): Unit = {
      log.warn("stall warning: " + warning)
    }

    override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {
      log.warn("deletionNotice: " + statusDeletionNotice)
    }

    override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {
      log.warn("onScrubGeo!")
    }

    override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {
      log.warn("onTrackLimitationNotice: " + numberOfLimitedStatuses)
    }

    override def onException(ex: Exception): Unit = {
      log.error("onException: " + ex.toString)
    }
  }

  val twitterStream = new TwitterStreamFactory().getInstance()

  def start(): Unit = {
    twitterStream.addListener(listener)

    val fq = new FilterQuery().track(Array("#indyref"))
    twitterStream.filter(fq)
  }

  def stop(): Unit = {
    twitterStream.shutdown()
  }

}
