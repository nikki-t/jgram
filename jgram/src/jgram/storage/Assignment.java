package jgram.storage;

import java.util.ArrayList;
import java.util.List;

import jgram.assessment.GradeMapping;
import jgram.assessment.Result;

public class Assignment {
	
	// Instance variable(s)
	private String grader;
	private String assignmentTitle;
	private String location;
	private List<Result> resultList;
	private GradeMapping gradeMapping;

	public Assignment(String inputGrader, String inputTitle,
			String inputLocation) {
		
		grader = inputGrader;
		assignmentTitle = inputTitle;
		location = inputLocation;
		resultList = new ArrayList<>();
	}
	
	/**
	 * Intent: Add a result to the result list instance variable.
	 * @param result
	 */
	public void addResult(Result result) {
		resultList.add(result);
	}
	
	public String getAssignmentTitle() {
		return assignmentTitle;
	}
	
	public GradeMapping getGradeMapping() {
		return gradeMapping;
	}
	
	public String getGrader() {
		return grader;
	}
	
	public String getLocation() {
		return location;
	}
	
	public List<Result> getResultList() {
		return resultList;
	}
	
	public void setGradeMapping(GradeMapping gm) {
		gradeMapping = gm;
	}
	
}
