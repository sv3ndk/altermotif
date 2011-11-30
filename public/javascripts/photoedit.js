var selectedPhotoIndex = 0;
var isPhotoInterractionEnabled = true;


function initPhotoEditMechanics() {
	initClickOnTHumbmail();

	initUploadPhoto();
	
	initDeletePhotoLink();
	
	initClickOnSetAsMainPhoto();
	
	initClickOnSetPhotoCaption();
	
	// simulates a click on the first image (in order to trigger the
	// initialization of the state of the page)
	$("#profileMPThumbContainer img:first").click();

}


function initClickOnTHumbmail() {

	$("#profileMPThumbContainer img").click(function(event) {
		
		var fullSizePhotoAddr = $(event.target).next().text();
		
		$("#profileMPCentralPhoto").attr("src", fullSizePhotoAddr);
		
		$("#profileMPThumbContainer img").removeClass("yoohoo");
		$(this).addClass("yoohoo");

		if ($(this).hasClass("profileEmptyImage")) {
			$("#deletePhotoLink, #photoCaption").removeClass("dabLink").addClass("dabLinkDisabled");
			$("#deletePhotoButton, #photoCaptionImage").removeClass("iconLink").addClass("iconLinkInactive");
			$("#photoCaption").text(setCaptionText);
			isPhotoInterractionEnabled = false;
		} else {
			$("#deletePhotoLink, #photoCaption").removeClass("dabLinkDisabled").addClass("dabLink");
			$("#deletePhotoButton, #photoCaptionImage").removeClass("iconLinkInactive").addClass("iconLink");
			
			var newCaption = $(this).attr("alt");
			if (newCaption == "") {
				$("#photoCaption").text(setCaptionText);
			} else {
				$("#photoCaption").text(newCaption);
			}
			
			$("#photoCaption").text();

			isPhotoInterractionEnabled = true;
		}

		// if this thumbnail is the first photo: this is already the profile photo => disactivate the "set photo as profile photo" link, otherwise
		// activate it
		if ($(this).attr("src") == $("#profileMPThumbContainer img:first").attr("src")) {
			$("#setAsMainPhotoImage").removeClass("iconLink").addClass("iconLinkInactive");
			$("#setAsMainPhotoLink").removeClass("dabLink").addClass("dabLinkDisabled");
		} else {
			$("#setAsMainPhotoImage").removeClass("iconLinkInactive").addClass("iconLink");
			$("#setAsMainPhotoLink").removeClass("dabLinkDisabled").addClass("dabLink");
		}

		// discovers the index of the photo which has been
		// selected
		$("#profileMPThumbContainer img").each(function(index, imageElement) {
			if ($(imageElement).hasClass("yoohoo")) {
				selectedPhotoIndex = index;
			}
		});
	});

}




function initUploadPhoto() {

	$("#pleaseWaitUploadDialog").dialog({
		autoOpen : false,
		closeOnEscape : false,
		modal : true,
		beforeClose : function(event, ui) {
			// prevents the dialog to close
			return false;
		}
	});

	$("#uploadPhotoButton.iconLink, #uploadPhotoLink.dabLink").click(function() {
		$("#hiddenUploadPhotoForm").show();
		$("#theFile").click();
		$("#hiddenUploadPhotoForm").hide();

	});

	$("#theFile").change(function() {
		$("#pleaseWaitUploadDialog").dialog("open");
		$("#hiddenUploadPhotoForm form").submit();
	});
}




function initDeletePhotoLink() {

	$("#deletePhotoLink.dabLink, #deletePhotoButton.iconLink").click(function(event) {

		if (isPhotoInterractionEnabled) {
			$("#dialogConfirmDeletePhoto").dialog("open");
		}

		return null;
	});
	
	
	$("#dialogConfirmDeletePhoto").dialog( {
		resizable : false,
		autoOpen : false,
		width : 350,
		modal : true,
		"buttons" : [ {
			text : okText,
			click : function() {
				$("#hiddenDeletePhotoForm #deletedPhotoIdx").val(selectedPhotoIndex);
				$("#hiddenDeletePhotoForm form").submit();
			}
			
		},

		{
			text : cancelText,
			click : function() {
				$(this).dialog("close");
			}
		}
		],
	});

}



function initClickOnSetAsMainPhoto() {
	$("#setAsMainPhotoImage, #setAsMainPhotoLink").click(function(event) {
		if ($(this).hasClass("dabLink") || $(this).hasClass("iconLink")) {
			$("#hiddenSetAsMainPhotoForm #photoIndex").val(selectedPhotoIndex);
			$("#hiddenSetAsMainPhotoForm form").submit();
		}
	});
}




function initClickOnSetPhotoCaption() {

	$("#profilePhotoMpcaptionDiv span.dabLink, #profilePhotoMpcaptionDiv img.iconLink").click(function() {
		if (isPhotoInterractionEnabled) {
			$("#editedCaption").val($("#photoCaption").text());
			$("#editCaptionDialog").dialog("open");
		}
	});

	$("#editCaptionDialog").dialog({
		autoOpen : false,
		closeOnEscape : true,
		width : 650,
		modal : true,
		"buttons" : [ {
			text : okText,
			click : function() {
				var newCaption = $("#editedCaption").val();
				doUpdatePhotoCaption(selectedPhotoIndex, newCaption);
			}
		},

		{
			text : cancelText,
			click : function() {
				$(this).dialog("close");
			}
		}
		],
	});
}
