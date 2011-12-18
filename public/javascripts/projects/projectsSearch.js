function init() {
	initTagMechanics();
	initThemeLogic();
}


/////////////////////////////////////////
// tags

function initTagMechanics() {

	// when clicking on the tag cloud
	$("#projectTagContainer a").click(function() {

		// this is defined in projectsTags, it should update the model, then call us back for updating here again 
		doAddOneTag($(event.target).text());
	});
	

	// when clicking on the "add tag" link
	initAddTagLogic(undefined, updateAllTags);
}

function updateAllTags(newAllTagsValue) {
	
	$(newAllTagsValue).each(function() {
		addOneTagInput(this);
	});
	
}


function addOneTagInput(addedTag) {

	if (addedTag != undefined && addedTag != "") {
		var thisTagIsAlreadyPresent = false;
		$("#hiddenTagInputs input").each(function(index, value) {
			if ($(value).val() == addedTag) {
				thisTagIsAlreadyPresent = true;
			}
			
		});
		
		if (!thisTagIsAlreadyPresent) {
			var addedTagInput = $("<input type='hidden' name='r.tag'/>");
			addedTagInput.val(addedTag);
			$("#hiddenTagInputs").append(addedTagInput);
		}
	}
}


/////////////////////////////////////////////////////
// 

function initThemeLogic() {
	// this is the init function defined in projectThemess.js
	initAddThemeLogic(undefined, updateAllThemesHiddenForm);
}

//this is called back from projectThemes.js any time the list of chosen themes changes
function updateAllThemesHiddenForm(newAllThemesValue) {
	$("#hiddenAllThemesJson").val(JSON.stringify(newAllThemesValue));
}
