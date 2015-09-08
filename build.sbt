name := "Mailer"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies  ++= Seq(
  "javax.mail" % "mail" % "1.5.0-b01",
  "commons-cli" % "commons-cli" % "1.3.1",
  "org.apache.commons" % "commons-csv" % "1.2",
  "org.yaml" % "snakeyaml" % "1.16"
)

mainClass := Some("main.Mailer")   
target in assembly := file("target")
assemblyJarName in assembly := "mailer.jar"
mainClass in assembly := Some("main.Mailer")    