package brainplus

object BrainPlus extends App {
  import scala.tools.jline.console._
  import scala.tools.jline.console.completer._
  val con = new scala.tools.jline.console.ConsoleReader

  implicit var verbose = false
  var bp = new BrainPlusInterpreter

  // Outputs "Hello World!"
  // bp.run("++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.")

  // Outputs "Hello World!" with a linefeed.
  // bp.run("++++++++[>++++[>++>+++>+++>+<<<<-]>+>->+>>+[<]<-]>>.>>---.+++++++..+++.>.<<-.>.+++.------.--------.>+.>++.")

  // Outputs ascii value 3
  // bp.run("a!.@+++$@")

  // Outputs ascii value 341 or 601?
  // bp.run("++>++++>+<<a!.>.>.@,>,-[-<+>]<+$@")

  // Outputs 99 bottles of beer
  // bp.run(",.+[>g22.<--h.+]]>++--[,],,,<->-][],$<>!,!,,-g,,,+@7$>66++.!-.[!+><+++[[..-<[-t-$--[$.--$-$-$---.>.@7-[.!!>6++-$++++$+$.@7+$6++.+++..!+.@$7--+.+-$[[-[.@7++++.6++++++++.---.@7+++++++.7$6+.!----[..@$>!3$---------------a-.+-b.+!$!!>c@d3----------------.e.f@")

  // Outputs "KeepCalmKeepCoding"
  // bp.run("1+$-+--!++>$<+++++++$+$$+++[++a.b$!.$a$+-.-[>c$$]]@5-----.7------$-----..!++++++.[]--o+>+>]>]]]+$!]>+@4+++.6+.7----.+.>>>!><->>[-<>,>+.[>>>>>>>>+>,,+]!]@>>4+-+-+++.+7-$.--$---------.+++++.--<!!![!+.[>.[<@")

  // Outputs arbitrarily many Fibonacci numbers. Does not exit.
  // bp.run(">++++++++++>+>+[[+++++[>++++++++<-]>.<++++++[>--------<-]+<<<]>.>>[[-]<[>+<-]>>[<<+>+>-]<[>+<-[>+<-[>+<-[>+<-[>+<-[>+<-[>+<-[>+<-[>+<-[>[-]>+>+<<<-[>+<-]]]]]]]]]]]+>>>]<<<]")

  // Multiply by 3
  // bp.run(">->,b<!a+!.b]!.+[.-$[[.+,!<!,,,,].><--.,.]]+--]+->@,>,-[-<+>]<+$@++[+[>,$[-<-a!a!$@")

  // bp.run("++[+[>,$[-<-a!a!.!]a>...,..,.]@,>,-[-<+>]<+$@")
  // bp.run("1+$-+--!++>$<+++++++$+$$+++[++a.b$!.$a$+-.-[>c$$]]@5-----.7------$-----..!++++++.[]--o+>+>]>]]]+$!]>+@4+++.6+.7----.+.>>>!><->>[-<>,>+.[>>>>>>>>+>,,+]!]@>>4+-+-+++.+7-$.--$---------.+++++.--.[<@")
  // bp.run(",>,$[!>--$<<a>>]4]+,,-[-<+>]<+.$@")

  def exitCommand(): Unit = System.exit(0)
  def printMemoryCommand(): Unit = bp.printMemory(true)
  def resetCommand(): Unit = {
    bp.reset()
    println("Interpreter memory reset.")
  }
  def verboseCommand(): Unit = {
    verbose = !verbose
    println(s"Verbose is now ${if (verbose) "on" else "off"}.")
  }
  def programHelloWorldCommand(): Unit = {
    // Outputs "Hello World!"
    bp.run("++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.")
  }

  val commands = Map[String, () => Unit]("exit" -> exitCommand, "p" -> printMemoryCommand, "printMemory" -> printMemoryCommand, "r" -> resetCommand, "reset" -> resetCommand, "verbose" -> verboseCommand, "runProgramHelloWorld" -> programHelloWorldCommand)

  class CommandAutoCompleter extends Completer {
    def complete(buffer: String, cursor: Int, candidates: java.util.List[CharSequence]): Int = {
      for ((commandString, command) <- commands) {
        if (commandString.startsWith(buffer)) {
          candidates.add(commandString)
        }
      }

      if (candidates.isEmpty) -1
      else 0
    }
  }

  con.addCompleter(new CommandAutoCompleter())

  println("Scala BrainPlus v0.1 by Matthew Purland")
  println("At the prompt, enter any valid Brainfuck or BrainPlus programs or enter any of the following commands.")
  print("Available commands: ")

  for ((commandString, command) <- commands) {
    print(commandString + " ")
  }
  println

  Iterator.continually(con.readLine("> ")).dropWhile(_ == null).foreach(line => {
    commands.get(line) match {
      case Some(command) => {
        command()
      }
      case _ => {
        bp.run(line)(verbose)
        if (verbose) bp.printMemory
      }
    }
    print("\n")
  })
}