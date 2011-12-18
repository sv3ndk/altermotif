// we put here the logic for the themes specification in the forms (the "add tag" link + "add" and "cancel" buttons,...l)
// this is directly coupled with the grapical part of the input in inputThemes.html

// these are all the currently selected themes
var allThemes ;


//this function must accept an array of String: it is provided by the "main" script: this function is called from here anytime we
//suggest to the main script to update its own model
var themeModelUpdatedCallback;


function initAddThemeLogic(initListOfThemes, callback) {
	
	if (initListOfThemes == undefined) {
		allThemes = [];
	} else {
		allThemes = initListOfThemes;
		for (var i = 0 ; i < allThemes.length; i++) {
			graphicalAddOneTheme(allThemes[i], true)
		}
	}
	themeModelUpdatedCallback = callback;
	
	updateSecondThemeDropDown();
	
	$("#addThemeLink").click(function() {
		switchThemeMode("enteringNewTheme");
	});
	
	$("#addThemeCancelButton").click(function() {
		switchThemeMode("normal");
	});
	
	$("#addThemeSecondButton").click(function() {
		addOneTheme();
		switchThemeMode("normal");
	});
	
	$("#themeSelector").on("change", updateSecondThemeDropDown);
	
	$("#themesGroup").on("click", "li img.deleteImageLink", removeOneTheme);

}

function switchThemeMode(mode) {
	if (mode == "normal") {
		$("#themeInputCommand").hide();
		$("#addThemeLink").toggle(500);
		
	} else {
		$("#addThemeLink").hide();
		$("#themeInputCommand").toggle(250);
	}
}


// update the content of the sub-theme drop down according to the current conent of the theme dropdown
function updateSecondThemeDropDown() {
	
	$("#subThemeSelector option").remove();
	
	var currentTheme = $("#themeSelector").val();
	var themeDef = resolveThemeDef(currentTheme);
	
	if (themeDef != undefined) {
		for (var i = 0 ; i < themeDef.subThemes.length; i++) {
			var oneOption = $("<option />");
			oneOption.attr("value", themeDef.subThemes[i].id);
			oneOption.text(themeDef.subThemes[i].label);
			$("#subThemeSelector").append(oneOption);
		}
	}
}

function resolveThemeDef(themeId) {
	for (var i = 0 ; i < allPossibleThemes.length; i++) {
		if (allPossibleThemes[i].id == themeId) {
			return allPossibleThemes[i];
		}
	}
}

function resolveSubThemeDef(subThemeId, themeDef) {
	for (var i = 0 ; i < themeDef.subThemes.length; i++) {
		if (themeDef.subThemes[i].id == subThemeId) {
			return themeDef.subThemes[i];
		}
	}
}


function addOneTheme() {
	
	var themeId = $("#themeSelector").val();
	var subThemeId = $("#subThemeSelector").val();
	
	var addedSelectedTheme = {
			themeId: themeId,
			subThemeId: subThemeId 
	};
	
	if (!isThisThemeAlreadySelected(addedSelectedTheme)) {
		allThemes.push(addedSelectedTheme);
		themeModelUpdatedCallback(allThemes);
		graphicalAddOneTheme(addedSelectedTheme, false);
	}
	
}


function isThisThemeAlreadySelected(oneTheme) {
	
	var alreadyExists = false;
	$(allThemes).each(function() {
		if (this.themeId==oneTheme.themeId && this.subThemeId==oneTheme.subThemeId) {
			alreadyExists = true;
		}
		
	});
	return alreadyExists;
	
}


function graphicalAddOneTheme(selectedTheme, immediate) {
	
	var themeDef = resolveThemeDef(selectedTheme.themeId);
	var subThemDef = resolveSubThemeDef(selectedTheme.subThemeId, themeDef);
	
	// TODO: use translations here
	var addedThemeText = themeDef.label;
	if (subThemDef != undefined && subThemDef.id != "other") {
		addedThemeText += " (" + subThemDef.label + ")"
	}
	
	var newThemeRow = $("#hiddenThemeTemplate").clone().removeAttr("id");
	newThemeRow.find("span").text(addedThemeText);
	
	newThemeRow.data(selectedTheme);
	
	$("#themesGroup").append(newThemeRow);
	
	if (immediate) {
		$("#themesGroup li:last").show(0);
	} else {
		$("#themesGroup li:last").show(250);
	}
}


function removeOneTheme(event) {
	
	var themeRow = $(event.target).parent();
	
	var removedSelectedTheme = themeRow.data();
	for (var removedIndex = 0 ; removedIndex < allThemes.length; removedIndex++) {
		var oneTheme = allThemes[removedIndex];
		if (oneTheme.themeId==removedSelectedTheme.themeId && oneTheme.subThemeId==removedSelectedTheme.subThemeId) {
			allThemes.splice(removedIndex, 1);
			themeModelUpdatedCallback(allThemes);
		}
	}
	themeRow.remove();
}

