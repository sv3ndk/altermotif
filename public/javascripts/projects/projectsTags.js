// we put here the logic for the tag specification in the forms (the "add tag" link + "add" and "cancel" buttons,...l)
// this is directly coupled with the grapical part of the input in inputTag.html


// this is the list of currently encoded tags bu the user
var allTags ;

// this function must accept an array of String: it is provided by the "main" script: this function is called from here anytime we
// suggest to the main script to update its own model
var tagModelUpdatedCallback;


function initAddTagLogic(allTagsInitValue, callback) {
	
	if (allTagsInitValue == undefined) {
		allTags = [];
	} else {
		allTags = allTagsInitValue;
		for ( var oneKey in allTags) {
			graphicalAddOneTag(allTags[oneKey], true);
		}
	}

	tagModelUpdatedCallback = callback;

	$("#addTagLink").click(function() {
		switchTagMode("enteringTag");
	});

	$("#addTagCancelButton").click(function() {
		switchTagMode("normal");
	});
	
	$("#addTagSecondButton").click(function() {
		addOneTag();
		switchTagMode("normal");
	});
	
	
	// click on the delete icon
	$("#tagGroup").on("click", "img.deleteImageLink", function(event){
		var removedTagValue = $(event.target).prev().text();
		
		var removedIndex = 0;
		for (var oneKey in allTags) {
			if (allTags[oneKey] == removedTagValue) {
				break;
			}
			removedIndex ++;
		}

		allTags.splice(removedIndex, 1);
		
		// calls back the "main" script to notify the update
		modelUpdatedCallback(allTags);
		$(event.target).parent().remove();
	});

	
}

function switchTagMode(mode) {
	if (mode == "normal") {
		$("#tagInputCommand").hide();
		$("#addTagLink").toggle(500);
		
	} else {
		$("#addTagLink").hide();
		$("#tagInputCommand").toggle(250);
		$("#addTagInput").val("").focus();
	}
}

function addOneTag() {
	var addedTag = $("#addTagInput").val();
	doAddOneTag(addedTag);
}


function doAddOneTag(addedTag) {
	if (addedTag != null && addedTag != "" && ! isTagAlreadyPresent(addedTag)) {
		allTags.push(addedTag);
		// calls back the "main" script to notify the update
		tagModelUpdatedCallback(allTags);
		graphicalAddOneTag(addedTag, false);
	}	
}


function isTagAlreadyPresent(tag) {
	var isPresent = false;
	
	$(allTags).each(function() {
		if (this == tag) {
			isPresent = true;
		}
	});
	
	return isPresent;
}



function graphicalAddOneTag(addedTag, immediate) {
	var newTagRow = $("#hiddenTagTemplate").clone().removeAttr("id");
	newTagRow.find("span").text(addedTag);
	$("#tagGroup").append(newTagRow);
	
	if (immediate) {
		$("#tagGroup li:last").show(0);
	} else {
		$("#tagGroup li:last").show(250);
	}
}
