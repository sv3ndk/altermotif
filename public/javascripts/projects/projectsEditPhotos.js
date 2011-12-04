

function init() {

	// this is photoedit.js
	initPhotoEditMechanics();
	
}

//this is called back from the photoedit.js
function doUpdatePhotoCaption(selectedPhotoIndex, newCaption) {
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

}
