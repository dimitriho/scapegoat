import sbt.Keys._

name := "scalac-scapegoat-plugin"

organization := "com.sksamuel.scapegoat"

scalaVersion := "2.12.0"

crossScalaVersions := Seq("2.11.8", "2.12.0")

scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8", "-Xmax-classfile-name", "254")

publishMavenStyle := true

fullClasspath in console in Compile ++= (fullClasspath in Test).value // because that's where "PluginRunner" is

initialCommands in console := s"""
import com.sksamuel.scapegoat._
def check(code: String) = {
  val runner = new PluginRunner { val inspections = ScapegoatConfig.inspections }
  // Not sufficient for reuse, not sure why.
  // runner.reporter.reset
  val c = runner compileCodeSnippet code
  val feedback = c.scapegoat.feedback
  feedback.warnings map (x => "%-40s  %s".format(x.text, x.snippet getOrElse "")) foreach println
  feedback
}
"""

scalacOptions ++= Seq(
  "-Xlint",
  "-Ywarn-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Ywarn-numeric-widen"
  //"-Ywarn-value-discard"
)
  
javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

libraryDependencies ++= Seq(
  "org.scala-lang"                  %     "scala-reflect"         % scalaVersion.value,
  "org.scala-lang"                  %     "scala-compiler"        % scalaVersion.value      % "provided",
  "org.scala-lang.modules"          %%    "scala-xml"             % "1.0.5",
  "org.scala-lang"                  %     "scala-compiler"        % scalaVersion.value      % "test",
  "commons-io"                      %     "commons-io"            % "2.4"                   % "test",
  "org.scalatest"                   %%    "scalatest"             % "3.0.0"                 % "test",
  "org.mockito"                     %     "mockito-all"           % "1.9.5"                 % "test",
  "joda-time"                       %     "joda-time"             % "2.3"                   % "test",
  "org.joda"                        %     "joda-convert"          % "1.3.1"                 % "test",
  "org.slf4j"                       %     "slf4j-api"             % "1.7.7"                 % "test"
)

sbtrelease.ReleasePlugin.autoImport.releasePublishArtifactsAction := PgpKeys.publishSigned.value

sbtrelease.ReleasePlugin.autoImport.releaseCrossBuild := true

publishTo <<= version {
  (v: String) =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

parallelExecution in Test := false

pomIncludeRepository := {
  _ => false
}

pomExtra := {
  <url>https://github.com/sksamuel/scapegoat</url>
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:sksamuel/scapegoat.git</url>
      <connection>scm:git@github.com:sksamuel/scapegoat.git</connection>
    </scm>
    <developers>
      <developer>
        <id>sksamuel</id>
        <name>sksamuel</name>
        <url>http://github.com/sksamuel</url>
      </developer>
    </developers>
}
