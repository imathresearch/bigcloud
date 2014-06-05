$(document).ready( function() {
	requestSession();
	
});

/**
 * The function that requests a session for the user and initializes the math console 
 * and the initial load.
 */
function requestSession() {
	
	$.ajax({
        url: "http://localhost:8080/bigCloud/rest/session_service_BC/new_BGSession/"+userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(host) {
        	console.log("Session confirmed -" );
        	getLastUserServiceExecutions(userName);
        	refreshJobsTable();
    		
        },
        error: function(error) {
            console.log("error updating table -" );
        }
    });
}

function getLastUserServiceExecutions(userName){
	
	$.ajax({
        url: "rest/session_service_BC/getActiveExecutions/" + userName,
        cache: false,
        dataType: "json",
		contentType: "application/json; charset=utf-8",
        type: "GET",
        success: function(executions) {
        	console.log("on success getServicesExecutions");
        	manageExecutions(executions);
        },
        error: function(error) {
            console.log("Possible error getting executions of services of user  - " + userName + " " +  error.status);
        },
    });
	
}

function manageExecutions(executions){
	
	/*var SA_param = {
            service: id_split[0],
            instance : id_split[1],
            query_terms: clean_terms,
            track_time: time
    };*/
	
	console.log("manage executions");
	console.log(executions);
	var i;
	for (i = 0; i < executions.length; i++){
		switch (executions[i].service){
			case "Twitter Sentiment Analysis":
				var p = JSON.parse(executions[i].params);
				var execution_param = {
					service: 'SAForm',
					instance: executions[i].idInstance,
					query_terms: p.query_terms,
					track_time: p.track_time
				};
				var execution_state = {
					idExecution: executions[i].idExecution,
					state: executions[i].state
				};
				processServiceState(execution_param, execution_state);
				break;
			default:
				console.log("Unknown service key " + params.service);
				break;
		}
	}
	
	
}

