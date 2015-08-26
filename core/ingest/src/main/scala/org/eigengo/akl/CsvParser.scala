package org.eigengo.akl

import akka.parboiled2.ParserInput.ByteArrayBasedParserInput
import akka.parboiled2._

import scala.util.{Failure, Success, Try}
import scalaz.ValidationNel

object CsvParser {

  case object EmptyCsv extends ValidationFailed
  case class UnreadableCsv(message: String) extends ValidationFailed

  def apply(input: Array[Byte]): ValidationNel[ValidationFailed, Seq[Seq[String]]] = {
    import scalaz.syntax.validation._

    if (input.length == 0) EmptyCsv.failureNel
    else new CsvParser(new ByteArrayBasedParserInput(input), ",").parsed() match {
      case Success(rows) ⇒ rows.successNel
      case Failure(ex)   ⇒ UnreadableCsv(ex.getMessage).failureNel
    }
  }

}

case class CsvParser(input: ParserInput, delimeter: String) extends Parser {
  def DQUOTE = '"'
  def DELIMITER_TOKEN = rule(capture(delimeter))
  def DQUOTE2 = rule("\"\"" ~ push("\""))
  def CRLF = rule(capture("\r\n" | "\n"))
  def NON_CAPTURING_CRLF = rule("\r\n" | "\n")

  val delims = s"$delimeter\r\n" + DQUOTE
  def TXT = rule(capture(!anyOf(delims) ~ ANY))
  val WHITESPACE = CharPredicate(" \t")
  def SPACES: Rule0 = rule(oneOrMore(WHITESPACE))

  def escaped = rule(optional(SPACES) ~
    DQUOTE ~ (zeroOrMore(DELIMITER_TOKEN | TXT | CRLF | DQUOTE2) ~ DQUOTE ~
    optional(SPACES)) ~> (_.mkString("")))
  def nonEscaped = rule(zeroOrMore(TXT | capture(DQUOTE)) ~> (_.mkString("")))

  def field = rule(escaped | nonEscaped)
  def row: Rule1[Seq[String]] = rule(oneOrMore(field).separatedBy(delimeter))
  def file = rule(zeroOrMore(row).separatedBy(NON_CAPTURING_CRLF))

  def parsed() : Try[Seq[Seq[String]]] = file.run()
}
