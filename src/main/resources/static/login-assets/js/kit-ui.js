/* ------------------------------------------------------------------------------
 *
 *  # Custom JS code
 *
 *  Place here all your custom js. Make sure it's loaded after app.js
 *
 * ---------------------------------------------------------------------------- */
var kit = kit || {};
kit.ui = kit.ui || {};
kit.ui.config = kit.ui.config || {};


kit.ui.config.select2 = function(){
	$('.select').select2();
}

kit.ui.config.noty = function(){
	// Override Noty defaults
	Noty.overrideDefaults({
		theme: 'limitless',
		layout: 'topRight',
		type: 'alert',
		timeout: 2500
	});
}

kit.ui.menu = function(){
	$('a.side-nav-link').removeClass('active');
	$('a#' + Cookies.get('current-menu')).addClass('active');

	$('a.side-nav-link').on('click', function(){
		Cookies.set('current-menu', $(this).attr('id'));
	})
}

kit.ui.config.datepicker = function(){
	$.each($('.datepicker-date-format'), function(ind, elem){
		var maxDate = undefined;
		var minDate = undefined;

		if($(elem).attr('maxDate')){
			maxDate = $(elem).attr('maxDate')
		}

		if($(elem).attr('minDate')){
			minDate = $(elem).attr('minDate')
		}

		new Datepicker(elem, {
			container: '.content-inner',
			buttonClass: 'btn btn-sm',
			prevArrow: document.dir == 'rtl' ? '&rarr;' : '&larr;',
			nextArrow: document.dir == 'rtl' ? '&larr;' : '&rarr;',
			format: 'yyyy-mm-dd',
			maxDate: maxDate,
			minDate: minDate,
			autoHide: true,
		});

	});
}

kit.ui.config.dateAndTimepicker = function(){
	/*$.each($('.datepicker'), function(ind, elem){
		$(elem).datetimepicker({
			useCurrent: false,
			format: "ddd, DD-MMM-YYYY",  // L, LL
			showTodayButton: true,
			icons: {
				next: "fa fa-chevron-right",
				previous: "fa fa-chevron-left",
				today: 'todayText',
			}
		});
	});*/

	$.each($('.timepicker'), function(ind, elem){
		$(elem).datetimepicker({
			format: "HH:mm",   // LT, LTS
			icons: {
				up: "ph-caret-up",
				down: "ph-caret-down"
			}
		});
	});

	$.each($('.datetimepicker'), function(ind, elem){
		$(elem).datetimepicker({
			useCurrent: false,
			format: "ddd, DD-MMM-YYYY HH:mm:ss",  // L, LL
			showTodayButton: true,
			icons: {
				next: "fa fa-chevron-right",
				previous: "fa fa-chevron-left",
				today: 'todayText',
				up: "fa fa-chevron-up",
				down: "fa fa-chevron-down"
			}
		});
	});
}

