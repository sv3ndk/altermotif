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