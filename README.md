# JGRAM

## Project Overview
Just in Time Grading Messages (JGRAM) is a program conceived of by Professor Braude at Boston University Metropolitan College to better connect strades. The goaludents with learning objectives and assignment g of the program is to provide assignment feedback through the use of checkpoints located in key points throughout the assignment. Along with the inclusion of feedback, each checkpoint has a weight and grade. JGRAM produces a results table that collocates all checkpoints in one place which provides an overview of the student’s grade. The checkpoints help students to understand how they met expectations of the assignment and where to focus their future work in the course.

## Installation

## Configuration

## Operation

## File List
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

## Test
JUnit tests are located jgram/src/jgram/tests 

## Contact information
Developer: Nikki Tebaldi

Contact: ntebaldi@bu.edu

## Changelog
Version 2.0: Re-build of JGRAM program originally developed by Sumesh Poduval found at https://gitlab.com/sumeshpoduvallab/jgram/-/tree/master/server
