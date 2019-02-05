package object boon {
  def partitionWith[A, S, F](xs: Seq[A], pfs: PartialFunction[A, S], pff: PartialFunction[A, F]): (Seq[S], Seq[F]) = {
    xs.foldLeft((Seq.empty[S], Seq.empty[F])){(acc, v) =>
      //refactor out `isDefined`
      if (pfs.isDefinedAt(v)) (acc._1 :+ pfs(v), acc._2)
      else if (pff.isDefinedAt(v)) (acc._1, acc._2 :+ pff(v))
      else acc
    }
  }

  //can we get this to return a These? \$/
  def partitionWith3[A, S, F, M](xs: Seq[A], pfs: PartialFunction[A, S], pff: PartialFunction[A, F], pfm: PartialFunction[A, M]): (Seq[S], Seq[F], Seq[M]) = {
    xs.foldLeft((Seq.empty[S], Seq.empty[F], Seq.empty[M])){(acc, v) =>
      //refactor out `isDefined`
      if (pfs.isDefinedAt(v)) (acc._1 :+ pfs(v), acc._2, acc._3)
      else if (pff.isDefinedAt(v)) (acc._1, acc._2 :+ pff(v), acc._3)
      else if (pfm.isDefinedAt(v)) (acc._1, acc._2, acc._3 :+ pfm(v))
      else acc
    }
  }

  def initOption[A](xs: Seq[A]): Option[Seq[A]] = if (xs.nonEmpty) Option(xs.init) else None
}

