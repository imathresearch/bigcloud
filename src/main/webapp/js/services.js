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
	console.log(terms);
	console.log(time);
	
	var id_split = form_id.split('_');
	var SA_param = {
            service: id_split[0],
            instance : id_split[1],
            query_terms: terms,
            track_time: time
    };
	
	$.ajax({
        url: "rest/service_service/submitService",
        cache: false,
        dataType: "json",
		contentType: "application/json; charset=utf-8",
		data : JSON.stringify(SA_param),
        type: "POST",
        success: function(service_state) {
        	console.log("success of submitservice");
        	console.log(service_state.state);
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
			break;
		case STATE.FINISHED_OK:
			console.log("The service finished ok");
			//getServiceData(params, idExecution);
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
        success: function(data) {
        	processServiceData(params, data);
        },
        error: function(error) {
            console.log("Possible error discovering the state of the execution - " + idExecution+ " " +  error.status);
        },
    });	
		
}

function processServiceData(params, data){

	switch (params.service){
		case "SAForm":
			//plot the data
			break;
		default:
			console.log("Unknown service " + params.service);
	}
	
}