kit.ui.config.searchSuggest = kit.ui.config.searchSuggest || {};
kit.ui.config.searchSuggest = {
	debounce : null,
	inset : '',
	customKeyPress : function () {
		$(".searchsuggest").off('keyup').on('keyup', function(e){
			if(e.which == 38 || e.which == 40 || e.which == 13){
				var swithc = "UP";
				if(e.which == 40) swithc = "DOWN";
				if(e.which == 13) swithc = "ENTR";
				kit.ui.config.searchSuggest.adjustSelectedItem(swithc);
				return;
			}
		});
	},
	selectItemAndSetValue : function(selectedItem, uielement, tableContainer){
		var itemval = $(selectedItem).find('td[data-forvalue="true"]').html();
		var itemprompt = "";
		$.each($(selectedItem).find('td[data-forprompt="true"]'), function(i, item){
			if(i == 0) {
				itemprompt += $(item).html();
			} else {
				itemprompt += ' - ' + $(item).html();
			}
		})

		var parentDiv = $(tableContainer).parent();
		$(parentDiv).find('.searchsuggest').val(itemprompt);
		$(parentDiv).find('#search-val').val(itemval);
		$(parentDiv).find('#search-des').val(itemprompt);
		$(tableContainer).remove();
		$(parentDiv).find('.searchsuggest').blur();
	},
	adjustSelectedItem : function(swithc){
		if(swithc == 'ENTR'){
			kit.ui.config.searchSuggest.selectItemAndSetValue($('tr.search-item.selected-search-item'), $('.searchsuggest'), $('.search-suggest-table-container'));
			return;
		}

		var selectedRowIndex = $('tr.search-item.selected-search-item').data('rowindex')

		$('tr.search-item').removeClass('selected-search-item');

		if(swithc == 'UP' && selectedRowIndex != 1){
			selectedRowIndex--;
		} else if(swithc == 'DOWN' && selectedRowIndex != $('tr.search-item').length) {
			selectedRowIndex++;
		}

		$('tr.search-item[data-rowindex="'+ selectedRowIndex +'"]').addClass('selected-search-item');

		var rowpos = $('tr.search-item[data-rowindex="'+ selectedRowIndex +'"]').position();
		var rowHeight = $('tr.search-item[data-rowindex="'+ selectedRowIndex +'"]').height();
		var tableHHeight = $('.search-suggest-table-container').height();

		if((rowpos.top - 3) > tableHHeight){
			if(swithc == 'DOWN'){
				$('.search-suggest-table-container').animate({
					scrollTop: $('.search-suggest-table-container').scrollTop() + Number(rowHeight + 2)
				}, 0);
			}
		}
		if (swithc == 'UP'){
			$('.search-suggest-table-container').animate({
				scrollTop: selectedRowIndex == 1 ?  $('.search-suggest-table-container').scrollTop() - 99999 : $('.search-suggest-table-container').scrollTop() - Number(rowHeight - 2)
			}, 0);
		}
	},
	tableMoodRowGenerator : function(uielement, data){
		var parent = $(uielement).parent();

		// Remove previous search suggest div if exist
		if($(parent).find('.search-suggest-table-container').length > 0){
			$(parent).find('.search-suggest-table-container').remove();
		}

		// create new search suggest resul div
		var headerColumn = 	'<tr>';
		$.each(data.columns, function(i, col){
			headerColumn += '<th style="text-align: left; white-space: nowrap;">'+ col +'</th>';
		})
			headerColumn += '</tr>';

		var bodyData = '';
		$.each(data.data, function(i, row){
			var selectedClass = "";
			if(i == 0) selectedClass = "selected-search-item";

			bodyData += '<tr class="search-item '+ selectedClass +'" data-rowindex="'+ (i+1) +'">';

			$.each(row, function(j, coldata){
				var forvalue = false;
				var forprompt = false;
				if(coldata==null) coldata=' '
				if(j == data.valueindex) forvalue = true;
				if(data.promptindex.includes(j)) forprompt = true;
				bodyData += '<td class="rw" style="text-align: left; white-space: nowrap;" data-forvalue="'+ forvalue +'" data-forprompt="'+ forprompt +'">'+ coldata +'</td>';

			})

			bodyData += '</tr>';
		})
		if(data.data.length == 0){
			bodyData += '<tr><td style="text-align: center;" colspan="'+ data.columns.length +'">No result found</td></tr>';
		}

		var tableContainer = 	'<div class="search-suggest-table-container" id="tabinset">'+
									'<table class="table table-striped table-bordered search-suggest-table">'+
										'<thead>' +
											headerColumn +
										'</thead>' +
										'<tbody>' +
											bodyData +
										'<tbody>' +
										'<tfoot>' +
										'<th class="resultcount" style="text-align: left;" colspan="'+ data.columns.length +'"></th>'	+			
										'</tfoot>' +
									'</table>'+
								'</div>';
		$(parent).append(tableContainer);
		
		$('.resultcount').html('Showing top '+$('.search-item').length+' records');

		$('tr.search-item').off('click').on('click', function(e){
			kit.ui.config.searchSuggest.selectItemAndSetValue($(this), uielement, $('.search-suggest-table-container'));
		})
	},
	generateSearchResult : function(uielement, data){
		if(data.tableMood == true){
			kit.ui.config.searchSuggest.tableMoodRowGenerator(uielement, data);
			$( "#tabinset" ).addClass( kit.ui.config.searchSuggest.inset );
			return;
		}

		var parent = $(uielement).parent();

		// Remove previous search suggest div if exist
		if($(parent).find('.search-suggest-results').length > 0){
			$(parent).find('.search-suggest-results').remove();
		}

		// create new search suggest resul div
		$(parent).append('<div class="search-suggest-results col-sm-6"></div>');

		var searchContainer = $(parent).find('.search-suggest-results');
		$(searchContainer).css({
			'display':'block',
			'min-width' : $(uielement).width() + 100 + 'px',
			'top' : $(uielement).height("px")
		});

		// clreate list items
		$(searchContainer).html('<ul class="search-container-ul"></ul>');
		var totalItem = 0;
		$.each(data, function(index, item){
			totalItem++;
			var listitem = '<li class="search-item" value="'+ item.value +'" prompt="'+ item.prompt +'">'+ item.prompt +'</li>';
			$(searchContainer).find('.search-container-ul').append(listitem);
		})
		if(totalItem == 0){
			var noresultitem = '<li class="search-item" value="" prompt="">No result found</li>';
			$(searchContainer).find('.search-container-ul').append(noresultitem);
		}

		$(searchContainer).find('.search-item').off('click').on('click', function(){
			var itemprompt = $(this).attr('prompt');
			var itemval = $(this).attr('value');
			$(uielement).val(itemprompt);
			$(parent).find('#search-val').val(itemval);
			$(parent).find('#search-des').val(itemprompt);
			$(searchContainer).remove();
		})

	},
	init : function(){
		$('form').attr('autocomplete', 'off');

		$(".searchsuggest").off('input').on('input', function(){
			var hint = $(this).val();
			var targetElement = $(this);
			var serachUrl = $(this).attr('search-url');
			kit.ui.config.searchSuggest.inset = $(this).attr('inset');
			if(kit.ui.config.searchSuggest.inset == undefined) kit.ui.config.searchSuggest.inset = 'leftinset'
			if(hint == '') return;

			if(kit.ui.config.searchSuggest.debounce != null) clearTimeout(kit.ui.config.searchSuggest.debounce);
			kit.ui.config.searchSuggest.debounce = setTimeout(function(){
				$.ajax({
					url : getBasepath() + '/' + serachUrl + '/' + hint,
					type : 'GET',
					success : function(data) {
						kit.ui.config.searchSuggest.generateSearchResult(targetElement, data);
					},
					error : function(jqXHR, status, errorThrown){
						
					}
				});
			}, 200);

			kit.ui.config.searchSuggest.customKeyPress();
		});

		$(".searchsuggest").off('blur').on('blur', function(){
			var targetElement = $(this);
			var parent = $(targetElement).parent();

			// Remove previous search suggest div if exist
			setTimeout(() => {
				// list mood
				if($(parent).find('.search-suggest-results').length > 0){
					$(parent).find('.search-suggest-results').remove();
				}
				// table mood
				if($(parent).find('.search-suggest-table-container').length > 0){
					$(parent).find('.search-suggest-table-container').remove();
				}
				// clear data
				if($(targetElement).val() == ''){
					$(parent).find('#search-val').val("");
					$(parent).find('#search-des').val("");
				} else {
					$(targetElement).val($(parent).find('#search-des').val());
				}
			}, 300);

		})
	}
}

