scalaVersion := "2.11.0"

name := "MagicResize"

version := "1.0"

resolvers += "stephenjudkins-bintray" at "http://dl.bintray.com/stephenjudkins/maven"

libraryDependencies += "ps.tricerato" %% "pureimage" % "0.1.1-michael"

libraryDependencies += "commons-io" % "commons-io" % "2.1"
