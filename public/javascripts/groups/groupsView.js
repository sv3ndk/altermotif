$(document).ready(function() {
	new dabGroupsViewLib.GroupsViewController();
});

var dabGroupsViewLib = {

	// /////////////////////////////////////////////////////////
	// controller for the main forum widget
	GroupsViewController : function() {

		this.userParticipantKoModel = new dabGroupsViewLib.UserParticipantKoModel();
		this.applyWithProfilePopupControler = new dabGroupsViewLib.ApplyWithProfilePopupControler(this.userParticipantKoModel);
		this.clickedApplicantId;

		this.init = function() {
			var self = this;

			// knockout bindings for user participant
			ko.applyBindings(this.userParticipantKoModel, $("#applyWithProfileTd")[0]);
			ko.applyBindings(this.userParticipantKoModel, $("#groupParticipants")[0]);
			ko.applyBindings(this.userParticipantKoModel, $("#groupUserApplicants")[0]);

			// click on "close group"
			if (isCloseGroupLinkEffective) {
				new Confirm.AskAndProceed(this, "#groupToolBox", "#closeGroupLink", confirmCloseGroupText, null, self.afterUserConfirmsCloseGroup).init();
			} else {
				alert("todo: non effective close group...");
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
			new Confirm.AskAndProceed(this, "#groupUserApplicants", ".acceptUserApplication", confirmAcceptApplyToGroup, self.whenUserClicksOnAcceptUserApplication, function() {
				self.whenUserConfirmsAcceptUserApplication()
			}).init();
			
			// click on "reject user application"
			new Confirm.AskAndProceed(this, "#groupUserApplicants", ".rejectUserApplication", confirmRejectApplyToGroup, self.whenUserClicksOnRejectUserApplication, function() {
				self.whenUserConfirmsRejectUserApplication()
			}).init();
			
			
			

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
				applicantId: self.clickedApplicantId
			}), function(data) {
				self.userParticipantKoModel.moveApplicantToParticipants(self.clickedApplicantId);
			});
		};
		
		this.whenUserClicksOnRejectUserApplication = function(self, event) {
			self.recordClickedApplicantId(event);
		};
		
		this.whenUserConfirmsRejectUserApplication = function() {
			var self = this;
			$.post(rejectUserApplicationToGroup({
				groupId : visitedGroupId,
				applicantId: self.clickedApplicantId
			}), function(data) {
				self.userParticipantKoModel.removeApplicant(self.clickedApplicantId);
			});
		};

		// ///////////////////////////
		
		this.recordClickedApplicantId = function(event) {
			this.clickedApplicantId = $(event.target).parent().find(".hiddenParticipantId").text();
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

			$("#confirmedParticipants div.participant").each(function(index, oneHtmlParticipant) {
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
				this.acceptedParticipants.push(new dabGroupsViewLib.UserParticipant(userName, profileLink, photoLocation, userLocation, role));
			} else {
				this.applicants.push(new dabGroupsViewLib.UserParticipant(userName, profileLink, photoLocation, userLocation, role));
			}
		};

		
		this.removeApplicant = function(applicantId) {
			var applicant = this.findApplicantById(applicantId);
			if (applicant != undefined ) {
				var applicantIndex = _.indexOf(this.applicants(), applicant);
				if (applicantIndex != -1) {
					this.applicants.splice(applicantIndex, 1);
				}
			}
		};
		
		this.moveApplicantToParticipants = function(applicantId) {
			var applicant = this.findApplicantById(applicantId);
			if (applicant != undefined ) {
				var applicantIndex = _.indexOf(this.applicants(), applicant);
				if (applicantIndex != -1) {
					this.applicants.splice(applicantIndex, 1);
					this.acceptedParticipants.push(applicant);
				}
				
			}
			
		}
		
		this.findApplicantById = function(applicantId) {
			return _.find(this.applicants(), function(oneApplicant) {return oneApplicant.userName == applicantId;});
		};
		
		this.init();
	},

	UserParticipant : function(userName, profileLink, photoLocation, userLocation, role) {

		this.userName = userName;
		this.profileLink = profileLink;
		this.photoLocation = photoLocation;
		this.userLocation = userLocation;
		this.role = ko.observable(role);

	},

};
