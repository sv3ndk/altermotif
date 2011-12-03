function init() {

	
	// this is present in languages.js
	initAllPossibleLanguagesMap();
	
	insertLangugeName();

	
	// photo gallery
	$("a[rel='profilePhotos']").colorbox({
		transition : "none",
		width : "75%",
		height : "75%",
		photo : true
	});

	
}


function insertLangugeName() {
	
	var languageCode = $(".languageCell").find("span.hidden").text();
	$(".languageCell span.langugeLabel").text(allPossibleLanguagesMap[languageCode]);

	
}