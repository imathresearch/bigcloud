function getExecutionState(params, idExecution){
	
	$.ajax({
        url: "rest/execution_service/executionState/" + idExecution,
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

function getExecutionData(params, idExecution){
	
	$.ajax({
        url: "rest/execution_service/getExecutionData/" + idExecution,
        cache: false,
        dataType: "json",
		contentType: "application/json; charset=utf-8",
        type: "GET",
        success: function(service_state) {
        	plotDataService(params, service_state);
        },
        error: function(error) {
            console.log("Possible error getting data of the execution - " + idExecution+ " " +  error.status);
        },
    });	
		
}

function getExecutionParcialData(params, idExecution){
	
	$.ajax({
        url: "rest/execution_service/getExecutionParcialData/" + idExecution,
        cache: false,
        dataType: "json",
		contentType: "application/json; charset=utf-8",
        type: "GET",
        success: function(service_state) {
        	plotDataService(params, service_state);
        },
        error: function(error) {
            console.log("Possible error getting data of the execution - " + idExecution+ " " +  error.status);
        },
    });	
		
}