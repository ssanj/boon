package com.example.picotest

import scala.language.implicitConversions

object PicoTest1 {

  trait Testable {
    type Actual
    val value1: Actual
    val value2: Actual
    val equality: Equality[Actual]
    val difference: Difference[Actual]
  }

  trait Equality[A] {
    def eql(a1: A, a2: A): Boolean
  }

  final case class Test(name: String, testable: Testable)
  final case class Suite(name: String, tests: Seq[Test])

  object Equality {

    def apply[A: Equality]: Equality[A] = implicitly[Equality[A]]

    implicit object IntEquality extends Equality[Int] {
      override def eql(a1: Int, a2: Int): Boolean = a1 == a2
    }

    implicit object LongEquality extends Equality[Long] {
      override def eql(a1: Long, a2: Long): Boolean = a1 == a2
    }

    implicit object BooleanEquality extends Equality[Boolean] {
      override def eql(a1: Boolean, a2: Boolean): Boolean = a1 == a2
    }

    implicit object StringEquality extends Equality[String] {
      override def eql(a1: String, a2: String): Boolean = a1 == a2
    }

    implicit object FloatEquality extends Equality[Float] {
      override def eql(a1: Float, a2: Float): Boolean = a1 == a2
    }

    implicit object DoubleEquality extends Equality[Double] {
      override def eql(a1: Double, a2: Double): Boolean = a1 == a2
    }

    implicit object CharEquality extends Equality[Char] {
      override def eql(a1: Char, a2: Char): Boolean = a1 == a2
    }

    implicit def listEquality[A](implicit E: Equality[A]): Equality[List[A]] = new Equality[List[A]] {
      override def eql(xs: List[A], ys: List[A]): Boolean = (xs, ys) match {
        case (_ :: _, Nil) => false
        case (Nil, _ :: _) => false
        case _ => xs.zip(ys).forall(p => E.eql(p._1, p._2))
      }
    }
  }

  trait StringRep[A] {
    def strRep(a: A): String
  }

  object StringRep {

    def apply[A: StringRep]: StringRep[A] = implicitly[StringRep[A]]

    private def genericStringRep[A]: StringRep[A] = new StringRep[A] {
      override def strRep(a: A): String = a.toString
    }

    implicit object IntStringRep extends StringRep[Int] {
      override def strRep(a: Int): String = genericStringRep[Int].strRep(a)
    }

    implicit object LongStringRep extends StringRep[Long] {
      override def strRep(a: Long): String = genericStringRep[Long].strRep(a)
    }

    implicit object FloatStringRep extends StringRep[Float] {
      override def strRep(a: Float): String = genericStringRep[Float].strRep(a)
    }

    implicit object DoubleStringRep extends StringRep[Double] {
      override def strRep(a: Double): String = genericStringRep[Double].strRep(a)
    }

    implicit object BooleanStringRep extends StringRep[Boolean] {
      override def strRep(a: Boolean): String = genericStringRep[Boolean].strRep(a)
    }

    implicit object StringStringRep extends StringRep[String] {
      override def strRep(a: String): String = s""""$a""""
    }

    implicit object CharStringRep extends StringRep[Char] {
      override def strRep(a: Char): String = s"'$a'"
    }

    implicit def listStringRep[A](implicit S: StringRep[A]): StringRep[List[A]] = new StringRep[List[A]] {
      override def strRep(xs: List[A]): String = xs.map(S.strRep).mkString("[", ",", "]")
    }
  }

  trait Difference[A] {
    def diff(a1: A, a2: A): String
  }

  object Difference {

    def apply[A: Difference]: Difference[A] = implicitly[Difference[A]]

    private def genericDifference[A](implicit rep: StringRep[A]): Difference[A] = new Difference[A] {
      def diff(a1: A, a2: A): String = s"${rep.strRep(a1)} != ${rep.strRep(a2)}"
    }

    implicit object IntDifference extends Difference[Int] {
      def diff(a1: Int, a2: Int): String = genericDifference[Int].diff(a1, a2)
    }

    implicit object LongDifference extends Difference[Long] {
      def diff(a1: Long, a2: Long): String = genericDifference[Long].diff(a1, a2)
    }

