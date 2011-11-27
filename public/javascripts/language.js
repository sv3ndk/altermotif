// this is a map of languagge iso codes (e.g. "en") to language name "ENglish", in the language chosen by the user
var allPossibleLanguagesMap;

// => just call allPossibleLanguagesMap['fr'] to get the name of the French language in the current user language 

//list of all languages (for auto completion)
var allPossibleLanguagesList;

function initAllPossibleLanguagesMap() {
	
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

}