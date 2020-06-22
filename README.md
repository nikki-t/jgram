# JGRAM

## Project Overview
Just in Time Grading Messages (JGRAM) is a program conceived of by Professor Braude at Boston University Metropolitan College to better connect students with their assignment grades. The goal of the program is to provide assignment feedback through the use of checkpoints located in key points throughout the assignment. Along with the inclusion of feedback, each checkpoint has a weight and grade. JGRAM produces a results table that collocates all checkpoints in one place with a total grade which provides an overview of the student’s grade. The checkpoints help students to understand how they met expectations of the assignment and where to focus their future work in the course.

JGRAM provides four main operations:
- Grade evaluation which calculates and creates a table of assignment grading results.
- Tamper Detection which evaluates previously graded assignments and determines if the checkpoints or grade mapping has been modified.
- New Document validation which validates that documents are ready for grading by the grader (i.e. instructor, professor, facilitator, teaching assistant, etc.).
- Assigment reporting which provides three reports: 1) Student grading data for one assignment; 2) All available grading data for a student; 3) Assignment statistics for all previously graded assignments.

## Installation
In Eclipse:
1. Clone Git Respository
2. Create new Java project
3. Convert project to a Maven project by right-clicking on the project in Project Explorer and clicking 'Configure' > 'Maven Project'
4. Run pom.xml as a Maven build to install and compile dependencies

## Configuration

## Operation

To run JGRAM:
* Execute MainJGRAM.java which serves as the main entry point to
the JGRAM program
* Enter numbers 1 through 6 to indicate the choice in task
you would like to run
* The program runs until the user enters 6

## File List
```
.
└── jgram
    ├── README.md
    └── jgram
        ├── README-A6.txt
        ├── hs_err_pid52837.log
        ├── pom.xml
        └── src
            └── jgram
                ├── MainJGRAM.java
                ├── assessment
                │   ├── Checkpoint.java
                │   ├── Comment.java
                │   ├── Document.java
                │   ├── Evaluator.java
                │   ├── GradeMapping.java
                │   ├── JustInTimeEvaluator.java
                │   └── Result.java
                ├── database
                │   └── jgram.db
                ├── exceptions
                │   ├── InvalidCheckpointException.java
                │   ├── InvalidCommentException.java
                │   └── InvalidGradeMappingException.java
                ├── security
                │   ├── JWT.java
                │   └── Secret.java
                ├── storage
                │   ├── Assignment.java
                │   └── RecordManager.java
                ├── task
                │   ├── AssignmentReportTask.java
                │   ├── EvalTaskRun.java
                │   ├── EvaluationTask.java
                │   ├── NewDocTaskRun.java
                │   ├── NewDocumentTask.java
                │   ├── TamperTask.java
                │   ├── TamperTaskRun.java
                │   ├── Task.java
                │   └── TaskRun.java
                ├── tests
                │   ├── AssignmentReportTaskTest.java
                │   ├── CheckpointTest.java
                │   ├── CommentTest.java
                │   ├── DocumentTest.java
                │   ├── EvalTaskRunTest.java
                │   ├── EvaluationTaskTest.java
                │   ├── GradeMappingTest.java
                │   ├── JWTTest.java
                │   ├── JustInTimeEvaluatorTest.java
                │   ├── NewDocTaskRunTest.java
                │   ├── NewDocumentTaskTest.java
                │   ├── RecordManagerTest.java
                │   ├── ResultTest.java
                │   ├── TamperTaskRunTest.java
                │   ├── TamperTaskTest.java
                │   ├── TaskTest.java
                │   ├── TestUtilities.java
                │   └── resources
                │       ├── document
                │       │   ├── GRADED
                │       │   │   └── GRADED_document-test.docx
                │       │   └── document-test.docx
                │       ├── eval
                │       │   ├── GRADED
                │       │   │   ├── GRADED_eval-task-test-valid.docx
                │       │   │   └── jgram.dat
                │       │   ├── eval-task-test-invalid.docx
                │       │   └── eval-task-test-valid.docx
                │       ├── jgramTest.db
                │       ├── newdoc
                │       │   ├── new-doc-test-invalid.docx
                │       │   └── new-doc-test-valid.docx
                │       ├── record
                │       │   └── last_first_a1.docx
                │       ├── report
                │       │   ├── REPORT_Assignment\ 2:\ Loop\ Basics.txt
                │       │   ├── REPORT_assignment_stats.txt
                │       │   └── REPORT_griffs_olive.txt
                │       ├── tamper
                │       │   ├── GRADED
                │       │   │   ├── GRADED_tamper-invalid.docx
                │       │   │   ├── GRADED_tamper-valid.docx
                │       │   │   └── report.txt
                │       │   ├── tamper-invalid.docx
                │       │   └── tamper-valid.docx
                │       └── tamperRun
                │           ├── GRADED
                │           │   ├── GRADED_tamper-invalid.docx
                │           │   ├── GRADED_tamper-valid.docx
                │           │   └── report.txt
                │           ├── tamper-invalid.docx
                │           └── tamper-valid.docx
                └── utilities
                    └── Validation.java
```
## Test
JUnit tests are located jgram/src/jgram/tests

## Contact information
Developer: Nikki Tebaldi

Contact: ntebaldi@bu.edu

## Changelog
Version 2.0: Re-build of JGRAM program originally developed by Sumesh Poduval found at https://gitlab.com/sumeshpoduvallab/jgram/-/tree/master/server