    implicit object FloatDifference extends Difference[Float] {
      def diff(a1: Float, a2: Float): String = genericDifference[Float].diff(a1, a2)
    }

    implicit object DoubleDifference extends Difference[Double] {
      def diff(a1: Double, a2: Double): String = genericDifference[Double].diff(a1, a2)
    }

    implicit object BooleanDifference extends Difference[Boolean] {
      val rep = StringRep[Boolean]
      def diff(a1: Boolean, a2: Boolean): String = s"${rep.strRep(a1)} is not ${rep.strRep(a2)}"
    }

    implicit object StringDifference extends Difference[String] {
      val rep = StringRep[String]
      def diff(a1: String, a2: String): String = s"${rep.strRep(a1)} != ${rep.strRep(a2)}"
    }

    implicit object CharDifference extends Difference[Char] {
      val rep = StringRep[Char]
      def diff(a1: Char, a2: Char): String = s"${rep.strRep(a1)} != ${rep.strRep(a2)}"
    }

    implicit def listDifference[A: Difference : StringRep]: Difference[List[A]] = new Difference[List[A]] {
      val rep = StringRep[List[A]]
      def diff(xs: List[A], ys: List[A]): String = s"${rep.strRep(xs)} != ${rep.strRep(ys)}"
    }
  }

  def testable[A](a1: A, a2: A)(implicit E: Equality[A], D: Difference[A]): Testable = new Testable {
    type Actual = A
    val value1: Actual = a1
    val value2: Actual = a2
    val equality = E
    val difference = D
  }

  final case class Success(name: String)
  final case class Failure(name: String, error: String)

  sealed trait TestResult

  object TestResult {
    def isSuccess(tr: TestResult): Boolean = tr match {
      case ts: TestSuccess => true
      case tf: TestFailure => false
    }

    def isFailure(tr: TestResult): Boolean = !isSuccess(tr)
  }

  final case class TestSuccess(value: Success) extends TestResult
  final case class TestFailure(value: Failure) extends TestResult

  sealed trait SuiteResult
  //Should these be NonEmptyLists?
  final case class AllPassed(name: String, passed: Seq[Success]) extends SuiteResult
  final case class SomePassed(name: String, passed: Seq[Success], failed: Seq[Failure]) extends SuiteResult
  final case class AllFailed(name: String, failed: Seq[Failure]) extends SuiteResult
  final case class NoTests(name: String) extends SuiteResult

  // def test[A](name: String, a1: A, a2: A)(implicit E: Equality[A], D: Difference[A]): Test = Test(name, testable[A](a1, a2))

  def defineTest[A](name: String, gen: => (A, A))(implicit E: Equality[A], D: Difference[A]): Test =
    Test(name, {
      val (a1, a2) = gen
      testable[A](a1, a2)
    })


  def runTest(test: Test): TestResult = {
      val testable = test.testable
      val value1 = testable.value1
      val value2 = testable.value2
      if (testable.equality.eql(value1, value2)) TestSuccess(Success(test.name))
      else TestFailure(Failure(test.name, testable.difference.diff(value1, value2)))
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

    val (passed, failed) = partitionWith[TestResult, Success, Failure](results,
      { case TestSuccess(success) => success },
      { case TestFailure(failure) => failure })

    (passed, failed) match {
      case (Seq(), Seq()) => NoTests(suite.name)
      case (xs, Seq())    => AllPassed(suite.name, xs)
      case (Seq(), ys)    => AllFailed(suite.name, ys)
      case (xs, ys)       => SomePassed(suite.name, xs, ys)
    }
  }

  final case class EqSyntax[A](value1: A) {
    def =?=(value2: A): (A, A) = (value1, value2)
  }

  object EqSyntax {
    implicit def toEqSyntax[A](value1: A): EqSyntax[A] = EqSyntax[A](value1)
  }

  abstract class SuiteLike(val suiteName: String) {
    import scala.collection.mutable.ListBuffer

    private val testRegister = new ListBuffer[Test]

    lazy val allTests: Seq[Test] = testRegister


    def test[A](name: String)(gen: => (A, A))(implicit E: Equality[A], D: Difference[A]): Unit = {
      testRegister += defineTest[A](name, gen)
    }
  }
}

