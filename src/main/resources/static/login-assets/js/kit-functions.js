/**
 * Application basepath
 */
function getBasepath(){
	var basePath = $('a.basePath').attr('href');
	basePath = basePath.split('/')[1];
	var href = location.href.split('/');
	if(basePath != ''){
		return href[0] + '//' + href[2] + '/' + basePath;
	}
	return href[0] + '//' + href[2];
}



// Toaster message
function showMessage(type, message, timeout) {
	if (type == undefined || type == "") return;

	if(timeout == undefined || timeout == "") timeout = 2500;

	new Noty({
		text: message,
		type: type,
		timeout: timeout
	}).show();
}




/**
 * Loading mask object
 * function1 : show  -- Show loading mask
 * function2 : hide  -- Hide loading mask
 */
var loadingMask2 = {
	show: function () {
		$("div#loadingmask2, div.loadingdots, div#loadingdots").removeClass("nodisplay");
	},
	hide: function () {
		$("div#loadingmask2, div.loadingdots, div#loadingdots").addClass("nodisplay");
	},
};


function sectionReloadAjaxReq(section) {
	loadingMask2.show();
	$.ajax({
		url: getBasepath() + section.url,
		type: "GET",
		success: function (data) {
			loadingMask2.hide();
			$("." + section.id).html("");
			$("." + section.id).append(data);
		},
		error: function (jqXHR, status, errorThrown) {
			loadingMask2.hide();
			showMessage("error", jqXHR.responseJSON.message);
		},
	});
}

function sectionReloadAjaxPostReq(section, data, callbackFunction) {
	loadingMask2.show();
	$.ajax({
		url: getBasepath() + section.url,
		type: "POST",
		data: data,
		success: function (data) {
			loadingMask2.hide();
			$("." + section.id).html("");
			$("." + section.id).append(data);

			if(callbackFunction != undefined){
				callbackFunction();
			}
		},
		error: function (jqXHR, status, errorThrown) {
			loadingMask2.hide();
			showMessage("error", jqXHR.responseJSON.message);
		},
	});
}

