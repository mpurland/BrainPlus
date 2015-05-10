package brainplus

import Array._
import spire.syntax.literals._
import spire.math.{ UByte, UShort, UInt, ULong }

object FunctionStackState {
  val MaxMemory = 30000
}

class FunctionStackState(var memoryPointer: Int = 0, val functionInputPointer: Int = 0, val instructionPointer: Int = 0, val functionName: Option[Char] = None)(implicit verbose: Boolean) {
  import Implicits._

  var memory: Array[UByte] = Array.fill(FunctionStackState.MaxMemory)(0)
  var minMemoryPointer: Int = 0
  var maxMemoryPointer: Int = 0
  var jumpStack = scala.collection.mutable.Stack[Int]()

  var exitLoop = false
  var exitLoopInstructionPointer: Int = 0

  clear()

  def clear()(implicit verbose: Boolean) = {
    if (verbose) println("Resetting function stack state...")
    exitLoop = false
    exitLoopInstructionPointer = 0

    memory = Array.fill[UByte](FunctionStackState.MaxMemory)(0)
    memoryPointer = 0
    minMemoryPointer = 0
    maxMemoryPointer = 0

    // Reset jump stack
    jumpStack.clear

    if (verbose) printMemory
  }

  def printMemory() = {
    print("Stack: ")
    for (i <- (minMemoryPointer.toInt to maxMemoryPointer.toInt)) {
      print(memory(i) + " ")
    }
    print("\n")
  }
}

// Instruction set (Brainfuck)
// >    Increment the pointer (to point to the next cell to the right).
// <    Decrement the pointer (to point to the next cell to the left).
// +    Increment (increase by one) the byte at the pointer.
// -    Decrement (decrease by one) the byte at the pointer.
// .    Output the value of the byte at the pointer.
// ,    Accept one byte of input, storing its value in the byte at the pointer.
// [    Jump forward to the command after the corresponding ] if the byte at the pointer is zero.
// ]    Jump back to the command after the corresponding [ if the byte at the pointer is nonzero.

// For information see (http://esolangs.org/wiki/Extended_Brainfuck and http://www.primaryobjects.com/CMS/Article163)
// Extended instruction set (BrainPlus)
// $    Overwrites the byte in storage with the byte at the pointer. (Extended Type I)
// !    Overwrites the byte at the pointer with the byte in storage. (Extended Type I)
// 0-F  Sets the value of the current memory pointer to a multiple of 16. (Extended Type III)
// a-z  Call function a-z, where function is named based upon location in code. (BrainPlus)
// @    Exits the program, or if inside a function, return to the last position in main program
//      and restore state. (Extended Type I, BrainPlus)
class BrainPlusInterpreter(implicit verbose: Boolean = false) extends Interpreter {
  import Implicits._

  // Call stack size
  val maxFunctionCallStackSize = 256

  // Storage used for a temporary variable or returning data from a function
  var storage: UByte = 0
  var stop: Boolean = false

  // Functions
  var functionInputPointer: Int = 0
  var functionCallStack = scala.collection.mutable.Stack[FunctionStackState]()

  // Function name to program pointer for function
  var functions = scala.collection.mutable.Map[Char, Int]()

  var lastPrinted: Option[Char] = None

  reset()

  def printMemory(implicit verbose: Boolean) = {
    println(s"Memory pointer: ${functionCallStack.top.memoryPointer}")
    println(s"Storage: $storage")
    println(lastPrinted match {
      case Some(lp) => s"Last output value: ${lp.toInt}"
      case _ => "No output yet."
    })
    functionCallStack.top.printMemory()
  }

  def reset()(implicit verbose: Boolean) = {
    if (verbose) println("Resetting state...")

    // Reset state
    stop = false
    storage = UByte(0)
    lastPrinted = None

    // Reset functions map
    functions.clear

    // Clear function call stack
    functionCallStack.clear

    // Push global state onto function call stack
    functionCallStack.push(new FunctionStackState())
  }

