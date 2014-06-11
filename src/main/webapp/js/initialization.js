$(document).ready( function() {
	requestSession();
	var l = [];
	for(var xh=0;xh<=23;xh++){
		for(var xm=0;xm<60;xm+=5){
			l.push(("0"+xh).slice(-2)+':'+("0"+xm).slice(-2));
		}
	}
	jQuery('#datetimepicker_1').datetimepicker({
		minDate:'-1970/01/01',
		startDate:'-1970/01/01',
		allowTimes: l
		//closeOnDateSelect:true,
	});
	
	jQuery('#datetimepicker_2').datetimepicker({
		minDate:'-1970/01/01',
		startDate:'-1970/01/01',
		allowTimes: l
		//closeOnDateSelect:true,
	});
	
	
});


/**
 * The function that requests a session for the user and initializes the math console 
 * and the initial load.
 */
function requestSession() {
	
	$.ajax({
        url: "rest/session_service_BC/new_BGSession/"+userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(host) {
        	//console.log("Session confirmed -" );
        	getLastUserServiceExecutions(userName);
        	refreshJobsTable();
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
        	//console.log("on success getServicesExecutions");
        	manageExecutions(executions);
        },
        error: function(error) {
            console.log("Possible error getting executions of services of user  - " + userName + " " +  error.status);
        },
    });
	
}

function manageExecutions(executions){
	
	var i;
	for (i = 0; i < executions.length; i++){
		switch (executions[i].service){
			case "Twitter Sentiment Analysis":
				var p = JSON.parse(executions[i].params);
				var execution_param = {
					service: 'SAForm',
					instance: executions[i].idInstance,
					query_terms: p.query_terms,
					format_track_time: p.track_time,
					update_freq: p.update_freq
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

