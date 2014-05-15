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
        	getJobs(false);
    		
        },
        error: function(error) {
            console.log("error updating table -" );
        }
    });
}