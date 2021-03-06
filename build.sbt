/*
 * Copyright 2017 Daniel Spiewak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

baseVersion in ThisBuild := "0.4"

bintrayVcsUrl in ThisBuild := Some("git@github.com:djspiewak/parseback.git")

addCommandAlias("measure-all", "benchmarks/jmh:run -rff results.csv")
addCommandAlias("measure", "benchmarks/jmh:run -rff results.csv .*parsebackRun")
addCommandAlias("profile", "benchmarks/jmh:run -prof jmh.extras.JFR -f 1 .*parsebackRun")

lazy val root = project
  .in(file("."))
  .settings(name := "root")
  .settings(noPublishSettings)
  .aggregate(benchmarks, coreJVM, coreJS)

lazy val benchmarks = project
  .in(file("benchmarks"))
  .dependsOn(coreJVM)
  .settings(name := "parseback-benchmarks")
  .settings(
    libraryDependencies ++= Seq(
      "com.codecommit"         %% "gll-combinators"          % "2.3",
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.5"),

    sourceDirectory in Jmh := (sourceDirectory in Compile).value)
  .settings(noPublishSettings)
  .enablePlugins(JmhPlugin)

lazy val core = crossProject
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "parseback")
  .settings(
    libraryDependencies += "org.typelevel" %%% "cats-core" % "1.1.0",
    libraryDependencies ++= Seq(
      "org.scalacheck" %% "scalacheck"        % "1.13.4"       % Test,
      "org.specs2"     %% "specs2-core"       % Versions.Specs % Test,
      "org.specs2"     %% "specs2-scalacheck" % Versions.Specs % Test),
    initialCommands := "import parseback._",
    logBuffered in Test := false)

lazy val coreJS = core.js
lazy val coreJVM = core.jvm
