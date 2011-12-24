package com.svend.dab.core.beans.projects;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.data.annotation.Transient;

import web.utils.Utils;

import com.svend.dab.core.beans.profile.UserSummary;

/**
 * @author svend
 * 
 */
public class Task {

	private static Logger logger = Logger.getLogger(Task.class.getName());
	
	public enum TASK_STATUS {
		todo("projectTaskTodo"), done("projectTaskDone");

		private final String label;

		private TASK_STATUS(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}

	}

	private String id;
	private String name;
	private List<UserSummary> assignees;
	private Date dueDate;
	
	@Transient
	private String dueDateStr;
	
	private TASK_STATUS status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<UserSummary> getAssignees() {
		return assignees;
	}

	public void setAssignees(List<UserSummary> assignees) {
		this.assignees = assignees;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
		dueDateStr  =Utils.formatDate(dueDate); 
	}

	public TASK_STATUS getStatus() {
		return status;
	}

	public void setStatus(TASK_STATUS status) {
		this.status = status;
	}

	public String getDueDateStr() {
		return dueDateStr;
	}

}
