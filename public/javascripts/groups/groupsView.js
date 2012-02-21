var ctrl;

$(document).ready(function() {
	ctrl = new dabGroupsViewLib.GroupsViewController();
});

var dabGroupsViewLib = {

	// /////////////////////////////////////////////////////////
	// controller for the main forum widget
	GroupsViewController : function() {

		this.userParticipantKoModel = new dabGroupsViewLib.UserParticipantKoModel();
		this.applyWithProfilePopupControler = new dabGroupsViewLib.ApplyWithProfilePopupControler(this.userParticipantKoModel);
		this.clickedApplicantId;
		this.clickedParticipantId;

		this.init = function() {
			var self = this;

			// knockout bindings for user participant
			ko.applyBindings(this.userParticipantKoModel, $("#applyWithProfileTd")[0]);
			ko.applyBindings(this.userParticipantKoModel, $("#groupParticipants")[0]);

			if ($("#groupPendingApplicants")[0] != undefined) {
				ko.applyBindings(this.userParticipantKoModel, $("#groupPendingApplicants")[0]);
			}

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
			new Confirm.AskAndProceed(this, "#applyWithProfileTd", "#cancelApplyToGroupLink", confirmCancelApplyToGroup, null, function() {
				self.afterUserConfirmsRemoveGroupApplication()
			}).init();

			// click on "accept user application"
			new Confirm.AskAndProceed(this, "#groupPendingApplicants", ".acceptUserApplication", confirmAcceptApplyToGroup,
					self.whenUserClicksOnAcceptUserApplication, function() {
						self.whenUserConfirmsAcceptUserApplication()
					}).init();

			// click on "reject user application"
			new Confirm.AskAndProceed(this, "#groupPendingApplicants", ".rejectUserApplication", confirmRejectApplyToGroup,
					self.whenUserClicksOnRejectUserApplication, function() {
						self.whenUserConfirmsRejectUserApplication()
					}).init();

			// click on "leave group"
			new Confirm.AskAndProceed(this, "#groupParticipants", ".leaveGroup", confirmLeaveGroup, null, this.whenUserConfirmsLeaveGroup).init();

			// click on "make admin"
			new Confirm.AskAndProceed(this, "#groupParticipants", ".makeAdmin", confirmMakeAdminText, this.whenUserClicksOnMakeAdmin,
					this.whenUserConfirmsMakeAdmin).init();

			// click on "make admin"
			new Confirm.AskAndProceed(this, "#groupParticipants", ".makeMember", confirmMemberAdminText, this.whenUserClicksOnMakeMember,
					this.whenUserConfirmsMakeMember).init();
			
			// click on "remove member"
			new Confirm.AskAndProceed(this, "#groupParticipants", ".removeMember", confirmRemoveMemberText, this.whenUserClicksOnRemoveMember,
					this.whenUserConfirmsRemoveMember).init();

		};

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
				self.userParticipantKoModel.alreadyApplyToGroupLinkVisisble(false);
			});
		};

		this.whenUserClicksOnAcceptUserApplication = function(self, event) {
			self.recordClickedApplicantId(event);
		};

		this.whenUserConfirmsAcceptUserApplication = function() {
			var self = this;
			$.post(acceptUserApplicationToGroup({
				groupId : visitedGroupId,
				applicantId : self.clickedApplicantId
			}), function(response) {
				if (response.success) {
					self.userParticipantKoModel.moveApplicantToParticipants(self.clickedApplicantId);
					self.applyUpdatedRoles(response, self.clickedApplicantId);
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
				self.userParticipantKoModel.removeApplicant(self.clickedApplicantId);
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
			}), function(response) {
				if (response.success) {
					self.userParticipantKoModel.updateParticipantRole(self.clickedParticipantId, adminRoleLabel);
					self.applyUpdatedRoles(response, self.clickedParticipantId);
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
			}), function(response) {
				if (response.success) {
					self.userParticipantKoModel.updateParticipantRole(loggedInUserId, memberRoleLabel);
					self.applyUpdatedRoles(response, self.clickedParticipantId);
				}
			});
		};

		this.whenUserClicksOnRemoveMember = function(self, event) {
			self.recordClickedParticipantId(event);
		};
		
		this.whenUserConfirmsRemoveMember =  function(self) {
			$.post(removeMember({
				groupId : visitedGroupId,
				removedUser : self.clickedParticipantId
			}), function(response) {
				if (response.success) {
					self.userParticipantKoModel.removeParticipant(self.clickedParticipantId);
					self.applyUpdatedRoles(response, self.clickedParticipantId);
				}
			});
		};
		
		// ///////////////////////////

		this.recordClickedApplicantId = function(event) {
			this.clickedApplicantId = $(event.target).parent().find(".hiddenParticipantId").text();
		};

		this.recordClickedParticipantId = function(event) {
			this.clickedParticipantId = $(event.target).parent().parent().find(".hiddenParticipantId").text();
		};

		this.applyUpdatedRoles = function(participantActionOutcome, otherUser) {
			this.userParticipantKoModel.updateParticipantVisibility(loggedInUserId, participantActionOutcome.loggedInUser_leaveLinkVisible,
					participantActionOutcome.loggedInUser_makeAdminLinkVisible, participantActionOutcome.loggedInUser_makeMemberLinkVisible, false);

			this.userParticipantKoModel.updateParticipantVisibility(otherUser, participantActionOutcome.otherUser_leaveLinkVisible,
					participantActionOutcome.otherUser_makeAdminLinkVisible, participantActionOutcome.otherUser_makeMemberLinkVisible, participantActionOutcome.otherUser_removeUserLinkVisible);
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

			$("#userParticipantsDataModel div.participant").each(function(index, oneHtmlParticipant) {
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
				this.acceptedParticipants.push(new dabGroupsViewLib.UserParticipant(userName, profileLink, photoLocation, userLocation, role,
						isLeaveLinkVisible, isMakeAdminLinkVisible, isMakeMemberLinkVisible, isRemoveMemberLinkVisible));
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

		this.updateParticipantVisibility = function(participantId, isLeaveLinkVisible, isMakeAdminLinkVisible, isMakeMemberLinkVisible, isRemoveMemberLinkVisible) {
			var participant = this.findParticipantById(participantId);
			if (participant != undefined) {
				participant.isLeaveLinkVisible(isLeaveLinkVisible);
				participant.isMakeAdminLinkVisible(isMakeAdminLinkVisible);
				participant.isMakeMemberLinkVisible(isMakeMemberLinkVisible);
				participant.isRemoveMemberLinkVisible(isRemoveMemberLinkVisible);
			}
		};

		this.removeParticipant = function(participantId) {
			var participant = this.findParticipantById(participantId);
			if (participant != undefined) {
				var participantIndex = _.indexOf(this.acceptedParticipants(), participant);
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
			return _.find(this.acceptedParticipants(), function(oneParticipant) {
				return oneParticipant.userName == participantId;
			});
		};

		this.init();
	},

	UserParticipant : function(userName, profileLink, photoLocation, userLocation, role, isLeaveLinkVisible, isMakeAdminLinkVisible, isMakeMemberLinkVisible, isRemoveMemberLinkVisible) {
		this.userName = userName;
		this.profileLink = profileLink;
		this.photoLocation = photoLocation;
		this.userLocation = userLocation;
		this.role = ko.observable(role);
		this.isLeaveLinkVisible = ko.observable(isLeaveLinkVisible);
		this.isMakeAdminLinkVisible = ko.observable(isMakeAdminLinkVisible);
		this.isMakeMemberLinkVisible = ko.observable(isMakeMemberLinkVisible);
		this.isRemoveMemberLinkVisible = ko.observable(isRemoveMemberLinkVisible);
	},

};
