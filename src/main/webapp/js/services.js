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
	key_service = id_split[0];
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
        	console.log("Success");
        },
        error: function(error) {
            console.log("Possible error submitting Service -" + error.status);
        },
    });	
}

function onSucess(key_service, service_state){
	
	if (key_service == "SAForm") {
		var id = setInterval(function () {
			
			state = getExecutionState(idExecution);
			if(state == FINISHED_OK)
				//get Job Result
				//plot
				clearInterval(id);
			if(state == FINISHED_ERROR)
				//say something
				clearInterval(id);
 			

		}, 100);
			
	};
	
	
	
}

function getExecutionState(idExecution){
	
	$.ajax({
        url: "rest/service_service/executionState/" + idExecution,
        cache: false,
        dataType: "json",
		contentType: "application/json; charset=utf-8",
        type: "GET",
        success: function(state) {
        	console.log("Success");
        },
        error: function(error) {
            console.log("Possible error discovering the state of the execution -" + idExecution+ " " +  error.status);
        },
    });	
		
}