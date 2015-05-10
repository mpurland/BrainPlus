Description
-----------

BrainPlus is a Brainfuck and BrainPlus interpreter implemented in Scala.

Building
--------

```
$ sbt run
[info] Running brainplus.BrainPlus 
Scala BrainPlus v0.1 by Matthew Purland
At the prompt, enter any valid Brainfuck or BrainPlus programs or enter any of the following commands.
Available commands: verbose printMemory runProgramHelloWorld p exit reset r 
> runProgramHelloWorld
Hello World!

> ++++++++++[>+++++++>++++++++++>+++>+<<<<-]>++.>+.+++++++..+++.>++.<<+++++++++++++++.>.+++.------.--------.>+.>.
Hello World!
             
> printMemory
Memory pointer: 4
Storage: 0
Last output value: 10
Stack: 0 87 100 33 10 

> 

```

### Requirements

* SBT 0.13+
* Scala 2.11.6

Author
------

Matthew Purland 

Acknowledgements
----------------

* Based on Brainfuck originally designed by Urban Müller. (http://en.wikipedia.org/wiki/Brainfuck)
* Based on BrainPlus originally created by Kory Becker in a series of articles. For further information see http://www.primaryobjects.com/CMS/Article149 and https://github.com/primaryobjects/AI-Programmer.