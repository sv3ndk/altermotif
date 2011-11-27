function init() {

	
	// this is present in languages.js
	initAllPossibleLanguagesMap();
	
	insertLangugeName();
	
	
}


function insertLangugeName() {
	
	var languageCode = $(".languageCell").find("span.hidden").text();
	$(".languageCell span.langugeLabel").text(allPossibleLanguagesMap[languageCode]);

	
}