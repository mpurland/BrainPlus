Description
-----------

BrainPlus is a Brainfuck and BrainPlus interpreter implemented in Scala.

Instruction Set
---------------

### Instruction set (Brainfuck)
```
>    Increment the pointer (to point to the next cell to the right).
<    Decrement the pointer (to point to the next cell to the left).
+    Increment (increase by one) the byte at the pointer.
-    Decrement (decrease by one) the byte at the pointer.
.    Output the value of the byte at the pointer.
,    Accept one byte of input, storing its value in the byte at the pointer.
[    Jump forward to the command after the corresponding ] if the byte at the pointer is zero.
]    Jump back to the command after the corresponding [ if the byte at the pointer is nonzero.
```

### Extended instruction set (BrainPlus)
For information see (http://esolangs.org/wiki/Extended_Brainfuck and http://www.primaryobjects.com/CMS/Article163)
```
$    Overwrites the byte in storage with the byte at the pointer. (Extended Type I)
!    Overwrites the byte at the pointer with the byte in storage. (Extended Type I)
0-F  Sets the value of the current memory pointer to a multiple of 16. (Extended Type III)
a-z  Call function a-z, where function is named based upon location in code. (BrainPlus)
@    Exits the program, or if inside a function, return to the last position in main program
     and restore state. (Extended Type I, BrainPlus)
```

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