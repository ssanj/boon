package boon

import syntax._
import result.SuiteOutput

object MissingImplementationSuite extends SuiteLike("MissingImplementationSuite") {

  private val t1 = test("can handle missing implementations") {

    %@({
      val so = MissingImplFixtures.run
      so.tests
    }) { tests =>
        tests.length =?= 1 | "no of tests" and %@(tests.head) { first =>
          first.fold({ (name, t1assertions, _) =>
            name =?= "test for missing implementations" | "test name" and
            t1assertions.length =?= 3 | "no of assertions" and %@(t1assertions.toSeq(0)) { t1a1 =>
              t1a1.fold({
                case (name, error, _, loc) =>
                  pass | "assertionOutput type" and
                  name =?= "Boolean test" | "assertion name" and
                  error =?= "an implementation is missing" | "assertion error" and
                  SuiteOutput.sourceLocation(loc).fold(
                    fail("expected SourceLocation") | "error location"
                  )(loc => loc.endsWith("ToBeImplementedSuite.scala:14") |# ("error location", "loc" -> loc))
              }, name            => fail(s"passed: $name") | "assertionOutput type",
                 (name, _)       => fail(s"Sequential passed: $name") | "assertionOutput type",
                 (name, _, _, _) => fail(s"Sequential failed: $name") | "assertionOutput type"
              )
            }
          },
            (name, error, trace, loc) => fail(s"thrown test: $name") | "test type",
            name => fail(s"ignored test: $name") | "testType")
        }
    } seq()
  }

  override val tests = NonEmptySeq.nes(t1)
}

object MissingImplFixtures {

  def run: SuiteOutput = SuiteOutput.toSuiteOutput(Boon.runSuiteLike(new ToBeImplementedSuite{}))
}

