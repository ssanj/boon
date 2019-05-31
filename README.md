# boon [![Build Status](https://travis-ci.org/ssanj/boon.svg?branch=master)](https://travis-ci.org/ssanj/boon) [ ![Download](https://api.bintray.com/packages/ssanj/maven/boon/images/download.svg) ](https://bintray.com/ssanj/maven/boon/_latestVersion)

> /buːn/ (noun)
> a thing that is helpful or beneficial

boon is a small framework for testing pure code. boon is:

1. Opinionated
1. Focuses on testing pure code
1. Has no external library dependencies
1. Fast
1. Strongly typed
1. Easy to run in the REPL

boon is inspired by [ScalaCheck](https://www.scalacheck.org) - which is a simple but very powerful Property-Based Testing framework.

Some things that are unique to boon:

1. Purity - test failures don't throw Exceptions
1. First Class Assertions - Assertions can be named and combined
1. Failure context - Failures can have write out a context of all useful information
1. Two failure modes - Assertions can either fail on the first error (**Seq**uential) or continue running other Assertions (**Ind**ependent)

## Usage in SBT ##

Add the following to your `build.sbt` file:

```scala
libraryDependencies += "net.ssanj" %% "boon" % "0.0.4-b01" % Test

testFrameworks += new TestFramework("boon.sbt.BoonFramework")

resolvers += Resolver.bintrayRepo("ssanj", "maven")
```

You can now run all the usual `sbt` test commands such as: `test` and `testOnly`.

For more information see the following links:

- [API](docs/api/index.md)
- [Use Custom Types in Assertions](docs/use-custom-types-in-assertions.md)
- [Customising Assertions](docs/customising-assertions.md)
- [Customising Output](docs/customising-output.md)
- [Running in the REPL](docs/running-in-the-repl.md)
- [Glossary](docs/glossary/index.md)
- [Publishing](docs/publishing.md)
