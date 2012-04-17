/*
 * In order to use this, the html must provide the following functionalities:
 * 
 * HTML form used to perform the following actions:
 * 
 * hiddenDeletePhotoForm
 * hiddenSetAsMainPhotoForm
 * hiddenUploadPhotoForm
 * 
 * + a Play AJAX function to update the photo caption, called like this: updatePhotoCaptionAction
 * 
 */

var photoEditLib = {
		
	PhotoEditController : function() {
	
		this.selectedPhotoIndex = 0;
		this.isPhotoInterractionEnabled = true;
		this.photoEditActionController = new photoEditLib.PhotoEditActionController();
		
		var self = this;
		
		this.init = function() {

			this.initAllPopups();

			// click on a thumbnail
			$("#profileMPThumbContainer img").click(function(event) {
				self.whenUserClicksOnThumbnail(event);
			});
			
			// delete selected photo
			$("#deletePhotoLink.dabLink, #deletePhotoButton.iconLink").click(function(event) {
				self.whenUserClicksDeletePhoto();
				return null;
			});

			// set as main photo
			$("#setAsMainPhotoImage, #setAsMainPhotoLink").click(function(event) {
				if ($(this).hasClass("dabLink") || $(this).hasClass("iconLink")) {
					self.photoEditActionController.doSetAsMainPhoto(self.selectedPhotoIndex);
				}
			});

			// edit photo caption
			$("#photoCaptionLink, #photoCaptionImage").click(function(event) {
				self.whenUserClicksEditPhotoCaption(event);
			});
			
			// upload photo
			$("#uploadPhotoButton.iconLink, #uploadPhotoLink.dabLink").click(function() {
				self.photoEditActionController.doUploadPhoto();
			});

			// simulates a click on the first image (in order to trigger the
			// initialization of the state of the page)
			$("#profileMPThumbContainer img:first").click();

		};
		
		
		//////////////////////////////
		//////////////////////////////
		this.whenUserClicksOnThumbnail = function(event) {
			var fullSizePhotoAddr = $(event.target).next().text();
			
			$("#profileMPCentralPhoto").attr("src", fullSizePhotoAddr);
			
			$("#profileMPThumbContainer img").removeClass("yoohoo");
			$(event.target).addClass("yoohoo");
	
			if ($(event.target).hasClass("profileEmptyImage")) {
				$("#deletePhotoLink, #photoCaptionLink").removeClass("dabLink").addClass("dabLinkDisabled");
				$("#deletePhotoButton, #photoCaptionImage").removeClass("iconLink").addClass("iconLinkInactive");
				$("#photoCaption").text(setCaptionText);
				self.isPhotoInterractionEnabled = false;
			} else {
				$("#deletePhotoLink, #photoCaptionLink").removeClass("dabLinkDisabled").addClass("dabLink");
				$("#deletePhotoButton, #photoCaptionImage").removeClass("iconLinkInactive").addClass("iconLink");
	
				self.isPhotoInterractionEnabled = true;
			}
	
			// if this thumbnail is the first photo: this is already the profile photo => disactivate the "set photo as profile photo" link, otherwise
			// activate it
			
			var clickedIndex = $("#profileMPThumbContainer img").index($(this));
			
			if (clickedIndex == mainPhotoIndex) {
				$("#setAsMainPhotoImage").removeClass("iconLink").addClass("iconLinkInactive");
				$("#setAsMainPhotoLink").removeClass("dabLink").addClass("dabLinkDisabled");
			} else {
				$("#setAsMainPhotoImage").removeClass("iconLinkInactive").addClass("iconLink");
				$("#setAsMainPhotoLink").removeClass("dabLinkDisabled").addClass("dabLink");
			}
	
			// discovers the index of the photo which has been selected
			$("#profileMPThumbContainer img").each(function(index, imageElement) {
				if ($(imageElement).hasClass("yoohoo")) {
					self.selectedPhotoIndex = index;
				}
	  		});
			
			this.photoEditActionController.updateSelectedPhotoCaption($(event.target).next().next().text());
		};
		
		//////////////////////////////
		//////////////////////////////
		this.whenUserClicksDeletePhoto = function() {
			if (this.isPhotoInterractionEnabled) {
				$("#dialogConfirmDeletePhoto").dialog("open");
			}
		};
		
		
		//////////////////////////////
		this.whenUserClicksEditPhotoCaption = function(event) {
			if (this.isPhotoInterractionEnabled) {
				$("#editedCaption").val(self.photoEditActionController.selectedPhotoCaption);
				$("#editCaptionDialog").dialog("open");
			}
		}
		//////////////////////////////

		
		
		/////////////////////////////////////
		////////////////////////////////////
		
		this.initAllPopups = function() {
			
			var self = this;
			
			$("#dialogConfirmDeletePhoto").dialog( {
				resizable : false,
				autoOpen : false,
				width : 350,
				modal : true,
				"buttons" : [ {
					text : okLabelValue,
					click : function() {
						self.photoEditActionController.doDeletePhoto(self.selectedPhotoIndex);
					}
				},
		
				{
					text : cancelLabelValue,
					click : function() {
						$(this).dialog("close");
					}
				}
				],
			});
			
			$("#editCaptionDialog").dialog({
				autoOpen : false,
				closeOnEscape : true,
				width : 650,
				modal : true,
				"buttons" : [ {
					text : okLabelValue,
					click : function() {
						self.photoEditActionController.doUpdatePhotoCaption(self.selectedPhotoIndex, $("#editedCaption").val());
					}
				},
		
				{
					text : cancelLabelValue,
					click : function() {
						$(this).dialog("close");
					}
				}
				],
			});

			
		};
		this.init();
	},
	
	PhotoEditActionController :function() {
		
		this.selectedPhotoCaption;
		
		// this is ugly: TODO: use a proper model!
		this.imgOfEditedPhotoCaption;
		
		var self = this;
		
		this.init = function() {
			
			$("#pleaseWaitUploadDialog").dialog({
				autoOpen : false,
				closeOnEscape : false,
				modal : true,
				beforeClose : function(event, ui) {
					// prevents the dialog to close
					return false;
				}
			});
	
			$("#theFile").change(function() {
				$("#pleaseWaitUploadDialog").dialog("open");
				$("#hiddenUploadPhotoForm form").submit();
			});
			
		};
		
		
		this.updateSelectedPhotoCaption = function(newCaption) {
			
			this.selectedPhotoCaption = newCaption;
			if (this.selectedPhotoCaption == "" || this.selectedPhotoCaption == null) {
				$("#photoCaptionLink").text(setCaptionText);
			} else {
				$("#photoCaptionLink").text(this.selectedPhotoCaption);
			}
		};
		
		
		this.doDeletePhoto = function(deletedPhotoIndex) {
			$("#hiddenDeletePhotoForm #deletedPhotoIdx").val(deletedPhotoIndex);
			$("#hiddenDeletePhotoForm form").submit();
		};
		
		this.doSetAsMainPhoto = function(mainPhotoIndex) {
			$("#hiddenSetAsMainPhotoForm #photoIndex").val(mainPhotoIndex);
			$("#hiddenSetAsMainPhotoForm form").submit();
		};
		
		this.doUploadPhoto = function() {
			$("#hiddenUploadPhotoForm").show();
			$("#theFile").click();
			$("#hiddenUploadPhotoForm").hide();
		};
		
		this.doUpdatePhotoCaption = function(editedPhotoIndex, newCaption) {
			$.post(
				updatePhotoCaptionAction(
						{photoIndex: editedPhotoIndex, photoCaption: newCaption}
						),
						
				function(data) {
					self.updateSelectedPhotoCaption(newCaption);
					
					$("	#editCaptionDialog").dialog("close");
				}
			);
		};
		this.init();
	},
}