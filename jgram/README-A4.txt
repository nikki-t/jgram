MAIN ENTRY POINT:
MainJGRAM.main

DESCRIPTION:
This iteration of JGRAM is focused on the use of Binary I/O and lambdas and
streams. 

Binary I/O is realized through the creation of a Record and RecordManager class.
It is mean to simulate the creation of a database record and the operations
required to write and retrieve a record.

Lambdas and streams are used through JGRAM to improve efficiency in the 
extraction and creation of collections of various JGRAM objects which include:
Document class:
- Parsing of Checkpoints, Comments and a GradeMapping
JustInTimeEvaluator:
- Calculation of total grade
Task:
- Creation of a file list that contains valid Word documents for grading


REQUIREMENTS:
Can be found here: https://drive.google.com/file/d/1MhhtkIf2ONlZGOkxMwvv6HQDOxKX3wCm/view
(Check Requirements tab)

CLASS MODEL:
Can be found here: https://drive.google.com/file/d/1MhhtkIf2ONlZGOkxMwvv6HQDOxKX3wCm/view
(Check RUML tab)

THIRD-PARTY APIs: 
io.jsonwebtokens
javax.xml.bind
javax.xml.crypto
org.junit.jupiter
org.apahce.poi
org.mockito