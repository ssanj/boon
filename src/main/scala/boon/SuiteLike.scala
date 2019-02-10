package boon

abstract class SuiteLike(val suiteName: String)(primary: Test, others: Test*) {

  def suite: Suite = Suite(SuiteName(suiteName), NonEmptySeq(primary, others))

  //we need another test method for multi assertion tests
}