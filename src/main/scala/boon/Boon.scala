package boon

object Boon {

  def testable[A](a1: A, a2: A)(implicit E: Equality[A], D: Difference[A]): Testable = new Testable {
    type Actual = A
    val value1: Actual = a1
    val value2: Actual = a2
    val equality = E
    val difference = D
  }

  def defineTest[A](name: String, gen: => (A, A))(implicit E: Equality[A], D: Difference[A]): Test =
    //TODO: Can we do without the duplicate 'name' in Test and Assertion
    SingleAssertionTest(
      name,
      Assertion(name, {
        val (a1, a2) = gen
        testable[A](a1, a2)
      })
    )

  def defineAssertion[A](name: String, gen: => (A, A))(implicit E: Equality[A], D: Difference[A]): Assertion =
    Assertion(name, {
      val (a1, a2) = gen
      testable[A](a1, a2)
    })


  //TODO: We need something like 'SuiteResult' here as well.
  //Try and unify it to get a common result type
  def runTest(test: Test): TestResult = test match {
      case SingleAssertionTest(name, assertion) => runAssertion(assertion)
      case MultiAssertionTest(name, assertions) => assertions.map(runAssertion)
  }

  def runAssertion(assertion: Assertion): TestResult = {
    val testable = assertion.testable
    val value1 = testable.value1
    val value2 = testable.value2
    if (testable.equality.eql(value1, value2)) TestSuccess(TestResult.Success(assertion.name))
    else TestFailure(TestResult.Failure(assertion.name, testable.difference.diff(value1, value2)))
  }

  private def partitionWith[A, S, F](xs: Seq[A], pfs: PartialFunction[A, S], pff: PartialFunction[A, F]): (Seq[S], Seq[F]) = {
    xs.foldLeft((Seq.empty[S], Seq.empty[F])){(acc, v) =>
      //refactor out `isDefined`
      if (pfs.isDefinedAt(v)) (acc._1 :+ pfs(v), acc._2)
      else if (pff.isDefinedAt(v)) (acc._1, acc._2 :+ pff(v))
      else acc
    }
  }

  def runSuiteLike(suiteLike: SuiteLike): SuiteResult = runSuite(Suite(suiteLike.suiteName, suiteLike.allTests))

  def runSuite(suite: Suite): SuiteResult = {
    val tests = suite.tests
    val results = tests.map(runTest)

    val (passed, failed) = partitionWith[TestResult, TestResult.Success, TestResult.Failure](results,
      { case TestSuccess(success) => success },
      { case TestFailure(failure) => failure })

    (passed, failed) match {
      case (Seq(), Seq()) => NoTests(suite.name)
      case (xs, Seq())    => AllPassed(suite.name, xs)
      case (Seq(), ys)    => AllFailed(suite.name, ys)
      case (xs, ys)       => SomePassed(suite.name, xs, ys)
    }
  }
}

