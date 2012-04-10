var dabMessagesLib = {

	MessageDataModel : function() {
		
		this.messages = ko.observableArray();
		this.previousPageExists = ko.observable();
		this.nextPageExists = ko.observable();
		
		this.messageReactionEnabled = ko.observable(); 
		this.displayedMessage = new dabMessagesLib.Message(null);
		
		var self = this;
		
		this.clear = function() {
			this.messages.splice(0, this.messages().length);
			this.displayedMessage.clear();
			this.messageReactionEnabled(false);
		};
		
		this.areAllMessagesSelected = ko.computed(function() {
			return _.all(self.messages(), function(message) {
				return message.isChecked();
			});
		});
		
		this.isAtLeastOneMessageSelected  = ko.computed(function() {
			return _.any(self.messages(), function(message) {
				return message.isChecked();
			});
		});
		
		this.getAllSelectedMessageIds = function() {
			return _(self.messages())
						.chain()
						.filter(function(message) {return message.isChecked();})
						.map(function(message) {return message.id(); })
						.value();
		};
		
		this.toggleAllCheckBoxes = function(value) {
			_.each(self.messages(), function(message) {
				message.isChecked(value);
			});
		};
		
		this.toggleCheckBox = function (checkboxIndex) {
			var previousValue = this.messages()[checkboxIndex].isChecked();
			this.messages()[checkboxIndex].isChecked(!previousValue);
		};
		
		this.addOneMessage = function(message) {
			this.messages.push(new dabMessagesLib.Message(message));
		};
		
		this.setDisplayedMessage = function (messageIndex) {
			var clickedMessage = this.messages()[messageIndex];
			
			if (clickedMessage != null) {
				this.messageReactionEnabled(true);
				this.displayedMessage.apply(clickedMessage);
				
				if (!clickedMessage.isRead()) {
					clickedMessage.isRead(true);
					return clickedMessage.id();
				} else {
					return null;
				}
			} else {
				return null;
			}
		};
	},

	
	Message: function(message) {
		
		this.id = ko.observable(message != null ? message.id : "");
		this.fromUser = ko.observable(message != null ? message.fromUser : { isProfileActive : false});
		this.to;
		this.subject = ko.observable(message != null ? message.subject : ""); 
		this.creationDate = ko.observable(message != null ? message.creationDate : "");    
		this.isRead = ko.observable(message != null ? message.read : true);
		this.content = ko.observable(message != null ? message.content : "");
		
		this.isChecked = ko.observable(false);
		
		this.apply = function(copied){
			this.id(copied.id());
			this.fromUser(copied.fromUser());
			this.subject(copied.subject());
			this.creationDate(copied.creationDate());
			this.isRead(copied.isRead());
			this.content(copied.content());
		};
		
		this.clear = function() {
			this.id("");
			this.fromUser("");
			this.subject("");
			this.creationDate("");
			this.isRead(true);
			this.content("");
		};
	},
};