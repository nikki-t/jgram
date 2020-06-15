MAIN ENTRY POINT:
MainJGRAM.main

DESCRIPTION:
This iteration of JGRAM is focused on the use of concurrency.

Concurrency is realized through the relationship between Task subclasses and
TaskRun subclasses. All TaskRun subclasses contain the operations required to
process one Word document assignment for the corresponding Task subclass. 
The TaskRun class implements Java's Runnable interface which allows TaskRun 
objects to execute asynchronously and therefore enables the Task subclasses to 
execute many TaskRun objects concurrently.

The order in which documents are processed does not matter and the order that
results are saved and reported also does not matter. The only area that requires
synchronization is output. All output operations are synchronized so that the
results of these operations are not interleaved either on the console, in the
binary save file, or in the tamper report.


REQUIREMENTS:
Can be found here: https://drive.google.com/file/d/1tud-YYE73E0ua25Ahv_yRKrP1BpVjkDS/view?usp=sharing
(Check Requirements tab)

CLASS MODEL:
Can be found here: https://drive.google.com/file/d/1tud-YYE73E0ua25Ahv_yRKrP1BpVjkDS/view?usp=sharing
(Check RUML tab)

THIRD-PARTY APIs: 
io.jsonwebtokens
javax.xml.bind
javax.xml.crypto
org.junit.jupiter
org.apahce.poi
org.mockito