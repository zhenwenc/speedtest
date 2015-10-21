name := "speedtest"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  lazy val AkkaV     = "2.3.11"
  lazy val AkkaHttpV = "1.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor"                        % AkkaV,
    "com.typesafe.akka" %% "akka-stream-experimental"          % AkkaHttpV,
    "com.typesafe.akka" %% "akka-http-core-experimental"       % AkkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % AkkaHttpV,
    "com.typesafe.akka" %% "akka-slf4j"                        % AkkaV,
    "org.slf4j"          %  "slf4j-api"                        % "1.7.12",
    "org.slf4j"          %  "slf4j-simple"                     % "1.7.12",
    "com.google.guava"  %  "guava"                             % "18.0",
    "joda-time"         %  "joda-time"                         % "2.4",
    "org.joda"          %  "joda-convert"                      % "1.2",
    "org.scalatest"     %% "scalatest"                         % "2.2.4" % "test",
    "org.mockito"       %  "mockito-all"                       % "1.9.5" % "test",
    "com.typesafe.akka" %% "akka-testkit"                      % AkkaV   % "test"
  )
}
