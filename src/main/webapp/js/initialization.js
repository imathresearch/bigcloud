$(document).ready( function() {
	console.log("EXECUTING INITIALIZATION")
	requestSession();
});

/**
 * The function that requests a session for the user and initializes the math console 
 * and the initial load.
 */
function requestSession() {
	console.log("BEFORE AJAX CALL");
	$.ajax({
        url: "http://localhost:8080/bigCloud/rest/session_service_BC/new_BGSession/"+userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(host) {
        	console.log("Session confirmed -" );
    		
        },
        error: function(error) {
            console.log("error updating table -" );
        }
    });
}