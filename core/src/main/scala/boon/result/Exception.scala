package boon.result

import scala.jdk.CollectionConverters._

final case class Trace(className: String, fileName: Option[String], methodName: String, lineNumber: Option[Int])

object Exception {

  def getTraces(ex: Throwable, depth: Int): Seq[Trace] = {
    val traces: Seq[StackTraceElement] = java.util.Arrays.asList(ex.getStackTrace:_*).asScala.toSeq

    traces.map { st =>
      Trace(st.getClassName, Option(st.getFileName), st.getMethodName, {
        val lineNumber = st.getLineNumber
        if (lineNumber < 0) None else Some(lineNumber)
      })
    } take(depth)
  }
}