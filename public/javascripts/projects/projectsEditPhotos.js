$(document).ready(function() {
	
	// project specific implementation of the actions that are triggered from edit photo screen 
	var photoEditActionController = new projectPhotoLib.PhotoEditActionController();
	
	// main controller of the screen (which is common to all "edit photo" screens (profile, projects, group)
	var photoEditController = new photoEditLib.PhotoEditController(photoEditActionController);
	
});


var projectPhotoLib =  {

	PhotoEditActionController :function() {
		
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
							{photoIndex: selectedPhotoIndex, photoCaption: newCaption}
							),
							
					function(data) {
						$("#photoCaption").text(newCaption)

						$("#profileMPThumbContainer img").each(function(index, imageElement) {
							if (index == selectedPhotoIndex) {
								$(imageElement).attr("alt", newCaption);
							}
						});
						
						$("	#editCaptionDialog").dialog("close");
					}
				);
		};
		this.init();
	},
}


