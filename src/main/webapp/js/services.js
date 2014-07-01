var STATE = {CREATED:"CREATED", RUNNING:"RUNNING", PAUSED:"PAUSED", CANCELLED:"CANCELLED", FINISHED_OK:"FINISHED_OK", FINISHED_ERROR:"FINISHED_ERROR"};

function submitService(form_id){
	//console.log(form_id);
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

function stopService(form_id){
	var id_split = form_id.split('_');
	
	var id_service = id_split[0];
	var id_instance = id_split[1];
	
	$.ajax({
        url: "rest/service_service/getLastExecution/" + id_instance,
        cache: false,
        dataType: "json",
		contentType: "application/json; charset=utf-8",
        type: "GET",
        success: function(execution) {
        	if(execution != null && execution.state == STATE.RUNNING){
        		if (confirm("The current instance in execution of this service will be cancelled. Do you want to continue?")) {
        			stopExecution(execution.idExecution);
        		}
        	}
        	else{
        		alert("There is no execution to be stopped");
        	}
        	
        },
        error: function(error) {
            console.log("Possible error getting last execution of service " + id_service + " instance " + id_instance + " of user " + userName + " -" + error.status);
        },
    });
	
}

function processServiceState(params, service_state){

	var idExecution = service_state.idExecution;
	
	//Polling for the state of the CURRENT execution associated to the service
	if (service_state.state == STATE.RUNNING){
		setTimeout(function (){
			getExecutionState(params, idExecution);
		}, params.update_freq*1000);
	}
	
	switch (params.service){
		case "SAForm": //Sentiment Analysis
			update_SAServiceUI(params, service_state);
			break;
		default:
			console.log("Unknown service key " + params.service);
			break;
	}
		
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




