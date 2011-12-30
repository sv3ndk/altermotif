////////////////////////////////////////////
// UserListPopup constructor
// 

function UserListPopup(callerObj, titleStr, onClickOnChosenUserCallbackFunc) {

	this.owner = callerObj;
	this.onClickOnChosenUserCallback = onClickOnChosenUserCallbackFunc;
	this.title = titleStr;
	
	this.activePopupHtmlElement;

	///////////////////////
	// init (always call this method)
	this.init = function () {
		var self = this;		
		this.initPopup($("#usersPopupList"));
	};
	
	////////////////////
	// class API
	
	
	// "normal" opening of the poup (without filtering the list of displayed users)
	this.open = function () {
		this.activePopupHtmlElement = $("#usersPopupList"); 
		$("#usersPopupList").dialog("open");
	};
	
	
	// filtered opening of the dialog (filtering the list of user => we actually open a popup clone here)
	this.openFiltered = function (listOfFilteredUsers, title) {
		this.activePopupHtmlElement = $("#usersPopupList").clone();
		
		// removing any line from the clone for the users specified in the list of filtered users
		this.activePopupHtmlElement.find(".oneContactPopupLine").each(function(index, element) {
			var thisUserName = $(element).find(".contactUserName").text();;
			if ($.inArray(thisUserName, listOfFilteredUsers) != -1) {
				$(element).remove();
			}
		} );
		
		this.initPopup(this.activePopupHtmlElement);
		this.activePopupHtmlElement.dialog("open");
	};
	
	
	this.close = function () {
		this.activePopupHtmlElement.dialog("close");
	};

	// this is the real total number of users (without taking into account any filtering)
	this.countTotalNumberOfPopupUser = function (){
		return $("#usersPopupList").find("div.oneContactPopupLine").length;
	};
	
	///////////////////////////
	// internal methods
	

	this.computePopupHeight = function(numberOfUsersInPopupList) {
		if (numberOfUsersInPopupList == 0) {
			return 150;
		} else if (numberOfUsersInPopupList < 4) {
			return 50 + numberOfUsersInPopupList * 100;
		} else {
			return 435;
		}
	};
	
	
	this.initPopup = function (popupHtmlElement) {
		var popupHeight = this.computePopupHeight(popupHtmlElement.find("div.oneContactPopupLine").length);

		popupHtmlElement.dialog({
			autoOpen : false,
			width : 550,
			height : popupHeight,
			modal : true,
			title : this.title
		});
		
		var self = this;
		popupHtmlElement.on("click", ".oneContactPopupLine", function(event) {
			var username = $(event.target).find(".contactUserName").text();
			if (username == null || username == "") {
				username = $(event.target).parent().find(".contactUserName").text();
			}
			
			// calling back the script on the main page in order to do whatever is necessary when a user is selected
			self.onClickOnChosenUserCallback(self.owner, username);
			self.close();
		});
	}
}