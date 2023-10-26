$(function()
{
	var pbar = $('#progressBar'), currentProgress = 0;
	function trackUploadProgress(e)
	{
		if(e.lengthComputable)
		{
			currentProgress = (e.loaded / e.total) * 100; // Amount uploaded in percent
			$(pbar).width(currentProgress+'%');

			if( currentProgress == 100 )
			console.log('Progress : 100%');
		}
	}

	function uploadFile()
	{
		var formdata = new FormData($('#form')[0]);
		$.ajax(
		{
			url:'/uploadDatabaseFile',
			type:'post',
			data:formdata,
			xhr: function()
			{
				// Custom XMLHttpRequest
				var appXhr = $.ajaxSettings.xhr();

				// Check if upload property exists, if "yes" then upload progress can be tracked otherwise "not"
				if(appXhr.upload)
				{
					// Attach a function to handle the progress of the upload
					appXhr.upload.addEventListener('progress',trackUploadProgress, false);
				}
				return appXhr;
			},
			success:function(){
			console.log('File uploaded !');
//			setTimeout(function(){
//			window.location.href = "http://localhost:8080/UploadDone";
//			$(".btn").removeClass('disabled');
//			document.getElementById("btd1").style['pointer-events'] = 'auto';
//			}, 3000);
//			},
			error:function(){ console.log('Error !'); },

			// Tell jQuery "Hey! don't worry about content-type and don't process the data"
			// These two settings are essential for the application
			contentType:false,
			processData: false
		})
	}

	$('#form').submit(function(e)
	{
		e.preventDefault();
		$(pbar).width(0).addClass('active');
//		$(".btn").addClass('disabled');
//		$('[data-toggle=collapse]').prop('disabled',true);
//		document.getElementById("btd1").style['pointer-events'] = 'none';
		uploadFile();
	});
})