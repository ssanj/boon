package boon

object NonEmptyMap {
  def values[K, V](head: (K, V), tail: (K, V)*): NonEmptyMap[K, V] =
    NonEmptySeq.nes[(K, V)](head, tail:_*)
}
