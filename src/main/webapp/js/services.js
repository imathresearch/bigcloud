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

function processServiceState(params, service_state){

	var idExecution = service_state.idExecution;
	
	//Polling for the state of the CURRENT execution associated to the service
	if (service_state.state == STATE.RUNNING){
		setTimeout(function (){
			getExecutionState(params, idExecution);
		}, 10000);
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




