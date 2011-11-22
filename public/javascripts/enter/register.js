$(document).ready(function() {
	// set focus on the first input field
	$("#username").focus();

	$("#dateOfBirth").datepicker({
		changeMonth : true,
		changeYear : true,
		dateFormat : "dd/mm/yy",
		yearRange : '-130:+0',
		showAnim : "blind"
	});

});

function initCaptcha(registerPageCaptchaTitle, hophop) {
	$('#registerButton').buttonCaptcha({
		codeWord : hophop,
		captchaHeader : registerPageCaptchaTitle,
		captchaTip : registerPageCaptchaTitle,
		verifyInput : true
	});
}

