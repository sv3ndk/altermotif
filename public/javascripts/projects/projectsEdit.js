$(document).ready(function() {
	init();
});

//var allLocations ;
var allLinks ;
var inputMultiLocationsController;
var inputMultiTextController;
var inputMultiThemesController;

function init() {
	
	var allLocations = dabUtils.parseJsonStringIntoObject("#hiddenAllLocationJson");
	inputMultiLocationsController = new dabInputMultiLocationsLib.InputMultiLocationsController($("#inputProjectLocations div"), allLocations);

	var allTags = dabUtils.parseJsonStringIntoObject("#hiddenAllTagsJson");
	inputMultiTextController = new dabInputMultiTextLib.InputMultiTextController($("#inputTags div.inputMultiText"), allTags);
	
	var initSelectedThemes = dabUtils.parseJsonStringIntoObject("#hiddenAllThemesJson");
	inputMultiThemesController = new dabInputMultiThemesLib.InputMultiThemesController($("#inputMultiThemes div.inputMultiThemes"),
		allThemes, initSelectedThemes, function(newSelectedThemesValue) {
			updateAllThemesHiddenForm(newSelectedThemesValue);
		});

	
	initAddLinkLogic();
	
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

//this is called back from projectThemes.js any time the list of chosen themes changes
function updateAllThemesHiddenForm(newAllThemesValue) {
	$("#hiddenAllThemesJson").val(JSON.stringify(newAllThemesValue));
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
		$("#hiddenAllTagsJson").val(inputMultiTextController.getTextJson());
		
		$("div.projectEditionFormContainer form").submit();
	});

	
	$("#cancelProfileButton").click(function() {
		$("#cancelEditionContainer form").submit();
	});
}