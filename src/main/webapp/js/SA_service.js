function submitService_SentimentAnalysis(form_id){
	
	var id_split = form_id.split('_');
	
	var terms = $('#query_terms_'+id_split[1]).val();	
	var dataPicker_val = $('#datetimepicker_'+id_split[1]).val();
	var freq = $('#update_freq_'+id_split[1]).val();
	
	
	var check = check_SAParams(terms, dataPicker_val, freq);
	
	if (check){
	
		var time_ms = Date.parse(dataPicker_val);
		var d = new Date();
		var listen_time = time_ms - d.getTime();
		listen_time = Math.floor(listen_time/1000);
		
		//The query terms cannot have space between them
		var clean_terms = terms.replace(/^\s+|\s+$/g, "").replace(/\s*,\s*/g, ",");
		
		var SA_params = {
	            service: id_split[0],
	            instance : id_split[1],
	            query_terms: clean_terms,
	            track_time: listen_time.toString(),
	            format_track_time: dataPicker_val,
	            update_freq:freq
	    };
		
		$('#execution-status_' + SA_params.instance).hide();	
		$("#radial-words-mood_" + SA_params.instance).hide();
	
		
		$.ajax({
	        url: "rest/service_service/submitService",
	        cache: false,
	        dataType: "json",
			contentType: "application/json; charset=utf-8",
			data : JSON.stringify(SA_params),
	        type: "POST",
	        success: function(service_state) {
	        	$("#submit_" + SA_params.instance).attr("disabled", "disabled");
				$("#stop_" + SA_params.instance).removeAttr("disabled", "disabled");
				$("#query_terms_" + SA_params.instance).attr("disabled", "disabled");
				$("#datetimepicker_" + SA_params.instance).attr("disabled", "disabled");
				$("#update_freq_" + SA_params.instance).attr("disabled", "disabled");
	        	service_instances[SA_params.instance].execution = service_state.idExecution;
	        	refreshJobsTable();
	        	processServiceState(SA_params, service_state);
	        },
	        error: function(error) {
	            console.log("Possible error submitting Service -" + error.status);
	        },
	    });
	}
	else{
		//console.log("Error en los parametros");
		alert("Incorrect parameters!!. Check:\n 1. The query is not empty \n 2. The date is in the future \n 3. The frequency is potive");
		
	}
}


function check_SAParams(terms, date, frequency){

	    if (!terms || !date || !frequency ){	       
	        return false;
	    }
	    else{    
	        //check that date is in the future
	        var time_ms = Date.parse(date);
		    var d = new Date();
		    var listen_time = time_ms - d.getTime();
	        if(listen_time < 0){	           
	            return false;
	        }	    
	        //check that the frequency is positive
	        var f = parseInt(frequency);
	        if(f < 0){
	           return false;
	        }
	        
	        return true;    
	    }
	
}



function update_SAServiceUI(params, service_state){
	
	$('#query_terms_'+params.instance).val(params.query_terms);
	$('#datetimepicker_'+params.instance).val(params.format_track_time);
	$('#update_freq_'+params.instance).val(params.update_freq);

	switch (service_state.state){
		case STATE.RUNNING:
			//console.log("The service is running");
			getExecutionParcialData(params, service_state.idExecution);			
			$('#execution-status_' + params.instance).html('Service Running');			
			break;
		case STATE.FINISHED_OK:
			//console.log("The service finished ok");
			$("#query_terms_" + params.instance).removeAttr("disabled", "disabled");
			$("#datetimepicker_" + params.instance).removeAttr("disabled", "disabled");
			$("#update_freq_" + params.instance).removeAttr("disabled", "disabled");
			$("#submit_" + params.instance).removeAttr("disabled", "disabled");
			$("#stop_" + params.instance).attr("disabled", "disabled");
			$('#execution-status_' + params.instance).html('Service Finished');
			refreshJobsTable();
			getExecutionParcialData(params, service_state.idExecution);
			break;
		case STATE.FINISHED_ERROR:
			$("#query_terms_" + params.instance).removeAttr("disabled", "disabled");
			$("#datetimepicker_" + params.instance).removeAttr("disabled", "disabled");
			$("#update_freq_" + params.instance).removeAttr("disabled", "disabled");
			$("#submit_" + params.instance).removeAttr("disabled", "disabled");
			$("#stop_" + params.instance).attr("disabled", "disabled");
			refreshJobsTable();
			//console.log("The service finished with errors");
			break;
		case STATE.CANCELLED:
			$("#query_terms_" + params.instance).removeAttr("disabled", "disabled");
			$("#datetimepicker_" + params.instance).removeAttr("disabled", "disabled");
			$("#update_freq_" + params.instance).removeAttr("disabled", "disabled");
			$("#submit_" + params.instance).removeAttr("disabled", "disabled");
			$("#stop_" + params.instance).attr("disabled", "disabled");
			$('#execution-status_' + params.instance).html('Service Cancelled');
			refreshJobsTable();
			getExecutionParcialData(params, service_state.idExecution);
			break;
		default:
			//console.log("Unknown state " + service_state.state);
			break;
	}

}

function plot_SA(params, service_state){
	
	var categories = params.query_terms.split(",");
	var x;
	var dataResult = [];
	for (x=0;x<categories.length;x++){
		dataResult.push(service_state.dataResult[categories[x]]*100);		
	}
	
	//$('#execution-status_' + params.instance).hide();
	$("#radial-words-mood_" + params.instance).show();
	$("#radial-words-mood_" + params.instance).kendoChart({
	        title: {
	            text: "Mood Analysis [0: Very negative, 50: Neutral, 100: Very Positive]"
	        },
	        chartArea: {
	            background: "transparent"
	        },
	        legend: {
	            position: "bottom"
	        },
	        seriesDefaults: {
	            type: "radarLine"
	        },
	        series: [{
	            name: "Average Mood Points",
	            data: dataResult
	        }],
	        categoryAxis: {
	            categories: categories
	        },
	        valueAxis: {
	            labels: {
	                format: "{0}"
	            }
	        },
	        tooltip: {
	            visible: true,
	            format: "{0} ptn"
	        }
	    });
	
	$('#execution-status_' + params.instance).show();
}

function add_SAInstanceCode(idInstance){
	
	var code = SA_template;
	code = code.replace(/xxInstIdxx/g, idInstance);
	$("#example").append($(code));
	
	$("#tabstrip" + idInstance).kendoTabStrip({
	    animation: {
	        open: { effects: "fadeIn" }
	    },
	    activate: kendo.resize("#tabstrip" + idInstance)
	});
	
	var l = [];
	for(var xh=0;xh<=23;xh++){
		for(var xm=0;xm<60;xm+=5){
			l.push(("0"+xh).slice(-2)+':'+("0"+xm).slice(-2));
		}
	}
	jQuery('#datetimepicker_'+idInstance).datetimepicker({
		minDate:'-1970/01/01',
		startDate:'-1970/01/01',
		allowTimes: l
		//closeOnDateSelect:true,
	});
	



}

