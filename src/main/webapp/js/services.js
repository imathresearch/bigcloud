function submitService(form_id){
	console.log(form_id);
	switch(form_id) {
		case "SA_form":
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
	
	var SA_param = {
            service: form_id,
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
        success: function(host) {
        	console.log("Success");
        },
        error: function(error) {
        	//refreshJobsTable();
            console.log("Possible error submitting Service -" + error.status);
        },
    });	
}