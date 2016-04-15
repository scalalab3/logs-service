import org.specs2._

abstract class DefaultSpec extends mutable.Specification

class ExampleText extends DefaultSpec {
  "Example test case" >> {
    val i = 1
    "Subcase 1" in {
      i must_== (1)
    }
  }
}
