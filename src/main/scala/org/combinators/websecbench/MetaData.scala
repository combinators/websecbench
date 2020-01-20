package org.combinators.websecbench

trait MetaData {
  def toReportElement(testNumber: String): String
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
}
