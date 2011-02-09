import sbt._

class GuardianTwitterStatsProject(info:ProjectInfo) extends DefaultProject(info) {
  val guardianGithub = "Guardian Github Releases" at "http://guardian.github.com/maven/repo-releases"

  val twitter4J = "org.twitter4j" % "twitter4j-core" % "2.1.10" withSources()
  val contentApiClient = "com.gu.openplatform" %% "content-api-client" % "1.9" withSources ()
  val liftJson = "net.liftweb" %% "lift-json" % "2.1" withSources ()
  val openCsv = "net.sf.opencsv" % "opencsv" % "2.1" withSources ()

  override def mainClass = Some("com.gu.twitterstats.TwitterStats")
}