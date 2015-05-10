package brainplus

import spire.syntax.literals._
import spire.math.{ UByte, UShort, UInt, ULong }

object Implicits {
  implicit def byteToUByte(byte: Byte) = UByte(byte)
  implicit def intToUByte(int: Int) = UByte(int)

  implicit class UByteHelper(ubyte: UByte) {
    def ==(that: Int): Boolean = ubyte.intValue == that
  }

  implicit class IntHelper(int: Int) {
    def toUByte(): UByte = UByte(int)
  }
}

// class InOperand[A](a: A) {
//   def in(seq: Seq[A]): Boolean = seq.contains(a)
// }

// object OperandConversions {
//   implicit def toInOperand[A](a: A): InOperand[A] = new InOperand(a)
// }