kit.ui.config.onscreenPrintBtn = function(){
	$('.btn-print').off('click').on('click', function(e){
		e.preventDefault();

		var params = {};
		params.reportCode = $(this).data('rptcode');
		params.reportType = "PDF";
		params.param1 = $("#businessZid").val();

		var i = 2;
		while(i <= 30){
			params['param' + i] = $(this).data('param' + i);
			i++;
		}

		generateOnScreenReport($(this).data('url'), params);
	});
}

kit.ui.config.advancedSearchBtInit = function(){
	$('input.searchsuggest2').off('keypress').on("keypress", function(e) {
		var keycode = (e.keyCode ? e.keyCode : e.which);
		if(keycode == '13'){
			e.preventDefault();
			$(this).siblings('.btn-search').trigger('click');
		}
	});

	$('.btn-search').off('click').on('click', function(e){
		e.preventDefault();
		$('#searchSuggestTableModal').modal('show');
		$('.search-suggest-results-container').html("");

		var searchValue = $(this).siblings('input.searchsuggest2').val();
		sectionReloadAjaxPostReq({
			id : $(this).data('reloadid'),
			url : $(this).data('reloadurl') + searchValue
		}, {
			"fieldId" : $(this).data('fieldid'),
			"mainscreen" : $(this).data('mainscreen'),
			"mainreloadurl" : $(this).data('mainreloadurl'),
			"mainreloadid" : $(this).data('mainreloadid'),
			"extrafieldcontroller" : $(this).data('extrafieldcontroller'),
			"detailreloadurl" : $(this).data('detailreloadurl'),
			"detailreloadid" : $(this).data('detailreloadid'),
			"additionalreloadurl" : $(this).data('additionalreloadurl'),
			"additionalreloadid" : $(this).data('additionalreloadid'),
		});
	});

	$('.btn-search-clear').off('click').on('click', function(e){
		e.preventDefault();
		$(this).siblings('input.searchsuggest2').val("");
		$(this).siblings('input.search-val').val("");
	});
}


kit.ui.config.inputFieldsEvents = function(){
	//declare class name 'numeric-only'
	$('input.numeric-only').on('keydown', function(e){-1!==$.inArray(e.keyCode,[46,8,9,27,13,110,190])||(/65|67|86|88/.test(e.keyCode)&&(e.ctrlKey===true||e.metaKey===true))&&(!0===e.ctrlKey||!0===e.metaKey)||35<=e.keyCode&&40>=e.keyCode||(e.shiftKey||48>e.keyCode||57<e.keyCode)&&(96>e.keyCode||105<e.keyCode)&&e.preventDefault()});

	$('input.numeric-only').on('blur', function(){
		var min = Number($(this).attr('min'));
		var max = Number($(this).attr('max'));
		var currentVal = Number($(this).val());
		if(currentVal < min) $(this).val(min);
		if(currentVal > max) $(this).val(max);
	});
}

kit.ui.init = function(){
	kit.ui.config.select2();
	kit.ui.config.noty();
	kit.ui.menu();
	kit.ui.config.datepicker();
	kit.ui.config.dateAndTimepicker();
	kit.ui.config.searchSuggest.init();
	kit.ui.config.onscreenPrintBtn();
	kit.ui.config.advancedSearchBtInit();
	kit.ui.config.inputFieldsEvents();
}
 
$(document).ready(function(){
	kit.ui.init();
})