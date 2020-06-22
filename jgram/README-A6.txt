MAIN ENTRY POINT:
MainJGRAM.main

DESCRIPTION:
This iteration of JGRAM is focused on integrating a database in to JGRAMs
infrastructure.

Database functionality is realized through the use of a SQLite database stored
in the jgram.database package.

The RecordManager class handles all database operations which include: managing
the database connection and inserting data, selecting data, and deleting data 
from the database.

The evaluation task creates an Assignment object that contains all student 
grading data from one assignment grading operation. The RecordManager class 
contains a reference to this Assignment object which gets written to the 
database.

The tamper detection task uses a hash string to compare current grading 
documents with past results. The RecordManager object provides a method that 
allows client code to search for a hash string from a previous grading attempt
by assignment name.

The assignment report task provides three different grading reports that are 
written as text files to a directory selected by the user. The first report
selects all data in the database for one assignment. The second report selects 
all grading data for one student. The third report provides statistics for all
previously graded assignments.

REQUIREMENTS:
Can be found here: https://drive.google.com/file/d/1ZZVIC8sMHdOGz0u9Kzej_mU12-hCLp7F/view?usp=sharing
(Check Requirements tab)

CLASS MODEL: https://drive.google.com/file/d/1ZZVIC8sMHdOGz0u9Kzej_mU12-hCLp7F/view?usp=sharing
Can be found here:
(Check RUML tab)

ENTITY DIAGRAM: https://drive.google.com/file/d/1ZZVIC8sMHdOGz0u9Kzej_mU12-hCLp7F/view?usp=sharing
Can be found here:
(Check ER Diagram tab)

THIRD-PARTY APIs: 
io.jsonwebtokens
javax.xml.bind
javax.xml.crypto
org.junit.jupiter
org.apache.poi
org.apache.commons.io
org.xerial.sqlite-jdbc
org.mockito