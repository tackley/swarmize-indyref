import twitter4j._

object Main extends App {

  val listener = new StatusListener() {
    override def onStatus(status: Status): Unit = {
      if (!status.isRetweet) {
        println()
        println(s"@${status.getUser.getScreenName}: ${status.getText}")
        println(s"HASHTAGS: ${status.getHashtagEntities.map(_.getText).mkString(", ")}")
        println(s"GEO: ${status.getGeoLocation}")
      }
    }

    override def onStallWarning(warning: StallWarning): Unit = {
      println("stall warning: " + warning)
    }

    override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {
      println("deletionNotice: " + statusDeletionNotice)
    }

    override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {
      println("onScrubGeo!")
    }

    override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {
      println("onTrackLimitationNotice: " + numberOfLimitedStatuses)
    }

    override def onException(ex: Exception): Unit = {
      println("onException: " + ex.toString)
    }
  }

  val twitterStream = new TwitterStreamFactory().getInstance()

  twitterStream.addListener(listener)

  // sample() method internally creates a thread which manipulates TwitterStream and
  // calls these adequate listener methods continuously.

  val fq = new FilterQuery().track(Array("#indyref"))
  twitterStream.filter(fq)

  Thread.sleep(10000000000L)

}

