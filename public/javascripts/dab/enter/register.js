$(document).ready(function() {
	// set focus on the first input field
	$("#username").focus();

	// this is in dab.js
	dabUtils.makeInputDatePicker("#dateOfBirth", '-130:+0');
	
});

function initCaptcha(registerPageCaptchaTitle, hophop) {
	$('#registerButton').buttonCaptcha({
		codeWord : hophop,
		captchaHeader : registerPageCaptchaTitle,
		captchaTip : registerPageCaptchaTitle,
		verifyInput : true
	});
}

