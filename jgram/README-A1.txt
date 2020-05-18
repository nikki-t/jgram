This iteration of JGRAM is focused on providing operations for two tasks:
EvaluationTask and TamperTestTask. All input is hardcoded into JGRAM and output
is printed to the console. 

To demonstrate downcasting for the first assignment, a task list is created at 
the beginning of the main method in MainJGRAM.java. Since this is a somewhat
trivial implementation, main will be re-configured in future operations to
create a new concrete subclass task upon user selection.

A hash string is written to a txt file called 'saved_jgrams.txt'. This enables 
the TamperTestTask to decode a previously graded assignment and provide a report
on any modifications made to the assignment documents checkpoints, total grade,
or grade mapping.

To run JGRAM, execute MainJGRAM.java which serves as the main entry point to 
the JGRAM program. Enter numbers 1 through 5 to indicate the choice in task
you would like to run. The program runs until the user enters 5.

Third-party APIs include: 
io.jsonwebtokens
javax.xml.bind
javax.xml.crypto