package brainplus

import Array._

// Brainfuck interpreter (Source: http://peter-braun.org/2012/07/brainfuck-interpreter-in-40-lines-of-scala/)
object BrainfuckInterpreter extends Interpreter {
  def run(source: String)(implicit verbose: Boolean) {
    var memory: Array[Integer] = Array.fill[Integer](30000)(0)
    var memptr: Integer = 0
    var prgptr: Integer = 0

    def jump(dir: Int) = {
      var loop = 1
      while (loop > 0) {
        prgptr += dir
        loop += (source(prgptr) match {
          case '[' => dir
          case ']' => -dir
          case _ => 0
        })
      }
    }

    while (prgptr < source.length) {
      source(prgptr) match {
        case '>' => memptr += 1
        case '<' => memptr -= 1
        case '+' => memory(memptr) += 1
        case '-' => memory(memptr) -= 1
        case '.' => print(memory(memptr).toChar)
        case ',' => memory(memptr) = readInt()
        case '[' => if (memory(memptr) == 0) jump(1)
        case ']' => if (memory(memptr) != 0) jump(-1)
        case _ => print("instruction not supported")
      }
      prgptr += 1
    }
  }
}