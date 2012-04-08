$(document).ready(function() {
	new dabGroupsViewLib.GroupsViewController();
});

var dabGroupsViewLib = {

	// /////////////////////////////////////////////////////////
	// controller for the main forum widget
	GroupsViewController : function() {

		this.userParticipantKoModel = new dabGroupsViewLib.UserParticipantKoModel();
		this.projectsParticipantKoModel = new dabGroupsViewLib.ProjectsParticipantKoModel();
		this.applyWithProfilePopupControler = new dabGroupsViewLib.ApplyWithProfilePopupControler(this.userParticipantKoModel);
		this.applyWithProjectPopupControler = new dabGroupsViewLib.ApplyWithProjectPopupControler(this.projectsParticipantKoModel, this.projectsParticipantKoModel);
		this.clickedApplicantId;
		this.clickedParticipantId;
		this.clickedProjectApplicantId;
		this.clickedProjectId;

		this.init = function() {
			var self = this;

			// knockout bindings for user participant
			ko.applyBindings(this.userParticipantKoModel,$("#applyWithProfileTd")[0]);
			ko.applyBindings(this.userParticipantKoModel, $("#groupParticipants")[0]);

			if ($("#groupPendingApplicants")[0] != undefined) {
				ko.applyBindings(this.userParticipantKoModel, $("#groupPendingApplicants")[0]);
			}

			// knockout bindings for project participant
			ko.applyBindings(this.projectsParticipantKoModel, $("#applyWithProjectTd")[0]);
			ko.applyBindings(this.projectsParticipantKoModel,$("#applyToGroupWithProjectDialog")[0]);
			ko.applyBindings(this.projectsParticipantKoModel,$("#groupProjects")[0]);
			if ($("#groupPendingProjectsApplicants")[0] != undefined) {
				ko.applyBindings(this.projectsParticipantKoModel,$("#groupPendingProjectsApplicants")[0]);
			}

			// photo gallery
			$("a[rel='profilePhotos']").colorbox({
				transition : "none",
				width : "75%",
				height : "75%",
				photo : true
			});

			
			// click on "close group"
			if (isCloseGroupLinkEffective) {
				new Confirm.AskAndProceed(this, "#groupToolBox", "#closeGroupLink", confirmCloseGroupText, null, self.afterUserConfirmsCloseGroup).init();
			} else {
				var displayer = new Confirm.MessageDisplayer(cannotCloseGroupText);
				$("#closeGroupLink").click(function(event) {
					displayer.showDialog();
				});
			}

			// click on "apply to group"
			$("#applyToGroupLink").click(function(event) {
				self.applyWithProfilePopupControler.showDialog()
			});

			// click on "cancel application to group"
			new Confirm.AskAndProceed(this, "#applyWithProfileTd",
					"#cancelApplyToGroupLink", confirmCancelApplyToGroup, null,
					function() {
						self.afterUserConfirmsRemoveGroupApplication()
					}).init();

			// click on "accept user application"
			new Confirm.AskAndProceed(this, "#groupPendingApplicants",
					".acceptUserApplication", confirmAcceptApplyToGroup,
					self.whenUserClicksOnAcceptUserApplication, function() {
						self.whenUserConfirmsAcceptUserApplication()
					}).init();

			// click on "reject user application"
			new Confirm.AskAndProceed(this, "#groupPendingApplicants",
					".rejectUserApplication", confirmRejectApplyToGroup,
					self.whenUserClicksOnRejectUserApplication, function() {
						self.whenUserConfirmsRejectUserApplication()
					}).init();

			// click on "leave group"
			new Confirm.AskAndProceed(this, "#groupParticipants",
					".leaveGroup", confirmLeaveGroup, null,
					this.whenUserConfirmsLeaveGroup).init();

			// click on "make admin"
			new Confirm.AskAndProceed(this, "#groupParticipants", ".makeAdmin",
					confirmMakeAdminText, this.whenUserClicksOnMakeAdmin,
					this.whenUserConfirmsMakeAdmin).init();

			// click on "make admin"
			new Confirm.AskAndProceed(this, "#groupParticipants",
					".makeMember", confirmMemberAdminText,
					this.whenUserClicksOnMakeMember,
					this.whenUserConfirmsMakeMember).init();

			// click on "remove member"
			new Confirm.AskAndProceed(this, "#groupParticipants",
					".removeMember", confirmRemoveMemberText,
					this.whenUserClicksOnRemoveMember,
					this.whenUserConfirmsRemoveMember).init();

			// click on "apply to group with one project where I am admin"
			$("#applyToGroupWithProjectLink").click(function(event) {
				self.applyWithProjectPopupControler.showDialog()
			});

			// click on "accept project application"
			new Confirm.AskAndProceed(this, "#groupPendingProjectsApplicants",
					".projectApplicantAccept",
					confirmAcceptProjectApplicationText,
					this.whenUserClicksOnAcceptProjectMembershipRequest,
					this.whenUserConfirmsAcceptProjectMembershipRequest).init();

			// click on "reject project application"
			new Confirm.AskAndProceed(this, "#groupPendingProjectsApplicants",
					".projectApplicantReject",
					confirmRejectProjectApplicationText,
					this.whenUserClicksOnRejectProjectMembershipRequest,
					this.whenUserConfirmsRejectProjectMembershipRequest).init();
			
			// click on "remove project from group"
			new Confirm.AskAndProceed(this, "#groupProjects",
					"span.projectRemoveFromGroup",
					confirmRemoveProjectFromGroupText,
					this.whenUserClicksOnRemoveProject,
					this.whenUserConfirmsRemoveProject).init();
		};
		
		///////////////////////////////////////

		this.afterUserConfirmsCloseGroup = function() {
			$("#hiddenCloseGroupForm #groupIdInput").val(visitedGroupId);
			$("#hiddenCloseGroupForm form").submit();
		};

		this.afterUserConfirmsRemoveGroupApplication = function() {
			var self = this;
			$.post(cancelApplytoGroup({
				groupId : visitedGroupId
			}), function(data) {
				self.userParticipantKoModel.applyToGroupLinkVisisble(true);
				self.userParticipantKoModel
						.alreadyApplyToGroupLinkVisisble(false);
			});
		};

		this.whenUserClicksOnAcceptUserApplication = function(self, event) {
			self.recordClickedApplicantId(event);
		};

		this.whenUserConfirmsAcceptUserApplication = function() {
			var self = this;
			$.post(
							acceptUserApplicationToGroup({
								groupId : visitedGroupId,
								applicantId : self.clickedApplicantId
							}),
							function(response) {
								if (response.success) {
									self.userParticipantKoModel
											.moveApplicantToParticipants(self.clickedApplicantId);
									self.applyUpdatedRoles(response,
											self.clickedApplicantId);
								}

							});
		};

		this.whenUserClicksOnRejectUserApplication = function(self, event) {
			self.recordClickedApplicantId(event);
		};

		this.whenUserConfirmsRejectUserApplication = function() {
			var self = this;
			$.post(rejectUserApplicationToGroup({
				groupId : visitedGroupId,
				applicantId : self.clickedApplicantId
			}), function(data) {
				self.userParticipantKoModel
						.removeApplicant(self.clickedApplicantId);
			});
		};

		this.whenUserConfirmsLeaveGroup = function(self) {
			$.post(leaveGroup({
				groupId : visitedGroupId,
			}), function(data) {
				self.userParticipantKoModel.removeParticipant(loggedInUserId);
				self.userParticipantKoModel.applyToGroupLinkVisisble(true);
			});
		};

		this.whenUserClicksOnMakeAdmin = function(self, event) {
			self.recordClickedParticipantId(event);
		};

		this.whenUserConfirmsMakeAdmin = function(self) {
			$.post(makeAdmin({
				groupId : visitedGroupId,
				upgradedUser : self.clickedParticipantId
			}),
				function(response) {
					if (response.success) {
						self.userParticipantKoModel.updateParticipantRole(self.clickedParticipantId, adminRoleLabel);
						self.applyUpdatedRoles(response,self.clickedParticipantId);
					}
				});
		};

		this.whenUserClicksOnMakeMember = function(self, event) {
			self.recordClickedParticipantId(event);
		};

		this.whenUserConfirmsMakeMember = function(self) {
			$.post(makeMember({
				groupId : visitedGroupId,
				downgradedUser : self.clickedParticipantId
			}),
				function(response) {
					if (response.success) {
						self.userParticipantKoModel.updateParticipantRole(loggedInUserId, memberRoleLabel);
						self.applyUpdatedRoles(response, self.clickedParticipantId);
					}
				});
		};

		this.whenUserClicksOnRemoveMember = function(self, event) {
			self.recordClickedParticipantId(event);
		};

		this.whenUserConfirmsRemoveMember = function(self) {
			$.post(
				removeMember({
					groupId : visitedGroupId,
					removedUser : self.clickedParticipantId
				}),
				function(response) {
					if (response.success) {
						self.userParticipantKoModel.removeParticipant(self.clickedParticipantId);
						self.applyUpdatedRoles(response,self.clickedParticipantId);
					}
				});
		};

		this.whenUserClicksOnAcceptProjectMembershipRequest = function(self, event) {
			self.recordClickedProjectApplicantId(event);
		};

		this.whenUserConfirmsAcceptProjectMembershipRequest = function(self, event) {
			$.post(
				acceptProjectApplicationToGroup({
					groupId : visitedGroupId,
					projectId : self.clickedProjectApplicantId
				}),
				function(response) {
					if (response.success) {
						self.projectsParticipantKoModel.upgradeProjectApplicationToAcceptedProject(self.clickedProjectApplicantId);
					}
				});
		};

		this.whenUserClicksOnRejectProjectMembershipRequest = function(self, event) {
			self.recordClickedProjectApplicantId(event);
		};

		this.whenUserConfirmsRejectProjectMembershipRequest = function(self, event) {
			$.post(
				rejectProjectApplicationToGroup({
					groupId : visitedGroupId,
					projectId : self.clickedProjectApplicantId
				}),
				function(response) {
					if (response.success) {
						self.projectsParticipantKoModel.removeApplicant(self.clickedProjectApplicantId);
						if (response.addedProjectIamAdminOf != null) {
							var prjSummary = response.addedProjectIamAdminOf.projectSummary;
							self.projectsParticipantKoModel.addProjectIamAdminOf(
									new dabGroupsViewLib.ProjectSummary(prjSummary.projectId, false, "", 
											prjSummary.name, prjSummary.projectLink, prjSummary.mainPhotoThumbLink, false));
						}
						self.projectsParticipantKoModel.applyToGroupWithProjectLinkVisisble((response.applyToGroupWithProjectLinkVisisble));
					}
				});
		};
		
		
		this.whenUserClicksOnRemoveProject = function(self, event) {
			self.recordClickedProject(event);
		};

		this.whenUserConfirmsRemoveProject = function(self, event) {
			$.post(
				removeProjectFromGroup({
					groupId : visitedGroupId,
					projectId : self.clickedProjectId
				}),
				function(response) {
					if (response.success) {
						self.projectsParticipantKoModel.removeProject(self.clickedProjectId);
						if (response.addedProjectIamAdminOf != null) {
							var prjSummary = response.addedProjectIamAdminOf.projectSummary;
							self.projectsParticipantKoModel.addProjectIamAdminOf(
									new dabGroupsViewLib.ProjectSummary(prjSummary.projectId, false, "", prjSummary.name, prjSummary.projectLink, prjSummary.mainPhotoThumbLink, false));
						}
						self.projectsParticipantKoModel.applyToGroupWithProjectLinkVisisble((response.applyToGroupWithProjectLinkVisisble));
					}
				});
		};
		// ///////////////////////////

		this.recordClickedApplicantId = function(event) {
			this.clickedApplicantId = $(event.target).parent().find(
					".hiddenParticipantId").text();
		};

		this.recordClickedParticipantId = function(event) {
			this.clickedParticipantId = $(event.target).parent().parent().find(
					".hiddenParticipantId").text();
		};

		this.applyUpdatedRoles = function(participantActionOutcome, otherUser) {
			this.userParticipantKoModel
					.updateParticipantVisibility(
							loggedInUserId,
							participantActionOutcome.loggedInUser_leaveLinkVisible,
							participantActionOutcome.loggedInUser_makeAdminLinkVisible,
							participantActionOutcome.loggedInUser_makeMemberLinkVisible,
							false);

			this.userParticipantKoModel.updateParticipantVisibility(otherUser,
					participantActionOutcome.otherUser_leaveLinkVisible,
					participantActionOutcome.otherUser_makeAdminLinkVisible,
					participantActionOutcome.otherUser_makeMemberLinkVisible,
					participantActionOutcome.otherUser_removeUserLinkVisible);
		};

		this.recordClickedProjectApplicantId = function(event) {
			this.clickedProjectApplicantId = $(event.target).parent().find(".hiddenProjectId").text();
		};
		
		this.recordClickedProject = function(event) {
			this.clickedProjectId = $(event.target).parent().find(".hiddenProjectId").text();
		};

		this.init();
	},

	// ///////////////////////////

	ApplyWithProjectPopupControler : function(userParticipantKoModel, projectsParticipantKoModel) {

		this.userParticipantKoModel = userParticipantKoModel;
		this.projectsParticipantKoModel = projectsParticipantKoModel;

		this.init = function() {
			var self = this;
			$("#applyToGroupWithProjectDialog").dialog({
				autoOpen : false,
				width : 400,
				"buttons" : [ {
					text : okLabelValue,
					click : function(event) {
						self.whenUserConfirmsApplyToGroupWithProject();
					}
				},

				{
					text : cancelLabelValue,
					click : function() {
						$("#applyToGroupWithProjectDialog").dialog("close");
					}
				} ]
			});

		};

		this.showDialog = function() {
			$("#applyToGroupWithProjectDialog textarea").val("")
			$("#applyToGroupWithProjectDialog").dialog("open");
		}

		this.whenUserConfirmsApplyToGroupWithProject = function() {

			var self = this;
			var selectedProjectId = $("#applyToGroupWithProjectDialog select").val();

			$.post(
				applytoGroupWithProject({
						groupId : visitedGroupId,
						projectId : selectedProjectId,
						applicationText : $("#applyToGroupWithProjectDialog textarea").val()
					}),
					function(response) {
						self.userParticipantKoModel.applyToGroupWithProjectLinkVisisble(response.applyToGroupWithProjectLinkVisisble);
						self.projectsParticipantKoModel.moveProjectFromIamAdminOf2GroupApplicants(selectedProjectId);
						$("#applyToGroupWithProjectDialog").dialog("close");
					});
		};

		this.init();

	},

	// ///////////////////////////

	ApplyWithProfilePopupControler : function(userParticipantKoModel) {

		this.userParticipantKoModel = userParticipantKoModel;

		this.init = function() {
			var self = this;
			$("#applyToGroupDialog").dialog({
				autoOpen : false,
				width : 400,
				"buttons" : [ {
					text : okLabelValue,
					click : function(event) {
						self.whenUserConfirmsApplyToGroup();
					}
				},

				{
					text : cancelLabelValue,
					click : function() {
						$("#applyToGroupDialog").dialog("close");
					}
				} ]
			});
		};

		this.showDialog = function() {
			$("#applyToGroupDialog textarea").val("")
			$("#applyToGroupDialog").dialog("open");
		}

		this.whenUserConfirmsApplyToGroup = function() {
			var self = this;
			$.post(applytoGroup({
				groupId : visitedGroupId,
				applicationText : $("#applyToGroupDialog textarea").val()
			}), function(data) {
				self.userParticipantKoModel.applyToGroupLinkVisisble(false);
				self.userParticipantKoModel.alreadyApplyToGroupLinkVisisble(true);
				$("#applyToGroupDialog").dialog("close");
			});
		};

		this.init();
	},

	// ///////////////////////////////////////////////////////////////////////////
	// data model

	ProjectsParticipantKoModel : function() {

		var self = this;
		this.applyToGroupWithProjectLinkVisisble = ko.observable($("#applyToGroupWithProjectLinkVisisble").text() == "true");
		this.projectsIamAdminOf = ko.observableArray();

		this.projectsMembers = ko.observableArray();
		this.numberOfProjectMembers = ko.computed(function() {
			return self.projectsMembers().length;
		});

		this.projectApplicants = ko.observableArray();
		this.numberOfProjectApplicants = ko.computed(function() {
			return self.projectApplicants().length;
		});

		this.init = function() {

			$("#projectsIAmAdminOf div.projectIamAdminOf").each(
					function(index, oneHtmlProject) {
						self.parseAndAddProjectIamAdminOf(oneHtmlProject)
					});

			$("#projectParticipantsDataModel div.projectApplicant").each(
					function(index, oneHtmlProject) {
						self.parseAndAddProjectMember(oneHtmlProject)
					});

		};
		
		//////////////////////////
		
		this.upgradeProjectApplicationToAcceptedProject = function(projectApplicantId) {
			var applicant = this.findApplicantById(projectApplicantId);
			if (applicant != null) {
				this.removeApplicant(projectApplicantId);
				this.projectsMembers.push(applicant);
			}
		};
		
		this.moveProjectFromIamAdminOf2GroupApplicants = function(projectId) {
			var project = this.findProjectIamAdminOfById(projectId);
			if (project != null) {
				this.removeProjectIamAdminOf(projectId);
				this.projectApplicants.push(project);
			}
		}
		
		this.removeProject = function(projectId) {
			var project = this.findProjectById(projectId);
			if (project != null) {
				var projectIndex = _.indexOf(this.projectsMembers(), project);
				if (projectIndex != -1) {
					this.projectsMembers.splice(projectIndex, 1);
				}
			}
		};
		
		this.removeProjectIamAdminOf = function(projectId) {
			var project = this.findProjectIamAdminOfById(projectId);
			if (project != null) {
				var projectIndex = _.indexOf(this.projectsIamAdminOf(), project);
				if (projectIndex != -1) {
					this.projectsIamAdminOf.splice(projectIndex, 1);
				}
			}
		};
		
		this.removeApplicant = function(applicantId) {
			var applicant = this.findApplicantById(applicantId);
			if (applicant != null) {
				var applicantIndex = _.indexOf(this.projectApplicants(),applicant);
				if (applicantIndex != -1) {
					this.projectApplicants.splice(applicantIndex, 1);
				}
			}
		};
		
		this.findProjectById = function(projectId) {
			return _.find(this.projectsMembers(), function(oneProject) {
				return oneProject.projectId == projectId;
			});
		};
		
		this.findApplicantById = function(applicantId) {
			return _.find(this.projectApplicants(), function(oneApplicant) {
				return oneApplicant.projectId == applicantId;
			});
		};
		
		this.findProjectIamAdminOfById = function(projectId) {
			return _.find(this.projectsIamAdminOf(), function(oneProject) {
				return oneProject.projectId == projectId;
			});
		}
		
		//////////////////

		// used for listing the project I am admin of
		this.parseAndAddProjectIamAdminOf = function(oneHtmlProject) {
			// TODO: clean up copy/pasted code between this and parseAndAddProjectMember
			var projectId = $(oneHtmlProject).find(".projectId").text();
			var projectLink = $(oneHtmlProject).find(".projectLink").attr("href");
			var projectName = $(oneHtmlProject).find(".projectName").text();
			if (projectName == null || projectName == "") {
				// some previous bug was leading to project without name. This should be useless now, but let's be paranoid...
				projectName = "---"
			}
			
			var projectMainThumb = $(oneHtmlProject).find(".projectMainThumb").attr("src");
			if (projectMainThumb == undefined || projectMainThumb == "") {
				projectMainThumb = $("#hiddenDefaultGroupThumb").attr("src");
			}
			
			this.addProjectIamAdminOf(new dabGroupsViewLib.ProjectSummary(projectId, false, "", projectName, projectLink, projectMainThumb, false));
		};
		
		this.addProjectIamAdminOf = function(project) {
			this.projectsIamAdminOf.push(project);
		}

		// used for listing the projects which are member of the group
		this.parseAndAddProjectMember = function(oneHtmlProject) {
			var projectId = $(oneHtmlProject).find(".projectId").text();
			var isProjectAccepted = $(oneHtmlProject).find(".isProjectAccepted").text() == "true";
			var projectApplicationText = $(oneHtmlProject).find(".projectApplicationText").text();
			var projectName = $(oneHtmlProject).find(".projectName").text();
			var projectLink = $(oneHtmlProject).find(".projectLink").attr("href");

			var projectMainThumb = $(oneHtmlProject).find(".projectMainThumb").attr("src");
			if (projectMainThumb == undefined || projectMainThumb == "") {
				projectMainThumb = $("#hiddenDefaultGroupThumb").attr("src");
			}

			var isRemoveFromGroupLinkVisible = $(oneHtmlProject).find(".isRemoveFromGroupLinkVisible").text() == "true";

			if (projectName == null || projectName == "") {
				// some previous bug was leading to project without name. This should be useless now, but let's be paranoid...
				projectName = "---"
			}
			
			var projectParticipant = new dabGroupsViewLib.ProjectSummary(projectId, isProjectAccepted, projectApplicationText, projectName, projectLink, projectMainThumb, isRemoveFromGroupLinkVisible);

			if (isProjectAccepted) {
				this.projectsMembers.push(projectParticipant);
			} else {
				this.projectApplicants.push(projectParticipant);
			}
		};

		this.init();

	},

	UserParticipantKoModel : function() {

		var self = this;
		this.applyToGroupLinkVisisble = ko.observable($("#applyToGroupLinkVisisble").text() == "true");
		this.alreadyApplyToGroupLinkVisisble = ko.observable($("#alreadyApplyToGroupLinkVisisble").text() == "true");

		this.acceptedParticipants = ko.observableArray();
		this.numberOfUserParticipants = ko.computed(function() {
			return self.acceptedParticipants().length;
		});

		this.applicants = ko.observableArray();
		this.numberOfUserApplicants = ko.computed(function() {
			return self.applicants().length;
		});

		this.init = function() {
			var self = this;

			$("#userParticipantsDataModel div.participant").each(
					function(index, oneHtmlParticipant) {
						self.parseAndAddUserParticipant(oneHtmlParticipant)
					});
		};

		this.parseAndAddUserParticipant = function(oneHtmlParticipant) {

			var userName = $(oneHtmlParticipant).find(".username").text();
			var isUserAccepted = $(oneHtmlParticipant).find(".isUserAccepted").text() == "true";
			var profileLink = $(oneHtmlParticipant).find(".profileLink").attr("href");
			var userLocation = $(oneHtmlParticipant).find(".userLocation").text();
			var role = $(oneHtmlParticipant).find(".groupRole").text();

			var photoLocation = $(oneHtmlParticipant).find(".profileContactThumb").attr("src");
			if (photoLocation == undefined || photoLocation == "") {
				photoLocation = $("#hiddenDefaultProfileThumb").attr("src");
			}

			if (isUserAccepted) {
				var isLeaveLinkVisible = $(oneHtmlParticipant).find(".isLeaveLinkVisible").text() == "true";
				var isMakeAdminLinkVisible = $(oneHtmlParticipant).find(".isMakeAdminLinkVisible").text() == "true";
				var isMakeMemberLinkVisible = $(oneHtmlParticipant).find(".isMakeMemberLinkVisible").text() == "true";
				var isRemoveMemberLinkVisible = $(oneHtmlParticipant).find(".isRemoveMemberLinkVisible").text() == "true";
				this.acceptedParticipants
					.push(new dabGroupsViewLib.UserParticipant(userName,
								profileLink, photoLocation, userLocation, role,
								isLeaveLinkVisible, isMakeAdminLinkVisible,
								isMakeMemberLinkVisible,
								isRemoveMemberLinkVisible));
			} else {
				this.applicants.push(new dabGroupsViewLib.UserParticipant(userName, profileLink, photoLocation, userLocation, role, false, false, false, false));
			}
		};

		this.updateParticipantRole = function(participantId, newRole) {
			var participant = this.findParticipantById(participantId);
			if (participant != undefined) {
				participant.role(newRole);
				participant.isMakeAdminLinkVisible(false);
				participant.isMakeMemberLinkVisible(false);
			}
		};

		this.updateParticipantVisibility = function(participantId,
				isLeaveLinkVisible, isMakeAdminLinkVisible,
				isMakeMemberLinkVisible, isRemoveMemberLinkVisible) {
			var participant = this.findParticipantById(participantId);
			if (participant != undefined) {
				participant.isLeaveLinkVisible(isLeaveLinkVisible);
				participant.isMakeAdminLinkVisible(isMakeAdminLinkVisible);
				participant.isMakeMemberLinkVisible(isMakeMemberLinkVisible);
				participant
						.isRemoveMemberLinkVisible(isRemoveMemberLinkVisible);
			}
		};

		this.removeParticipant = function(participantId) {
			var participant = this.findParticipantById(participantId);
			if (participant != undefined) {
				var participantIndex = _.indexOf(this.acceptedParticipants(),participant);
				if (participantIndex != -1) {
					this.acceptedParticipants.splice(participantIndex, 1);
				}
			}
		};

		this.removeApplicant = function(applicantId) {
			var applicant = this.findApplicantById(applicantId);
			if (applicant != undefined) {
				var applicantIndex = _.indexOf(this.applicants(), applicant);
				if (applicantIndex != -1) {
					this.applicants.splice(applicantIndex, 1);
				}
			}
		};

		this.moveApplicantToParticipants = function(applicantId) {
			var applicant = this.findApplicantById(applicantId);
			if (applicant != undefined) {
				var applicantIndex = _.indexOf(this.applicants(), applicant);
				if (applicantIndex != -1) {
					this.applicants.splice(applicantIndex, 1);
					this.acceptedParticipants.push(applicant);
				}
			}
		}

		this.findApplicantById = function(applicantId) {
			return _.find(this.applicants(), function(oneApplicant) {
				return oneApplicant.userName == applicantId;
			});
		};

		this.findParticipantById = function(participantId) {
			return _.find(this.acceptedParticipants(),
					function(oneParticipant) {
						return oneParticipant.userName == participantId;
					});
		};

		this.init();
	},

	UserParticipant : function(userName, profileLink, photoLocation,
			userLocation, role, isLeaveLinkVisible, isMakeAdminLinkVisible,
			isMakeMemberLinkVisible, isRemoveMemberLinkVisible) {
		this.userName = userName;
		this.profileLink = profileLink;
		this.photoLocation = photoLocation;
		this.userLocation = userLocation;
		this.role = ko.observable(role);
		this.isLeaveLinkVisible = ko.observable(isLeaveLinkVisible);
		this.isMakeAdminLinkVisible = ko.observable(isMakeAdminLinkVisible);
		this.isMakeMemberLinkVisible = ko.observable(isMakeMemberLinkVisible);
		this.isRemoveMemberLinkVisible = ko
				.observable(isRemoveMemberLinkVisible);
	},

	ProjectSummary : function(projectId, isProjectAccepted, projectApplicationText, projectName, projectLink, projectMainThumb, isRemoveFromGroupLinkVisible) {
		this.projectId = projectId;
		this.isProjectAccepted = isProjectAccepted;
		this.projectApplicationText = projectApplicationText;
		this.projectName = projectName;
		this.projectLink = projectLink;
		this.projectMainThumb = projectMainThumb;
		this.isRemoveFromGroupLinkVisible = isRemoveFromGroupLinkVisible;
	},
};