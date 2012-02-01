//var allLocations ;
var allLinks ;
var inputMultiLocationsController;

function init() {
	
	var allLocations = dabUtils.parseJsonStringIntoObject("#hiddenAllLocationJson");
	inputMultiLocationsController = new dabInputMultiLocationsLib.InputMultiLocationsController($("#inputProjectLocations div"), allLocations);
	
	initAddLinkLogic();
	initTagLogic();
	initThemeLogic();
	
	setupSetLanguageAutoComplete();
	initSubmitCancelButtons();
	
	// this is present in dab.js
	dabUtils.makeInputDatePicker("#projectNewDueDate", '-0:+100');
}


///////////////////////////////////////////////////////////////
// add/remove link logic

function initAddLinkLogic() {

	allLinks = dabUtils.parseJsonStringIntoObject("#hiddenAllLinksJson");
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



////////////////////////////////////////////////////////////////
// add a theme logic

function initThemeLogic() {
	// this is the init function defined in projectThemess.js
	initAddThemeLogic(dabUtils.parseJsonStringIntoObject("#hiddenAllThemesJson"), updateAllThemesHiddenForm);
}

//this is called back from projectThemes.js any time the list of chosen themes changes
function updateAllThemesHiddenForm(newAllThemesValue) {
	$("#hiddenAllThemesJson").val(JSON.stringify(newAllThemesValue));
}


///////////////////////////////////////////////////////////////
// add tag logic


function initTagLogic() {
	// this is the init function defined in projectTags.js
	initAddTagLogic(dabUtils.parseJsonStringIntoObject("#hiddenAllTagsJson"), updateAllTagsHiddenForm);
}

//this is called back from projectTags.js any time the list of selected tags changes
function updateAllTagsHiddenForm(newAllTagValue) {
	$("#hiddenAllTagsJson").val(JSON.stringify(newAllTagValue));
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

		if (editAssetCtrl != undefined) {
			// this controller is in projectsEditAssets.js
			editAssetCtrl.updateSubmittedAssets();
		}

		if (editTasksCtrl != undefined) {
			// this controller is in projectsEditTasks.js
			editTasksCtrl.updateSubmittedTasks();
		}
		
		$("#hiddenAllLocationJson").val(inputMultiLocationsController.getAllLocationJson());
		
		$("div.projectEditionFormContainer form").submit();
	});

	
	$("#cancelProfileButton").click(function() {
		$("#cancelEditionContainer form").submit();
	});
}