function submitMainForm(customurl, customform){
	if(customform == undefined && $('form#mainform').length < 1) return;

	var targettedForm = customform == undefined ? $('form#mainform') : customform;
	if(!targettedForm.smkValidate()) return;

	var submitUrl = (customurl == undefined || customurl == null) ? targettedForm.attr('action') : customurl;
	var submitType = targettedForm.attr('method');
	var formData = $(targettedForm).serializeArray();
	var enctype = targettedForm.attr('enctype');
	if(enctype == 'multipart/form-data'){
		submitMultipartForm(submitUrl, submitType, targettedForm, false);
		return;
	}

	loadingMask2.show();
	$.ajax({
		url : submitUrl,
		type :submitType,
		data : formData,
		success : function(data, status, xhr) {
			loadingMask2.hide();

			// For file download
			if(data.fileDownload == true){
				if("application/octet-stream" == data.mediaType.type + '/' + data.mediaType.subtype){
					var blob = new Blob([data.file], { type: data.mediaType.type + '/' + data.mediaType.subtype });
					var url = window.URL.createObjectURL(blob);
					var a = document.createElement('a');
					a.href = url;
					a.download = data.fileName;
					document.body.appendChild(a);
					a.click();
					window.URL.revokeObjectURL(url);
					showMessage(data.status.toLowerCase(), data.message);
					return;
				}
			}

			if(data.status == 'SUCCESS'){
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
		error : function(jqXHR, status, errorThrown){
			showMessage(status, "Something went wrong .... ");
			loadingMask2.hide();
		}
	});
}

function submitMultipartForm(submitUrl, submitType, targettedForm, frommodal){
	var files = $('#fileuploader').get(0).files;

	var formData = new FormData();
	if(files.length == 1){
		formData.append("file", files[0]);
	}
	for (var x = 0; x < files.length; x++) {
		formData.append("files[]", files[x]);
	}

	$.each($(targettedForm).serializeArray(), function(i, b){
		formData.append(b.name, b.value);
	})

	loadingMask2.show();
	$.ajax({
		url : submitUrl,
		type :submitType,
		data : formData,
		async: false,
		cache: false,
		processData: false,
		contentType: false,
		success : function(data) {
			loadingMask2.hide();
			if(data.status == 'SUCCESS'){
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

				showMessage(data.status.toLowerCase(), data.message);
			}
		}, 
		error : function(jqXHR, status, errorThrown){
			showMessage(status, "Something went wrong .... ");
			loadingMask2.hide();
		}
	});
}


function deleteRequest(customurl, data){
	loadingMask2.show();
	$.ajax({
		url : customurl,
		type : 'DELETE',
		data : data,
		success : function(data) {
			loadingMask2.hide();
			if(data.status == 'SUCCESS'){
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

				showMessage(data.status.toLowerCase(), data.message);
			}
		}, 
		error : function(jqXHR, status, errorThrown){
			showMessage(status, "Something went wrong .... ");
			loadingMask2.hide();
		}
	});
}



function actionPostRequest(customurl, data, timeout){
	loadingMask2.show();
	$.ajax({
		url : customurl,
		type : 'POST',
		data : data,
		success : function(data) {
			loadingMask2.hide();
			if(data.status == 'SUCCESS'){
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

				showMessage(data.status.toLowerCase(), data.message, timeout);
			}
		}, 
		error : function(jqXHR, status, errorThrown){
			showMessage(status, "Something went wrong .... ");
			loadingMask2.hide();
		}
	});
}


function generateOnScreenReport(customurl, data, reportType){
	if(reportType == undefined || reportType == '') reportType = "PDF";

	loadingMask2.show();
	$.ajax({
		url : customurl,
		type : 'POST',
		data : data,
		success : function(data) {
			loadingMask2.hide();
			var arrrayBuffer = base64ToArrayBuffer(data);
			if("PDF" == reportType){
				var blob = new Blob([arrrayBuffer], {type: "application/pdf"});
				var link = window.URL.createObjectURL(blob);
				window.open(link,'', 'height=650,width=840');
			} else {
				var blob = new Blob([arrrayBuffer], {type: "application/octetstream"});
				var isIE = false || !!document.documentMode;
				if (isIE) {
					window.navigator.msSaveBlob(blob, reportName + ".xls");
				} else {
					var url = window.URL || window.webkitURL;
					link = url.createObjectURL(blob);
					var a = $("<a />");
					a.attr("download", reportName + ".xls");
					a.attr("href", link);
					$("body").append(a);
					a[0].click();
					$(a, "body").remove();
				}
			}
		}, 
		error : function(jqXHR, status, errorThrown){
			showMessage(status, "Something went wrong .... ");
			loadingMask2.hide();
		}
	});
}



/**
 * Submit Report form
 * @param customurl
 * @returns
 */
function submitReportForm(customurl){
	if($('form#reportform').length < 1) return;

	var targettedForm = $('form#reportform');
	if(!targettedForm.smkValidate()) return;

	var submitUrl = (customurl != undefined) ? customurl : targettedForm.attr('action');
	var submitType = targettedForm.attr('method');
	var formData = $(targettedForm).serializeArray();
	var reportType = $('#reportType').val();
	if(reportType == undefined || reportType == '') reportType = "PDF";
	var reportName = $('#reportName').val() != '' ? $('#reportName').val() : 'report';

	loadingMask2.show();
	$.ajax({
		url : submitUrl,
		type : submitType,
		data : formData,
		success : function(data) {
			loadingMask2.hide();
			var arrrayBuffer = base64ToArrayBuffer(data);
			if("PDF" == reportType){
				var blob = new Blob([arrrayBuffer], {type: "application/pdf"});
				var link = window.URL.createObjectURL(blob);
				window.open(link,'', 'height=650,width=840');
			} else {
				var blob = new Blob([arrrayBuffer], {type: "application/octetstream"});
				var isIE = false || !!document.documentMode;
				if (isIE) {
					window.navigator.msSaveBlob(blob, reportName + ".xls");
				} else {
					var url = window.URL || window.webkitURL;
					link = url.createObjectURL(blob);
					var a = $("<a />");
					a.attr("download", reportName + ".xls");
					a.attr("href", link);
					$("body").append(a);
					a[0].click();
					$(a, "body").remove();
				}
			}
		}, 
		error : function(jqXHR, status, errorThrown){
			loadingMask2.hide();
			showMessage(status, "Something went wrong .... ");
		}
	});
}

/**
 * Convert Base64 string to array buffer
 * @param base64
 * @returns
 */
function base64ToArrayBuffer(base64) {
	var binaryString = window.atob(base64);
	var binaryLen = binaryString.length;
	var bytes = new Uint8Array(binaryLen);
	for (var i = 0; i < binaryLen; i++) {
		var ascii = binaryString.charCodeAt(i);
		bytes[i] = ascii;
	}
	return bytes;
}
