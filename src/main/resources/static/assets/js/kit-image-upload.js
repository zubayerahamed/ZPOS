$(document).ready(function(){
	$('body').on('click', '.avatar-upload', function(e) {
		e.preventDefault();
		$(this).siblings(".avatar-image:hidden").trigger('click');
		submitUrl = $(this).attr('href');
	});

	var $modal = $('#avatar-modal');
	var image = document.getElementById('image');
	var cropper;
	var submitUrl;

	$("body").on("change", ".avatar-image", function(e) {
		var files = e.target.files;
		var done = function(url) {
			image.src = url;
			$modal.modal('show');
		};

		var reader;
		var file;
		var url;

		if (files && files.length > 0) {
			file = files[0];

			if (URL) {
				done(URL.createObjectURL(file));
			} else if (FileReader) {
				reader = new FileReader();
				reader.onload = function(e) {
					done(reader.result);
				};
				reader.readAsDataURL(file);
			}
		}
	});

	// Init cropper js when modal show and destroy cropper js when modal hide
	$modal.on('shown.bs.modal', function() {
		cropper = new Cropper(image, {
			aspectRatio: 1,
			viewMode: 1,
			preview: '.preview'
		});
	}).on('hidden.bs.modal', function() {
		cropper.destroy();
		cropper = null;
	});

	$('.crop-cancel').off('click').on('click', function() {
		$modal.modal('hide');
	});

    $("#crop").click(function() {
        canvas = cropper.getCroppedCanvas({
            width: 160,
            height: 160,
        });
    
        canvas.toBlob(function(blob) {
            url = URL.createObjectURL(blob);
            var reader = new FileReader();
            reader.readAsDataURL(blob);
    
            reader.onloadend = function() {
                var base64data = reader.result;
                loadingMask2.show();
                $.ajax({
                    type: "POST",
                    dataType: "json",
                    url: submitUrl,
                    data: {
                        'id': $('#entityid').val(),
                        'image': base64data
                    },
                    success: function(data) {
                        loadingMask2.hide();
                        if (data.status == 'SUCCESS') {
                            $modal.modal('hide');
                            
                            if(data.displayMessage == true) showMessage(data.status.toLowerCase(), data.message);

							if(data.triggermodalurl){
								modalLoader(getBasepath() + data.triggermodalurl, data.modalid);
							} else {
								if(data.reloadsections != undefined && data.reloadsections.length > 0){
									$.each(data.reloadsections, function (ind, section) {
										if(section.postData.length > 0){
											var data = {};
											$.each(section.postData, function(pi, pdata){
												data[pdata.key] = pdata.value;
											})
											sectionReloadAjaxPostReq(section, data);
										} else {
											sectionReloadAjaxReq(section);
										}
									});
								} else if(data.reloadurl){
									doSectionReloadWithNewData(data);
								} else if(data.redirecturl){
									setTimeout(() => {
										window.location.replace(getBasepath() + data.redirecturl);
									}, 1000);
								}
							}

						} else {
							if(data.displayErrorDetailModal){
								$('#errorDetailModal').modal('show');
			
								sectionReloadAjaxReq({
									id : data.reloadelementid,
									url : data.reloadurl,
								});
							}
			
							if(data.status) showMessage(data.status.toLowerCase(), data.message);
			
						}
                    },
                    error: function(jqXHR, status, errorThrown) {
                        loadingMask2.hide();
                        showMessage("error", jqXHR.responseJSON.message);
                    }
                });
            }
        });
    });
})