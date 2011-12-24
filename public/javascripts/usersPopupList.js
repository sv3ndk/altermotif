var onClickOnChosenUserCallback;

function initUsersPopupList(callback) {
	
	onClickOnChosenUserCallback = callback;
	
	popupHeight = computePopupHeight($("#usersPopupList div.oneContactPopupLine").length);
	
	$("#usersPopupList").dialog({
		autoOpen : false,
		width : 550,
		height : popupHeight,
		modal : true
	});
	
	
	$("#usersPopupList").on("click", ".oneContactPopupLine", function(event) {
		var username = $(event.target).find(".contactUserName").text();
		if (username == null || username == "") {
			username = $(event.target).parent().find(".contactUserName").text();
		}
		
		// calling back the script on the main page in order to do whatever is necessary when a user is selected
		onClickOnChosenUserCallback(username);
	});

}

function computePopupHeight(numberOfUsersInPopupList) {
	if (numberOfUsersInPopupList == 0) {
		return 150;
	} else if (numberOfUsersInPopupList < 4) {
		return 50 + numberOfUsersInPopupList * 100;
	} else {
		return 435;
	}
	
	return 
}

function openUsersPopupList() {
	$("#usersPopupList").dialog("open");
}

var clonedPopup;
function openFilteredUsersPopupList(listOfFilteredUsers, title) {
	clonedPopup = $("#usersPopupList").clone();
	
	// TODO: clean this up (lot's of copy/pasted code...)
	
	clonedPopup.find(".oneContactPopupLine").each(function(index, element) {
		var thisUserName = $(element).find(".contactUserName").text();;
		if ($.inArray(thisUserName, listOfFilteredUsers) != -1) {
			$(element).remove();
		}
	} );
	
	popupHeight = computePopupHeight(clonedPopup.find("div.oneContactPopupLine").length);

	clonedPopup.dialog({
		autoOpen : false,
		width : 550,
		height : popupHeight,
		modal : true,
		title : title
	});
	
	
	clonedPopup.on("click", ".oneContactPopupLine", function(event) {
		var username = $(event.target).find(".contactUserName").text();
		if (username == null || username == "") {
			username = $(event.target).parent().find(".contactUserName").text();
		}
		
		// calling back the script on the main page in order to do whatever is necessary when a user is selected
		onClickOnChosenUserCallback(username);
	});

	
	
	clonedPopup.dialog("open");
}

function closeUsersPopupList() {
	$("#usersPopupList").dialog("close");
}

function closeFilteredUsersPopupList() {
	clonedPopup.dialog("close");
}

