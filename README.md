# JGRAM

## Project Overview
Just in Time Grading Messages (JGRAM) is a program conceived of by Professor Braude at Boston University Metropolitan College to better connect strades. The goaludents with learning objectives and assignment g of the program is to provide assignment feedback through the use of checkpoints located in key points throughout the assignment. Along with the inclusion of feedback, each checkpoint has a weight and grade. JGRAM produces a results table that collocates all checkpoints in one place which provides an overview of the student’s grade. The checkpoints help students to understand how they met expectations of the assignment and where to focus their future work in the course.

## Installation
In Eclispse:
1. Clone Git Respository
2. Create new Java project
3. Convert project to a Maven project by right-clicking on the project in Project Explorer and clicking 'Configure' > 'Maven Project'
4. Run pom.xml as a Maven build to install and compile dependencies

## Configuration

## Operation

To run JGRAM:
* Execute MainJGRAM.java which serves as the main entry point to 
the JGRAM program
* Enter numbers 1 through 5 to indicate the choice in task
you would like to run
* The program runs until the user enters 5.

## File List
```
jgram
├── README.txt
├── pom.xml
├── saved_jgrams.txt
└── src
    ├── jgram
    │   ├── MainJGRAM.java
    │   ├── assessment
    │   │   ├── Checkpoint.java
    │   │   ├── Comment.java
    │   │   ├── Document.java
    │   │   ├── Evaluator.java
    │   │   ├── GradeMapping.java
    │   │   ├── JustInTimeEvaluator.java
    │   │   └── Result.java
    │   ├── security
    │   │   └── JWT.java
    │   ├── task
    │   │   ├── EvaluationTask.java
    │   │   ├── NewDocumentTask.java
    │   │   ├── TamperTestTask.java
    │   │   └── Task.java
    │   ├── tests
    │   │   ├── CheckpointTest.java
    │   │   ├── CommentTest.java
    │   │   ├── DocumentTest.java
    │   │   ├── GradeMappingTest.java
    │   │   ├── JWTTest.java
    │   │   ├── JustInTimeEvaluatorTest.java
    │   │   └── ResultTest.java
    │   └── utilities
    │       └── Validation.java
    └── module-info.java
```
## Test
JUnit tests are located jgram/src/jgram/tests 

## Contact information
Developer: Nikki Tebaldi

Contact: ntebaldi@bu.edu

## Changelog
Version 2.0: Re-build of JGRAM program originally developed by Sumesh Poduval found at https://gitlab.com/sumeshpoduvallab/jgram/-/tree/master/server
