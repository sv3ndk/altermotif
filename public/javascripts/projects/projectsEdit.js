var allLocations;
var allLinks;
var allTags;

function init() {
	
	initLocationLogic();
	
	registerDatePicker();
	
	initAddLinkLogic();
	
	initAddTagLogic();
	
	setupSetLanguageAutoComplete();
	
	initSubmitCancelButtons();
}


//////////////////////////////////////////////////
// location logic


function initLocationLogic() {
	
	allLocations = parseJsonStringIntoObject("#hiddenAllLocationJson");
	for ( var oneKey in allLocations) {
		graphicalAddOneLocation(allLocations[oneKey], true);
	}
	
	// this init is present in the dabGeocoder.js file
	initializeGeoCoder("#addLocationInput", "project_map_canvas", "#locationLat", "#locationLong");

	$("#addLocationLink").click(function() {
		switchLocationMode("enteringLocation");
	});

	$("#addLocationCancelButton").click(function() {
		switchLocationMode("normal");
	});
	
	$("#addLocationSecondButton").click(function() {
		addOneLocation();
		switchLocationMode("normal");
	});
	
	// click on the delete icon
	$("#locationGroup").on("click", "img.deleteImageLink", function(event){
		var removedTagValue = $(event.target).prev().text();
		
		var removedIndex = 0;
		for (var oneKey in allLocations) {
			if (allLocations[oneKey].location == removedTagValue) {
				break;
			}
			removedIndex ++;
		}

		allLocations.splice(removedIndex, 1);
		$("#hiddenAllLocationJson").val(JSON.stringify(allLocations));
		$(event.target).parent().remove();
	});
	
	
}

function switchLocationMode(mode) {
	
	if (mode == "normal") {
		$("#locationInputCommand1, #locationInputCommand2").hide();
		$("#addLocationLink").toggle(500);
		$("#addLocationInput").val('');
		
	} else {
		$("#addLocationLink").hide();
		$("#locationInputCommand1, #locationInputCommand2").toggle(250);
		$("#locationLat").val("");
		$("#locationLong").val("");
		$("#addLocationInput").focus();
		google.maps.event.trigger(map, "resize");
	}
}


function addOneLocation () {

	var oneLanguge =  {
			location: $("#addLocationInput").val(),
			latitude: $("#locationLat").val(),
			longitude: $("#locationLong").val()
	};
	
	if (oneLanguge.location != undefined && oneLanguge.latitude != undefined && oneLanguge.latitude != "" && oneLanguge.longitude != undefined && oneLanguge.longitude != "") {
		allLocations.push(oneLanguge);
		$("#hiddenAllLocationJson").val(JSON.stringify(allLocations));
		graphicalAddOneLocation (oneLanguge, false);
		
	}
}

function graphicalAddOneLocation (oneLanguge, immediate) {
	var newLocationRow = $("#hiddenLocationTemplate").clone().removeAttr("id");
	newLocationRow.find(".projectLocationText").text(oneLanguge.location);
	$("#locationGroup").append(newLocationRow);
	
	if (immediate) {
		$("#locationGroup li:last").show(0);
	} else {
		$("#locationGroup li:last").show(250);
	}
}



///////////////////////////////////////////////


function registerDatePicker() {

	$("#projectNewDueDate").datepicker({
		changeMonth : true,
		changeYear : true,
		dateFormat : "dd/mm/yy",
		yearRange : '-0:+100',
		showAnim : "blind"
	});

//	$("#editProjectForm\\:projectNewDueDate\\:inputField").datepicker("setDate",
//			$("#editProjectForm\\:projectNewDueDate\\:inputField").val());

}



///////////////////////////////////////////////////////////////
// add/remove link logic

function initAddLinkLogic() {

	allLinks = parseJsonStringIntoObject("#hiddenAllLinksJson");
	for ( var oneKey in allLinks) {
		graphicalAddOneLink(allLinks[oneKey], true);
	}
	
	$("#addLinkLink").click(function() {
		switchLinkMode("enteringLink");
	});

	$("#addLinkCancelButton").click(function() {
		switchLinkMode("normal");
	});
	
	$("#addLinkSecondButton").click(function() {
		addOneLink();
		switchLinkMode("normal");
	});
	
	// click on the delete icon
	$("#linksGroup").on("click", "img.deleteImageLink", function(event){
		var removedLinkValue = $(event.target).prev().text();
		
		var removedIndex = 0;
		for (var oneKey in allLinks) {
			if (allLinks[oneKey] == removedLinkValue) {
				break;
			}
			removedIndex ++;
		}

		allLinks.splice(removedIndex, 1);
		$("#hiddenAllLinksJson").val(JSON.stringify(allLinks));
		$(event.target).parent().remove();
	});
	
}


