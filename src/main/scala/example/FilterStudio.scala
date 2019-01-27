package example

object FilterStudio {
  def filterStudio(values: List[String]): List[String] = values.filterNot(_ == "studio")
}
