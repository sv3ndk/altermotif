var geocoder;
var map;
var marker;

var okLabelValue;
var cancelLabelValue;
var warningUnsavedChangesTextValue;

// this is a map of languagge iso codes (e.g. "en") to language name "ENglish", in the language chosen by the user
var allPossibleLanguagesMap;

// reverve of the above: mapping from the human name to the code
var allPossibleLanguagesMap_reverse;

// list of all languages (for auto completion)
var allPossibleLanguagesList;

// mappting between a level (0, 1 or 2) and the name of this level
var allPossibleLevelNamesMap;

// languages of the profile: this is udpated in real type as the user modifies his list of langauges
var profileLanguages ; 


// ------------------------------
// ------------------------------

function init(oklabel, cancelLabel, warningUnsavedChangesText) {
	okLabelValue = oklabel;
	cancelLabelValue = cancelLabel;
	warningUnsavedChangesTextValue = warningUnsavedChangesText;

	registerDatePicker();
	initLanguageMechanics();

	initUncleanNavigationAwayWarning();
	initCityEdition();
	
	initSelectGender();

};

var geocoder;
var map;
var marker;

function initCityEdition() {

	initLat = $("#locationLat").val();
	initLong = $("#locationLong").val();

	initializeGeoCoder(initLat, initLong);
}

function initializeGeoCoder(initLat, initLong) {
	var latlng = new google.maps.LatLng(initLat, initLong);
	var options = {
		zoom : 8,
		center : latlng,
		mapTypeId : google.maps.MapTypeId.ROADMAP
	};

	geocoder = new google.maps.Geocoder();

	map = new google.maps.Map(document.getElementById("city_map_canvas"), options);

	marker = new google.maps.Marker({
		map : map,
		draggable : false
	});

	marker.setPosition(latlng);
	map.setCenter(latlng);

	$("#location").autocomplete({
		// This bit uses the geocoder to fetch address values
		source : function(request, response) {
			geocoder.geocode({
				'address' : request.term
			}, function(results, status) {
				response($.map(results, function(item) {
					return {
						label : item.formatted_address,
						value : item.formatted_address,
						latitude : item.geometry.location.lat(),
						longitude : item.geometry.location.lng()
					};
				}));
			});
		},
		// This bit is executed upon selection of an address
		select : function(event, ui) {
			chosenLatLng = new google.maps.LatLng(ui.item.latitude, ui.item.longitude);

			$("#locationLat").val(ui.item.latitude);
			$("#locationLong").val(ui.item.longitude);
			$("#location").val(ui.item.label);

			marker.setPosition(chosenLatLng);
			map.setCenter(chosenLatLng);

		}
	});

}

function initLanguageMechanics() {

	// builds the mapping between language code and language human name
	var allPossibleLanguageNames = $("#allPossibleLanguageNames").text();
	allPossibleLanguages = JSON.parse(allPossibleLanguageNames);
	allPossibleLanguagesMap = new Array();
	allPossibleLanguagesMap_reverse = new Array();
	allPossibleLanguagesList = [];
	for ( var oneKey in allPossibleLanguages) {
		allPossibleLanguagesMap[allPossibleLanguages[oneKey].code] = allPossibleLanguages[oneKey].name;
		allPossibleLanguagesMap_reverse[allPossibleLanguages[oneKey].name] = allPossibleLanguages[oneKey].code;
		allPossibleLanguagesList.push(allPossibleLanguages[oneKey].name);
	}

	// builds the mapping between level code and level human name
	var allPossibleLevelNames = $("#allPossibleLevelNames").text();
	allPossibleLevelNamesMap = JSON.parse(allPossibleLevelNames);

	// initiliation of the form: let's display the languages already in the profile of this user
	allLanguagesOfThisUser = $("#languages").val();
	if (allLanguagesOfThisUser != null && allLanguagesOfThisUser != "") {
		profileLanguages = JSON.parse(allLanguagesOfThisUser);
		
		for ( var oneKey in profileLanguages) {
			graphicalAddOneLanguage(profileLanguages[oneKey].name, profileLanguages[oneKey].level)
		}
	}
	
	
	initLanguageButtons();
}


// add one row in the html for dispalying this language +`a drop down to allow changeing the level
function graphicalAddOneLanguage(languageCode, languageLevel) {
	var el = "<li style='display: none'><div class=\"languageLabel\">"
	el += allPossibleLanguagesMap[languageCode];
	el += "</div>";

	el += "<select class='rightspaced' size='1' >";
	
	for (var i = 0; i < 3 ; i ++) {
		if (languageLevel == i) {
			el += "<option value='" + i + "' selected='selected'>" + allPossibleLevelNamesMap[i] + "</option>";
		} else {
			el += "<option value='" + i + "'>" + allPossibleLevelNamesMap[i] + "</option>";
		}
	}

	el += "</select></li>";
	
	$("#languageGroup").append(el);
	$("#languageGroup li:last").show(250);
	$("#languageGroup li:last").append($("#trashImg").clone().removeAttr("id").show());
	//$("#languageGroup").append("<br />");
}




