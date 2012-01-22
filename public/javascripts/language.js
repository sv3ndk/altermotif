


var dabLanguageLib = {
		
	LanguageMapper : function() {
		
		this.allPossibleLanguagesMap = new Array();;
		this.allPossibleLanguagesMap_reverse = new Array();;
		
		//list of all languages (for auto completion)
		this.allPossibleLanguagesList= [];
		
		this.init = function(allPossibleLanguages) {
			// builds the mapping between language code and language human name
			for ( var oneKey in allPossibleLanguages) {
				this.allPossibleLanguagesMap[allPossibleLanguages[oneKey].code] = allPossibleLanguages[oneKey].name;
				this.allPossibleLanguagesMap_reverse[allPossibleLanguages[oneKey].name] = allPossibleLanguages[oneKey].code;
				this.allPossibleLanguagesList.push(allPossibleLanguages[oneKey].name);
			}
		};
		
		
		this.resolveLanguageOfCode = function (isocode) {
			return this.allPossibleLanguagesMap[isocode];
		};

		this.resolveCodeOfLanguage = function (language) {
			return this.allPossibleLanguagesMap_reverse[language];
		};
	},
		
};











//////////////////
// DEPRECATED

// this is a map of languagge iso codes (e.g. "en") to language name "ENglish", in the language chosen by the user
// => just call allPossibleLanguagesMap['fr'] to get the name of the French language in the current user language 
var allPossibleLanguagesMap = new Array();;

var allPossibleLanguagesMap_reverse = new Array();;

//list of all languages (for auto completion)
var allPossibleLanguagesList= [];


function initAllPossibleLanguagesMap() {
	
	// builds the mapping between language code and language human name
	allPossibleLanguages = JSON.parse($("#allPossibleLanguageNames").text());
	for ( var oneKey in allPossibleLanguages) {
		allPossibleLanguagesMap[allPossibleLanguages[oneKey].code] = allPossibleLanguages[oneKey].name;
		allPossibleLanguagesMap_reverse[allPossibleLanguages[oneKey].name] = allPossibleLanguages[oneKey].code;
		allPossibleLanguagesList.push(allPossibleLanguages[oneKey].name);
	}

}