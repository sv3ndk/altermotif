

var dabEmailPopupLib = {
		
	EmailPopupController : function (htmlPopupElement, sendingPopupHtmlElement, textSubject, textBody, sendEmailCallBack) {

		this.htmlPopupElement = htmlPopupElement;
		this.sendingPopupHtmlElement = sendingPopupHtmlElement;
		this.sendEmailCallBack = sendEmailCallBack;
		
		this.defaultTextBody = textBody;
		this.textSubject = textSubject;
		
		this.emailPopupModel = new dabEmailPopupLib.EmailPopupModel();
		
		///////////////////////
		// public API
		this.open = function () {
			
			this.emailPopupModel.reset("", "", this.textSubject, this.defaultTextBody);
			this.htmlPopupElement.dialog("open");
			this.htmlPopupElement.find(".toInput").focus();
		};

		this.close = function () {
			this.htmlPopupElement.dialog("close");
		};
		
		
		///////////////////////
		// internal API
		this.init = function() {
			var self = this;

			$("#emailSentSuccessfullyNotification").notify();
			$("#emailSentUnsuccessfullyNotification").notify();
			
			ko.applyBindings(self.emailPopupModel, htmlPopupElement[0]);
			
			this.htmlPopupElement.dialog({
				autoOpen : false,
				width: 900,
				height: 525,
				"buttons" : [ {
					text : okLabelValue,
					click : function(event) {
						self.whenUserClicksOk();
					}
				},

				{
					text : cancelLabelValue,
					click : function() {
						self.close();
					}
				}
				]
			});
		};
		
		this.whenUserClicksOk = function() {

			var self = this;
			this.emailPopupModel.validate();
			if (this.emailPopupModel.isValid()) {
				$.blockUI({message: sendingPopupHtmlElement});
				var sentContent = encodeURI(self.emailPopupModel.text());
				$.post(sendEmailCallBack(
						{recipient: self.emailPopupModel.recipient(), replyTo: self.emailPopupModel.replyTo(), subject: self.emailPopupModel.subject(), textContent: sentContent}
					), 
					function(data) {
						$.unblockUI();
						
						if (data.ok) {
							$("#emailSentSuccessfullyNotification").notify("create", {}, {
								expires : 2000,
								speed : 750
							});
						} else {
							$("#emailSentUnsuccessfullyNotification").notify("create", {}, {
								expires : 2000,
								speed : 750
							});
						}
						self.close();
					}
				);
				
			}
		};
		
		this.init();
	},
	
	
	
	EmailPopupModel : function () {
		this.replyTo = ko.observable();
		this.recipient = ko.observable();
		this.subject = ko.observable();
		this.text = ko.observable();
		
		this.isRecipientMissing = ko.observable(false);
		this.isRecipientIncorrect = ko.observable(false);
		this.isSubjectMissing = ko.observable(false);
		this.isTextMissing = ko.observable(false);
		
		this.validate = function() {
			this.isRecipientMissing(this.recipient() == "" || this.recipient() == undefined);
			this.isSubjectMissing(this.subject() == "" || this.subject() == undefined);
			this.isTextMissing(this.text() == "" || this.text() == undefined);
			
			if (! this.isRecipientMissing()) {
				var emailRegexp = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
				this.isRecipientIncorrect(!emailRegexp.test(this.recipient()));
			}
			
		};
		
		this.isValid = function() {
			return !this.isRecipientMissing() && !this.isSubjectMissing() && !this.isTextMissing() && ! this.isRecipientIncorrect();
		};
		
		this.reset = function (replyTo, recipient, subject, text) {
			this.replyTo(replyTo);
			this.recipient(recipient);
			this.subject(subject);
			this.text(text);
			this.isRecipientMissing(false);
			this.isSubjectMissing(false);
			this.isTextMissing(false);
		};
		
	},
	
};