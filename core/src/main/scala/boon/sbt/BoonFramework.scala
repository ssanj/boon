package boon.sbt

import sbt.testing.Fingerprint
import sbt.testing.Framework
import sbt.testing.Runner
import sbt.testing.SubclassFingerprint

final class BoonFramework extends Framework {

  def name(): String = "Boon"

  def fingerprints(): Array[Fingerprint] = Array(BoonFramework.fingerprint)

  override def runner(args: Array[String], remoteArgs: Array[String], testClassLoader: ClassLoader): Runner = {
    new BoonRunner(args, remoteArgs, testClassLoader)
  }
}

object BoonFramework {

  val fingerprint: Fingerprint = new SubclassFingerprint {

    val isModule = true

    def requireNoArgConstructor(): Boolean = true

    def superclassName(): String = "boon.SuiteLike"
  }
}