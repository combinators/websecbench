package org.combinators.websecbench

trait MetaData {
  def toReportElement(testNumber: String): String

  def getTaintSources: Seq[TaintSource]
  def makeSafe:MetaData
}

case class PathTraversalVulnerability(isVulnerable: Boolean) extends MetaData {
  def toReportElement(testNumber: String): String = {
    s"""
       |<test-metadata>
       |  <benchmark-version>1.2</benchmark-version>
       |  <category>pathtraver</category>
       |  <test-number>${testNumber}</test-number>
       |  <vulnerability>${isVulnerable}</vulnerability>
       |  <cwe>22</cwe>
       |</test-metadata>
       |""".stripMargin
  }

  override def getTaintSources = Seq(UncheckedString())

  override def makeSafe: MetaData = PathTraversalVulnerability(false)
}


trait TaintSource

case class UncheckedString() extends TaintSource
case class StaticString() extends TaintSource