  def parse(source: String)(implicit verbose: Boolean) = {
    var instructionPointer: Int = 0

    // Process
    while (instructionPointer < source.size && !stop) {
      val symbol = source(instructionPointer)
      var nextSymbol: Option[Char] = None

      if (instructionPointer + 1 < source.length) {
        nextSymbol = Some(source(instructionPointer + 1))
      }

      if (verbose) {
        printMemory
        println(s"Processing symbol: $symbol")
      }

      var topFunctionCallState = functionCallStack.top

      symbol match {
        case '$' => if (!topFunctionCallState.exitLoop) storage = topFunctionCallState.memory(topFunctionCallState.memoryPointer)
        case '!' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = storage
        
        case '0' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 0
        case '1' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 16
        case '2' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 32
        case '3' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 48
        case '4' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 64
        case '5' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 80
        case '6' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 96
        case '7' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 112
        case '8' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 128
        case '9' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 144
        case 'A' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 160
        case 'B' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 176
        case 'C' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 192
        case 'D' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 208
        case 'E' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 224
        case 'F' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = 240

        case '>' => {
          if (!topFunctionCallState.exitLoop) {
            topFunctionCallState.memoryPointer += 1

            if (topFunctionCallState.memoryPointer >= FunctionStackState.MaxMemory) {
              if (verbose) println(s"Memory pointer > ${FunctionStackState.MaxMemory}. Resetting to ${FunctionStackState.MaxMemory}.")
              topFunctionCallState.memoryPointer = 0
            }

            topFunctionCallState.maxMemoryPointer = Math.max(topFunctionCallState.maxMemoryPointer, topFunctionCallState.memoryPointer)
          }
        }
        case '<' => {
          if (!topFunctionCallState.exitLoop) {
            topFunctionCallState.memoryPointer -= 1

            if (topFunctionCallState.memoryPointer < 0) {
              if (verbose) println("Memory pointer < 0. Resetting to 0.")
              topFunctionCallState.memoryPointer = 0
            }

            topFunctionCallState.minMemoryPointer = Math.min(topFunctionCallState.minMemoryPointer, topFunctionCallState.memoryPointer)
          }
        }
        case '+' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = topFunctionCallState.memory(topFunctionCallState.memoryPointer) + UByte(1)
        case '-' => if (!topFunctionCallState.exitLoop) topFunctionCallState.memory(topFunctionCallState.memoryPointer) = topFunctionCallState.memory(topFunctionCallState.memoryPointer) - UByte(1)
        case '.' => {
          if (!topFunctionCallState.exitLoop) {
            var memoryChar: Char = topFunctionCallState.memory(topFunctionCallState.memoryPointer).toChar
            lastPrinted = Some(memoryChar)
            if (verbose) println(s"Output: '$memoryChar' value: ${memoryChar.toInt}")
            else print(memoryChar)
          }
        }
        case ',' => if (!topFunctionCallState.exitLoop) {
          if (functionCallStack.length <= 1) {
            topFunctionCallState.memory(topFunctionCallState.memoryPointer) = UByte(readByte())
          } else {
            topFunctionCallState.memory(topFunctionCallState.memoryPointer) = topFunctionCallState.memory(functionInputPointer)
            functionInputPointer += 1
          }
        }
        // Detect simple halting infinite loop. If found simply go next instruction.
        case x if (symbol == '[' && nextSymbol != None && nextSymbol.get == ']') => instructionPointer += 2

        case '[' => {
          topFunctionCallState.jumpStack.push(instructionPointer)

          if (!topFunctionCallState.exitLoop) {
            if (topFunctionCallState.memory(topFunctionCallState.memoryPointer) == 0.toUByte) {
              if (verbose) println("Skipping [. Jumping forward.")
              topFunctionCallState.exitLoop = true
              topFunctionCallState.exitLoopInstructionPointer = instructionPointer
            } else if (verbose) {
              println("Continuing [")
            }
          }
        }
        case ']' => {
          if (topFunctionCallState.jumpStack.length > 0) {
            val jumpInstructionPointer = topFunctionCallState.jumpStack.pop

            if (!topFunctionCallState.exitLoop) {
              if (topFunctionCallState.memory(topFunctionCallState.memoryPointer) != 0.toUByte) {
                if (verbose) println("Continuing ]. Jumping backward.")
                instructionPointer = jumpInstructionPointer - 1
              } else if (verbose) println("Exiting ]")
            } else if (jumpInstructionPointer == topFunctionCallState.exitLoopInstructionPointer) {
              if (verbose) println(s"Exiting loop.")
              // Exiting loop
              topFunctionCallState.exitLoop = false
              topFunctionCallState.exitLoopInstructionPointer = 0
            }
          } else {
            if (verbose) println("] without corresponding [");
            // Exiting loop (bad program...)
            topFunctionCallState.exitLoop = false
            topFunctionCallState.exitLoopInstructionPointer = 0
          }
        }
        case '@' => {
          if (functionCallStack.length > 1) {
            var tempFunctionCallState = functionCallStack.pop
            var newTopFunctionCallState = functionCallStack.top

            instructionPointer = tempFunctionCallState.instructionPointer
            functionInputPointer = tempFunctionCallState.functionInputPointer
            newTopFunctionCallState.memoryPointer = tempFunctionCallState.memoryPointer
          } else {
            stop = true
          }
        }
        case x if ('a' to 'z').contains(x) => {
          if (!topFunctionCallState.exitLoop) {
            functions.get(x) match {
              case Some(functionInstructionPointer) => {
                if (verbose) {
                  println(s"Calling function: $x")
                }
                var newFunctionCallState = new FunctionStackState(functionInputPointer = topFunctionCallState.memoryPointer, memoryPointer = topFunctionCallState.memoryPointer, instructionPointer = instructionPointer)
                functionCallStack.push(newFunctionCallState)

                functionInputPointer = topFunctionCallState.memoryPointer
                instructionPointer = functionInstructionPointer
              }
              case _ => if (verbose) println(s"Invalid function: $x")
            }
          }
        }
        case x => if (verbose) println(s"Instruction not supported: $x")
      }
      instructionPointer += 1
    }
  }

  def processFunctions(source: String)(implicit verbose: Boolean) = {
    var instructionPointer: Int = source.indexOf('@')
    var function: Char = 'a'
    while (instructionPointer > -1 && instructionPointer < source.length - 1 && !stop) {
      functions(function) = instructionPointer
      function = (function.toInt + 1).toChar
      instructionPointer = source.indexOf('@', instructionPointer + 1)
    }

    if (verbose) {
      print("Functions: ")
      if (functions.size == 0) {
        println("none")
      }
      for ((f, p) <- functions) {
        print(s"$f($p) ")
      }
      print("\n")
    }
  }

  def run(source: String)(implicit verbose: Boolean) = {
    if (verbose) println(s"Code: $source")

    // Reset before running
    reset

    // Pre-process functions map after first @
    processFunctions(source)

    // Parse the code
    parse(source)
  }
}