function initLanguageButtons() {
	
	$("#addLanguageInput, #addLanguageSecondButton, #cancelAddLanguageButton").hide();
	
	$("#addALanguageLink").click(function() {
		switchLanguageMode("enteringLanguage");
	});

	$("#cancelAddLanguageButton").click(function() {
		switchLanguageMode("normal");
	});

	$("#addLanguageInput").autocomplete({
		source : allPossibleLanguagesList
	});
	
	$("#addLanguageSecondButton").click(function() {

		var chosenLanguage = $("#addLanguageInput").val();
		if (chosenLanguage != null) {
			var chosenCode = allPossibleLanguagesMap_reverse[chosenLanguage];
			if (chosenCode != null) {
				if (!isThisLanguageAlreadyChosen(chosenCode)) {
					if (profileLanguages == null) {
						profileLanguages = [];
					}
					
					profileLanguages.push({"name": chosenCode, "level": 2});
					graphicalAddOneLanguage(chosenCode, 2)
					updateSubmittedLanguagesJson();
				}
			}
		} 
		
		switchLanguageMode("normal");
	}) ;
	
	
	// makes sure that the levels submitted back to the server containt the values selected by the user
	$("#languageGroup").on("change", "select", function(event){
		
		var level = $(event.target).val();
		var code = allPossibleLanguagesMap_reverse[$(event.target).prev().text()]; 
		
		for (var oneKey in profileLanguages) {
			if (profileLanguages[oneKey].name == code) {
				profileLanguages[oneKey].level = level;
			}
		}
		updateSubmittedLanguagesJson();
		
	}
	)
	
	// click on the delete icon
	$("#languageGroup").on("click", "img.deleteImageLink", function(event){
		var code = allPossibleLanguagesMap_reverse[$(event.target).prev().prev().text()];
		
		var removedIndex = 0;
		for (var oneKey in profileLanguages) {
			if (profileLanguages[oneKey].name == code) {
				break;
			}
			removedIndex ++;
		}
		
		profileLanguages.splice(removedIndex, 1);
		updateSubmittedLanguagesJson();
		
		$(event.target).parent().remove();
		
	});
}


function switchLanguageMode( mode) {
	if (mode == "normal") {
		$("#addLanguageInput, #addLanguageSecondButton, #cancelAddLanguageButton").hide();
		$("#addALanguageLink").toggle(500);
		$("#addLanguageInput").val('');
		
		if (profileLanguages.length > 1) {
			
		}
		
	} else {
		$("#addALanguageLink").hide();
		$("#addLanguageInput, #addLanguageSecondButton, #cancelAddLanguageButton").toggle(250);
		$("#addLanguageInput").focus();
	}
}


// update the language data in the form, as it will be submitted back to the server 
function updateSubmittedLanguagesJson() {
	$("#languages").val(JSON.stringify(profileLanguages));
}



function isThisLanguageAlreadyChosen(someCode) {
	
	for (var oneKey in profileLanguages) {
		if (profileLanguages[oneKey].name == someCode) {
			return true;
		}
	}
	return false;
	
}


function registerDatePicker() {
	$("#dateOfBirth").datepicker({
		changeMonth : true,
		changeYear : true,
		dateFormat : "dd/mm/yy",
		yearRange : '-130:+0',
		showAnim : "blind"
	});
}

function scrollup() {
	$("html").scrollTop(0);
}

/**
 * 
 */
function initUncleanNavigationAwayWarning() {
	$(':input').bind("change", function() {
		updateDirtyState(true);
	});

	$("#editProfileButton").click(submitEditedProfile);
	$("#cancelProfileButton").click(submitCancelEditProfile);

}

// this is called when the user clicks on "update" => in that case we want to allow the navigation
function submitEditedProfile() {
	updateDirtyState(false);
	$("#editProfileContainer form").submit();
}

function submitCancelEditProfile() {
	updateDirtyState(false);
	$("#hiddenCancelForm form").submit();
}


function updateDirtyState(on) {
	if (on) {
		window.onbeforeunload = unloadMessage;
		$("#editProfileButton").removeAttr('disabled');
		$("#cancelEditProfileButton").removeAttr('disabled');
	} else {
		window.onbeforeunload = null;
		$("#editProfileButton").attr("disabled", "disabled");
		$("#cancelEditProfileButton").attr("disabled", "disabled");
	}
}

/**
 * @param on
 */
function unloadMessage(event) {

	e = event || window.event;

	// For IE and Firefox prior to version 4
	if (e) {
		e.returnValue = warningUnsavedChangesTextValue;
	}

	// For Safari
	return warningUnsavedChangesTextValue;

	// FF > 4 refuse to take this message into account...

}



function initSelectGender() {
	var selectedGender = $("#initSelectGender").text();
	
	if (selectedGender == "M") {
		$("#genderRadioMale").attr('checked', true);
	} else if (selectedGender == "F") {
		$("#genderRadioFemale").attr('checked', true);
	} else {
		$("#genderRadioUnspecified").attr('checked', true);
	}
	
	
}