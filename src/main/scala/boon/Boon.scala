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
    Test(
      name,
      NonEmptySeq.nes(Assertion(name, {
        val (a1, a2) = gen
        testable[A](a1, a2)
      }))
    )

  def defineAssertion[A](name: String, gen: => (A, A))(implicit E: Equality[A], D: Difference[A]): Assertion =
    Assertion(name, {
      val (a1, a2) = gen
      testable[A](a1, a2)
    })


  def runAssertion(assertion: Assertion): AssertionResult = {
    val testable = assertion.testable
    val value1 = testable.value1
    val value2 = testable.value2
    if (testable.equality.eql(value1, value2)) AssertionSuccess(AssertionResult.Success(AssertionResult.AssertionName(assertion.name)))
    else AssertionFailure(AssertionResult.Failure(AssertionResult.AssertionName(assertion.name), testable.difference.diff(value1, value2)))
  }

  def runTest(test: Test): TestResult = test match {
    case Test(name, assertions) =>
      val assertionResults = assertions.map(runAssertion)
      val (passed, failed) = partitionWith[AssertionResult, AssertionResult.Success, AssertionResult.Failure](assertionResults
        .toSeq,
        { case AssertionSuccess(value) => value},
        { case AssertionFailure(value) => value })

      (passed, failed) match {
        // case (Seq(), Seq()) => ???//this should never happen! :P Figure out a way to not check for this
        case (x +: xs, Seq()) =>   TestSuccess(TestResult.Success(TestResult.TestName(test.name), NonEmptySeq[AssertionResult.Success](x, xs)))
        case (Seq(), y +: ys) =>   TestFailure(TestResult.Failure(TestResult.TestName(test.name), NonEmptySeq[AssertionResult.Failure](y, ys)))
        case (x +: xs, y +: ys) => TestMixed(TestResult.Mixed(TestResult.TestName(test.name), NonEmptySeq[AssertionResult.Success](x, xs), NonEmptySeq[AssertionResult.Failure](y, ys)))
      }
  }

  private def partitionWith[A, S, F](xs: Seq[A], pfs: PartialFunction[A, S], pff: PartialFunction[A, F]): (Seq[S], Seq[F]) = {
    xs.foldLeft((Seq.empty[S], Seq.empty[F])){(acc, v) =>
      //refactor out `isDefined`
      if (pfs.isDefinedAt(v)) (acc._1 :+ pfs(v), acc._2)
      else if (pff.isDefinedAt(v)) (acc._1, acc._2 :+ pff(v))
      else acc
    }
  }

  //can we get this to return a These? \$/
  private def partitionWith3[A, S, F, M](xs: Seq[A], pfs: PartialFunction[A, S], pff: PartialFunction[A, F], pfm: PartialFunction[A, M]): (Seq[S], Seq[F], Seq[M]) = {
    xs.foldLeft((Seq.empty[S], Seq.empty[F], Seq.empty[M])){(acc, v) =>
      //refactor out `isDefined`
      if (pfs.isDefinedAt(v)) (acc._1 :+ pfs(v), acc._2, acc._3)
      else if (pff.isDefinedAt(v)) (acc._1, acc._2 :+ pff(v), acc._3)
      else if (pfm.isDefinedAt(v)) (acc._1, acc._2, acc._3 :+ pfm(v))
      else acc
    }
  }

  def runSuiteLike(suiteLike: SuiteLike): SuiteResult = runSuite(suiteLike.suite)

  def runSuite(suite: Suite): SuiteResult = {
    val testResults = suite.tests.map(runTest).toSeq
    val (passed, failed, mixed) = partitionWith3[TestResult, TestResult.Success, TestResult.Failure, TestResult.Mixed](testResults,
      { case TestSuccess(success) => success },
      { case TestFailure(failures) => failures },
      { case TestMixed(mixed) => mixed })

    (passed, failed, mixed) match {
      // case (Seq(), Seq(), Seq()) => ???//should never happen
      case (x +: xs, Seq(), Seq()) => SuiteResult.Success(SuiteResult.SuiteName(suite.name), NonEmptySeq[TestResult.Success](x, xs))
      case (Seq(), y +: ys, Seq()) => SuiteResult.Failure(SuiteResult.SuiteName(suite.name), NonEmptySeq[TestResult.Failure](y, ys))
      case (Seq(), Seq(), z +: zs) => SuiteResult.Mixed(SuiteResult.SuiteName(suite.name), NonEmptySeq[TestResult.Mixed](z, zs))
    }


    // val (passed, failed) = partitionWith[AssertionResult, AssertionResult.Success, AssertionResult.Failure](results,
    //   { case AssertionSuccess(success) => success },
    //   { case AssertionFailure(failure) => failure })

    // (passed, failed) match {
    //   case (Seq(), Seq()) => NoTests(suite.name)
    //   case (xs, Seq())    => AllPassed(suite.name, xs)
    //   case (Seq(), ys)    => AllFailed(suite.name, ys)
    //   case (xs, ys)       => SomePassed(suite.name, xs, ys)
    // }
  }
}

