function submitService_SentimentAnalysis(form_id){
	
	var id_split = form_id.split('_');
	
	var terms = $('#query_terms_'+id_split[1]).val();
	var time = $('#track_time_'+id_split[1]).val();
	
	//The query terms cannot have space between them
	var clean_terms = terms.replace(/^\s+|\s+$/g, "").replace(/\s*,\s*/g, ",");
	
	var SA_params = {
            service: id_split[0],
            instance : id_split[1],
            query_terms: clean_terms,
            track_time: time
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
        	console.log("success of submitservice");
        	refreshJobsTable();
        	processServiceState(SA_params, service_state);
        },
        error: function(error) {
            console.log("Possible error submitting Service -" + error.status);
        },
    });	
}



function update_SAServiceUI(params, service_state){
	
	$('#query_terms_'+params.instance).val(params.query_terms);
	$('#track_time_'+params.instance).val(params.track_time);

	switch (service_state.state){
		case STATE.RUNNING:
			console.log("The service is running");
			getExecutionParcialData(params, service_state.idExecution);
			$('#execution-status_' + params.instance).show();
			$('#execution-status_' + params.instance).html('Service Running');			
			break;
		case STATE.FINISHED_OK:
			console.log("The service finished ok");
			$('#execution-status_' + params.instance).html('Service Finished');
			refreshJobsTable();
			getExecutionParcialData(params, service_state.idExecution);
			break;
		case STATE.FINISHED_ERROR:
			console.log("The service finished with errors");
			break;
		default:
			console.log("Unknown state " + service_state.state);
			break;
	}

}

function plot_SA(params, service_state){
	
	console.log("IN plot_SA");
	
	var categories = params.query_terms.split(",");
	
	console.log(service_state.dataResult);
	var x;
	var dataResult = [];
	for (x=0;x<categories.length;x++){
		console.log(categories[x]);
		dataResult.push(service_state.dataResult[categories[x]]*100);
		console.log(service_state.dataResult[categories[x]]*100);
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
}

