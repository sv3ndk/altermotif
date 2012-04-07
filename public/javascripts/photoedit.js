var photoEditLib = {
		
	PhotoEditController : function(photoEditActionController) {
	
		this.selectedPhotoIndex = 0;
		this.isPhotoInterractionEnabled = true;
		this.photoEditActionController = photoEditActionController;
		
		this.init = function() {

			var self = this;
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
			$("#profilePhotoMpcaptionDiv span.dabLink, #profilePhotoMpcaptionDiv img.iconLink").click(function() {
				self.whenUserClicksEditPhotoCaption();
				if (isPhotoInterractionEnabled) {
					$("#editedCaption").val($("#photoCaption").text());
					$("#editCaptionDialog").dialog("open");
				}
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
			var self = this;
			var fullSizePhotoAddr = $(event.target).next().text();
			
			$("#profileMPCentralPhoto").attr("src", fullSizePhotoAddr);
			
			$("#profileMPThumbContainer img").removeClass("yoohoo");
			$(event.target).addClass("yoohoo");
	
			if ($(event.target).hasClass("profileEmptyImage")) {
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
			
			//mainPhotoIndex;
			
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
		};
		
		//////////////////////////////
		//////////////////////////////
		this.whenUserClicksDeletePhoto = function() {
			if (this.isPhotoInterractionEnabled) {
				$("#dialogConfirmDeletePhoto").dialog("open");
			}
		};
		
		
		//////////////////////////////
		this.whenUserClicksEditPhotoCaption = function() {
			if (this.isPhotoInterractionEnabled) {
				$("#editedCaption").val($("#photoCaption").text());
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
		
}