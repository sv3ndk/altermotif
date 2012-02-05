// js code common to all pages 

///////////////////////////////////////
// main db library


var dabUtils =  {
	
	// associate a jquery datepicker to any element matching this jqSelector
	makeInputDatePicker : function (jqSelector, yearRange) {
		$(jqSelector).datepicker({
			changeMonth : true,
			changeYear : true,
			dateFormat : "dd/mm/yy",
			yearRange : yearRange,
			showAnim : "blind"
		});
	},
	
	
	// simple constructor to contain a translated value: name is the key and localizedName is the equivalent in the language of the current user
	LocalizedTaskStatus : function(name, localizedName) {
		this.name = name,
		this.localizedName = localizedName
	},
	
	
	parseJsonStringIntoObject: function(jqSelector) {

		var htmlValue = $(jqSelector).val();
		if (htmlValue != null && htmlValue != "" && htmlValue != "null")  {
			var parsed = JSON.parse(htmlValue);
			return parsed;
		} else {
			return [];
		}	
	},
	
}



//////////////////////////////////////////
//common Knockout stuff
//////////////////////////////////////////

var commonKOStuff = {

	genericBeforeRemoveElement : function(elem) {
		if (elem.nodeType === 1) {
			$(elem).slideUp(function() {
				$(elem).remove();
			});
		}
	},

	genericAfterAddElement : function t(elem) {
		if (elem.nodeType === 1) {
			$(elem).hide().slideDown();
		}
	}
}






////////////////////////////////
// master menu init
var timeout = 500;
var closetimer = 0;
var ddmenuitem = 0;

function initMasterLayout(updateLanguageAction) {

	initChangeLanguageLogic(updateLanguageAction);
	
	// this is present in simpleActions.js
	initAskAndAct();
	
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
		
		var newLanguageCode = $("#selectOneLanguageDropdown").val();
		$('#selectedLg').load(
				updateLanguageAction({selection: newLanguageCode}), 
				function () {
					// we reload the complete page after a language setting: there are messages everywhere!
					if (typeof isTermsAndConditionsPage != 'undefined' ) {
						// in case of "terms and conditions", we have to relaod the page corresponding to the new language
						window.location.href=$("#hiddenLinksToTermsAndConditions a." +newLanguageCode).attr("href");
					} else if (typeof isPrivacyStatementPage != 'undefined') {
						// in case of "privacy statements", we have to relaod the page corresponding to the new language
						window.location.href=$("#hiddenLinksToPrivacyStatement a." +newLanguageCode).attr("href");
 					} else {
 						// in most cases, we just reload the current page
 						window.location.href=window.location.href;
					}
				});
									
	});

}


function registerToggleDabLinks() {
	
	$("body").on("click", "span.toggleLink", function(event) {
		var toggelable = $($(event.target).siblings(".toggleContainer"));
		toggelable.toggle("blind", {}, 250);
	});
}


///////////////////////////////////
// common js validation methods

function dabValidateURL(textval) {
	  var urlregex = new RegExp("^(http:\/\/www.|https:\/\/www.|www.){1}([0-9A-Za-z]+\.)");
	  return urlregex.test(textval);
}


// associate a jquery datepicker to any element matching this jqSelector
function makeInputDatePicker(jqSelector, yearRange) {
	$(jqSelector).datepicker({
		changeMonth : true,
		changeYear : true,
		dateFormat : "dd/mm/yy",
		yearRange : yearRange,
		showAnim : "blind"
	});
}

