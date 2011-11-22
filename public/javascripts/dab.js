//javascrip common to all pages (top menu)

var timeout = 500;
var closetimer = 0;
var ddmenuitem = 0;

function initMasterLayout(updateLanguageAction) {

	initChangeLanguageLogic(updateLanguageAction);
	
	
	$("#topDropDownMenu ul li").hover(function() {

		$(this).addClass("hover");
		$('ul:first', this).css('visibility', 'visible');

	}, function() {

		$(this).removeClass("hover");
		$('ul:first', this).css('visibility', 'hidden');

	});
	
	
	registerToggleDabLinks();
	
}


function initChangeLanguageLogic(updateLanguageAction) {
	$("#selectOneLanguageDropdown").change(function(event) {
		
		$('#selectedLg').load(
				updateLanguageAction({selection: $("#selectOneLanguageDropdown").val()}), 
				function () {
					// we reload the complete page after a language setting: there are messages everywhere!
					window.location.href=window.location.href;
				});
									
	});

}


function registerToggleDabLinks() {
	
	$(".pageContainer").on("click", "span.toggleLink", function(event) {
		var toggelable = $($(event.target).siblings(".toggleContainer"));
		toggelable.toggle("blind", {}, 250);
	});
}


// common js validation method
function dabValidateURL(textval) {
	  var urlregex = new RegExp("^(http:\/\/www.|https:\/\/www.|www.){1}([0-9A-Za-z]+\.)");
	  return urlregex.test(textval);
	}