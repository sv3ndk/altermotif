/**
 * 
 */

// by default, this first photo is selected
var selectedPhotoIndex = 0;

var isPhotoInterractionEnabled = true;

function init() {

	initClickOnTHumbmail();

	initDeletePhotoLink();

	initUploadPhoto();

	initClickOnSetProfilePhoto();

	initClickOnSetPhotoCaption(okText, cancelText);

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
			$("#deletePhotoLink").removeClass("dabLink");
			$("#deletePhotoLink").addClass("dabLinkDisabled");
			$("#deletePhotoButton").removeClass("iconLink");
			$("#deletePhotoButton").addClass("iconLinkInactive");
			$("#photoCaption").text(setCaptionText);
			$("#photoCaption").removeClass("dabLink");
			$("#photoCaption").addClass("dabLinkDisabled");
			$("#photoCaptionImage").removeClass("iconLink");
			$("#photoCaptionImage").addClass("iconLinkInactive");
			isPhotoInterractionEnabled = false;
		} else {
			$("#deletePhotoLink").removeClass("dabLinkDisabled");
			$("#deletePhotoLink").addClass("dabLink");
			$("#deletePhotoButton").removeClass("iconLinkInactive");
			$("#deletePhotoButton").addClass("iconLink");
			
			var newCaption = $(this).attr("alt");
			if (newCaption == "") {
				$("#photoCaption").text(setCaptionText);
			} else {
				$("#photoCaption").text(newCaption);
			}
			
			$("#photoCaption").text();
			$("#photoCaption").removeClass("dabLinkDisabled");
			$("#photoCaption").addClass("dabLink");
			$("#photoCaptionImage").removeClass("iconLinkInactive");
			$("#photoCaptionImage").addClass("iconLink");

			isPhotoInterractionEnabled = true;
		}

		// if this thumbnail is the first photo: this is already the profile photo => disactivate the "set photo as profile photo" link, otherwise
		// activate it
		if ($(this).attr("src") == $("#profileMPThumbContainer img:first").attr("src")) {
			$("#setProfilePhotoButton").removeClass("iconLink");
			$("#setProfilePhotoButton").addClass("iconLinkInactive");
			$("#setProfilePhotoLink").removeClass("dabLink");
			$("#setProfilePhotoLink").addClass("dabLinkDisabled");
		} else {
			$("#setProfilePhotoButton").removeClass("iconLinkInactive");
			$("#setProfilePhotoButton").addClass("iconLink");
			$("#setProfilePhotoLink").removeClass("dabLinkDisabled");
			$("#setProfilePhotoLink").addClass("dabLink");
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

function initDeletePhotoLink() {

	$("#deletePhotoLink.dabLink, #deletePhotoButton.iconLink").click(function(event) {

		if (isPhotoInterractionEnabled) {

			$("#dialogConfirmDeletePhoto").dialog({
				resizable : false,
				width : 350,
				modal : true,
				"buttons" : [ {
					text : okText,
					click : function() {
						$("#hiddenDeletePhotoForm\\:deletedPhotoIndex").val(selectedPhotoIndex);
						$("#hiddenDeletePhotoForm\\:link").click();
						$(this).dialog("close");
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

		return null;
	});
}

function initUploadPhoto() {

	$("#pleaseWaitDialog").dialog({
		autoOpen : false,
		closeOnEscape : false,
		modal : true,
		beforeClose : function(event, ui) {
			// prevents the dialog to close
			return false;
		}
	});

	$("#uploadPhotoButton.iconLink, #uploadPhotoLink.dabLink").click(function() {
		$("#theFile").click();
	});

	$("#theFile").change(function() {
		$("#pleaseWaitDialog").dialog("open");
		$("#hiddenSubmitPhotoButton").click();
	});
}

function initClickOnSetProfilePhoto() {

	$("#setProfilePhotoLink, #setProfilePhotoButton").click(function(event) {

		if ($(this).hasClass("dabLink") || $(this).hasClass("iconLink")) {
			$("#hiddenSetProfilePhotoForm\\:photoIndex").val(selectedPhotoIndex);
			$("#hiddenSetProfilePhotoForm\\:hiddenButton").click();
		}

	});

};

function initClickOnSetPhotoCaption(okText, cancelText) {

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
				$("#hiddenEditPhotoCaptionForm\\:photoIndex").val(selectedPhotoIndex);
				$("#hiddenEditPhotoCaptionForm\\:photoCaption").val($("#editedCaption").val());

				// the hidden form is not submitted if we do not
				// close the dialog before...
				$(this).dialog("close");
				$("#hiddenEditPhotoCaptionForm\\:hiddenButton").click();
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
