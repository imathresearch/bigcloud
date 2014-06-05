var STATE = {CREATED:"CREATED", RUNNING:"RUNNING", PAUSED:"PAUSED", CANCELLED:"CANCELLED", FINISHED_OK:"FINISHED_OK", FINISHED_ERROR:"FINISHED_ERROR"};

function submitService(form_id){
	console.log(form_id);
	var id_split = form_id.split('_');
	switch(id_split[0]) {
		case "SAForm":
			submitService_SentimentAnalysis(form_id);
			break;
		default:
			console.log("Unknown service");
	};
	
	//return false;	
};

function submitService_SentimentAnalysis(form_id){
	
	var terms = $('#'+form_id).find('textarea[name="query_terms"]').val();
	var time = $('#'+form_id).find('input[name="tracking_time"]').val();
	
	console.log(time);
	
	var clean_terms = terms.replace(/^\s+|\s+$/g, "").replace(/\s*,\s*/g, ",");
	console.log(clean_terms);
	
	var id_split = form_id.split('_');
	var SA_param = {
            service: id_split[0],
            instance : id_split[1],
            query_terms: clean_terms,
            track_time: time
    };
	
	$('#execution-status_' + SA_param.instance).hide();	
	$("#radial-words-mood_" + SA_param.instance).hide();
	$('#execution-status-print_' + SA_param.instance).hide();
	
	$.ajax({
        url: "rest/service_service/submitService",
        cache: false,
        dataType: "json",
		contentType: "application/json; charset=utf-8",
		data : JSON.stringify(SA_param),
        type: "POST",
        success: function(service_state) {
        	console.log("success of submitservice");
        	refreshJobsTable();
        	processServiceState(SA_param, service_state);
        },
        error: function(error) {
            console.log("Possible error submitting Service -" + error.status);
        },
    });	
}

function processServiceState(params, service_state){

	var idExecution = service_state.idExecution;
	if (service_state.state == STATE.RUNNING){
		setTimeout(function (){
			getExecutionState(params, idExecution);
		}, 10000);
	}
	
	switch (params.service){
		case "SAForm":
			updateSAServiceUI(params, service_state);
			break;
		default:
			console.log("Unknown service key " + params.service);
			break;
	}
		
}

function getExecutionState(params, idExecution){
	
	$.ajax({
        url: "rest/service_service/executionState/" + idExecution,
        cache: false,
        dataType: "json",
		contentType: "application/json; charset=utf-8",
        type: "GET",
        success: function(service_state) {
        	processServiceState(params, service_state);
        },
        error: function(error) {
            console.log("Possible error discovering the state of the execution -" + idExecution+ " " +  error.status);
        },
    });	
		
}

function updateSAServiceUI(params, service_state){

	switch (service_state.state){
		case STATE.RUNNING:
			console.log("The service is running");
			getServiceParcialData(params, service_state.idExecution);
			$('#execution-status_' + params.instance).show();
			$('#execution-status_' + params.instance).html('Service Running!!<br><span class="glyphicon glyphicon-time bigglyph"></span>');			
			break;
		case STATE.FINISHED_OK:
			console.log("The service finished ok");
			$('#execution-status-print_' + params.instance).show();
			$('#execution-status-print_' + params.instance).html('Service Finished!!');
			refreshJobsTable();
			getServiceParcialData(params, service_state.idExecution);
			break;
		case STATE.FINISHED_ERROR:
			console.log("The service finished with errors");
			break;
		default:
			console.log("Unknown state " + service_state.state);
			break;
	}

}

function getServiceData(params, idExecution){
	
	$.ajax({
        url: "rest/service_service/getServiceData/" + idExecution,
        cache: false,
        dataType: "json",
		contentType: "application/json; charset=utf-8",
        type: "GET",
        success: function(service_state) {
        	console.log("on success getServiceData");
        	plotDataService(params, service_state);
        },
        error: function(error) {
            console.log("Possible error getting data of the execution - " + idExecution+ " " +  error.status);
        },
    });	
		
}

function getServiceParcialData(params, idExecution){
	
	$.ajax({
        url: "rest/service_service/getServiceParcialData/" + idExecution,
        cache: false,
        dataType: "json",
		contentType: "application/json; charset=utf-8",
        type: "GET",
        success: function(service_state) {
        	console.log("on success getServiceData");
        	plotDataService(params, service_state);
        },
        error: function(error) {
            console.log("Possible error getting data of the execution - " + idExecution+ " " +  error.status);
        },
    });	
		
}

function plotDataService(params, service_state){
	
	switch (params.service){
		case "SAForm":
			plot_SA(params, service_state);
			break;
		default:
			console.log("Unknown service key " + params.service);
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
	
	$('#execution-status_' + params.instance).hide();
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




