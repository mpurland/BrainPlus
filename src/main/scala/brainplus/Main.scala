package brainplus

object BrainPlus extends App {
  import scala.tools.jline.console._
  import scala.tools.jline.console.completer._
  val con = new scala.tools.jline.console.ConsoleReader

  implicit var verbose = false
  var bp = new BrainPlusInterpreter

  // Outputs "Hello World!"
  // bp.run("++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.")

  def exitCommand(arguments: Seq[String]): Unit = System.exit(0)
  def printMemoryCommand(arguments: Seq[String]): Unit = bp.printMemory(true)
  def resetCommand(arguments: Seq[String]): Unit = {
    bp.reset()
    println("Interpreter memory reset.")
  }
  def verboseCommand(arguments: Seq[String]): Unit = {
    verbose = !verbose
    println(s"Verbose is now ${if (verbose) "on" else "off"}.")
  }
  def loadProgramCommand(arguments: Seq[String]): Unit = {
    val source = scala.io.Source.fromFile(s"${arguments(1)}").mkString
    bp.run(source)
  }
  def programHelloWorldCommand(arguments: Seq[String]): Unit = {
    loadProgramCommand("loadProgram" :: "src/main/programs/hello.b" :: Nil)
  }

  val commands = Map[String, Seq[String] => Unit]("exit" -> exitCommand, "p" -> printMemoryCommand, "printMemory" -> printMemoryCommand, "r" -> resetCommand, "reset" -> resetCommand, "verbose" -> verboseCommand, "runProgramHelloWorld" -> programHelloWorldCommand, "loadProgram" -> loadProgramCommand)

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
    val arguments = line.split(" ")
    val commandString = arguments(0)
    commands.get(commandString) match {
      case Some(command) => {
        command(arguments)
      }
      case _ => {
        bp.run(line)(verbose)
        if (verbose) bp.printMemory
      }
    }

    print("\n")
  })
}