function switchLinkMode(mode) {
	if (mode == "normal") {
		$("#linkInputCommand").hide();
		$("#addLinkLink").toggle(500);
		
	} else {
		$("#addLinkLink").hide();
		$("#linkInputCommand").toggle(250);
		$("#addLinkInput").val("").focus();
	}
}



function addOneLink() {
	var addedLink = $("#addLinkInput").val();
	
	if (addedLink != null && addedLink != "") {
		allLinks.push(addedLink);
		$("#hiddenAllLinksJson").val(JSON.stringify(allLinks));
		graphicalAddOneLink(addedLink, false);
	}
	
}

function graphicalAddOneLink(addedLink, immediate) {
	var newLinkRow = $("#hiddenLinkTemplate").clone().removeAttr("id");
	newLinkRow.find("span").text(addedLink);
	$("#linksGroup").append(newLinkRow);
	
	if (immediate) {
		$("#linksGroup li:last").show(0);
	} else {
		$("#linksGroup li:last").show(250);
	}
}




///////////////////////////////////////////////////////////////
// add tag logic

function initAddTagLogic() {
	
	allTags = parseJsonStringIntoObject("#hiddenAllTagsJson");
	for ( var oneKey in allTags) {
		graphicalAddOneTag(allTags[oneKey], true);
	}


	$("#addTagLink").click(function() {
		switchTagMode("enteringTag");
	});

	$("#addTagCancelButton").click(function() {
		switchTagMode("normal");
	});
	
	$("#addTagSecondButton").click(function() {
		addOneTag();
		switchTagMode("normal");
	});
	
	
	// click on the delete icon
	$("#tagGroup").on("click", "img.deleteImageLink", function(event){
		var removedTagValue = $(event.target).prev().text();
		
		var removedIndex = 0;
		for (var oneKey in allTags) {
			if (allTags[oneKey] == removedTagValue) {
				break;
			}
			removedIndex ++;
		}

		allTags.splice(removedIndex, 1);
		$("#hiddenAllTagsJson").val(JSON.stringify(allTags));
		$(event.target).parent().remove();
	});

	
}

function switchTagMode(mode) {

	if (mode == "normal") {
		$("#tagInputCommand").hide();
		$("#addTagLink").toggle(500);
		
	} else {
		$("#addTagLink").hide();
		$("#tagInputCommand").toggle(250);
		$("#addTagInput").val("").focus();
	}
}

function addOneTag() {
	var addedTag = $("#addTagInput").val();
	
	if (addedTag != null && addedTag != "") {
		allTags.push(addedTag);
		$("#hiddenAllTagsJson").val(JSON.stringify(allTags));
		graphicalAddOneTag(addedTag, false);
	}
}



function graphicalAddOneTag(addedTag, immediate) {
	
	var newTagRow = $("#hiddenTagTemplate").clone().removeAttr("id");
	newTagRow.find("span").text(addedTag);
	$("#tagGroup").append(newTagRow);
	
	if (immediate) {
		$("#tagGroup li:last").show(0);
	} else {
		$("#tagGroup li:last").show(250);
	}
}




///////////////////////////////////////////////////////////

function setupSetLanguageAutoComplete() {
	
	// this is defined in languges.js
	initAllPossibleLanguagesMap();
	
	$("#projectNewLanguage").autocomplete({
		source : allPossibleLanguagesList
	}
	);
}


function initSubmitCancelButtons() {
	
	$("#editProfileButton").click(function() {
		$("#projectNewContainer form").submit();
	});

	
	$("#cancelProfileButton").click(function() {
		$("#cancelEditionContainer form").submit();
	});
	
}


///////////////////////////////////////////////////////
// utilities

function parseJsonStringIntoObject(jqSelector) {

	var allLanguageJson = $(jqSelector).val();
	if (allLanguageJson != null && allLanguageJson != "" && allLanguageJson != "null")  {
		return JSON.parse(allLanguageJson);
	} else {
		return [];
	}
	
}


