
$(document).ready( function() {
	requestSession();
	/*
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
	});*/
	
	
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
        	getUserInstances(userName)
        	//getLastUserServiceExecutions(userName);
        	//refreshJobsTable();
        	//refreshJobsTable();
    		
        },
        error: function(error) {
            console.log("error updating table -" );
        }
    });
}


function getUserInstances(userName){
	
	$.ajax({
        url: "rest/session_service_BC/getInstances/"+userName,
        cache: false,
        dataType: "json",
        type: "GET",
        success: function(instances) {
        	addInstancesCode(instances);
        	addJobTableCode();
        	addFAQCode();
        	getLastUserServiceExecutions(userName);
        	refreshJobsTable();
        },
        error: function(error) {
            console.log("error updating table -" );
        }
    });
	
	
}

function addInstancesCode(instances){
	var i;
	for (i = 0; i < instances.length; i++){
		switch (instances[i].serviceName){
			case "Twitter Sentiment Analysis":
				add_SAInstanceCode(instances[i].idInstance);				
				break;
			default:
				console.log("Unknown service key " + params.service);
				break;
		}
	}
}

function addJobTableCode(){
	var code = JobTable_code;
	$("#example").append($(code));
	var code_menu = '<li><a href="#resources">Resources</a></li>';
	$("#menu").append($(code_menu));
	$("#menu").kendoMenu();
	$("#exec-table").kendoGrid({
	    height: 430,
	    scrollable: true,
	    sortable: true,
	    filterable: true,
	});
	
    
}

function addFAQCode(){
	var code = FAQ_code;
	$("#example").append($(code));
	var code_menu = '<li><a href="#faq">FAQ</a></li>';
	$("#menu").append($(code_menu));
	$("#menu").kendoMenu();
	$("#panelbar").kendoPanelBar();
	
}


function getLastUserServiceExecutions(userName){
	
	//Initialization of service_instances just in the case that 
	// there is no execution of any service
	//Now, we only have SA_Form services
	//TODO: do better, maybe with a rest call
	var sa_elements = $("[id^='SAForm_']");
	for (var i = 0; i < sa_elements.length; i++) {		
		var nameID = $(sa_elements[i]).attr("id");
		var IDinstance = nameID.split('_')[1];
		service_instances[IDinstance] = {execution:-1};
		$("#stop_" + IDinstance).attr("disabled", "disabled");
	}
	
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
				if(execution_state.state == STATE.RUNNING){
					$("#submit_" + executions[i].idInstance).attr("disabled", "disabled");
					$("#stop_" + executions[i].idInstance).removeAttr("disabled", "disabled");
					$("#query_terms_" + executions[i].idInstance).attr("disabled", "disabled");
					$("#datetimepicker_" + executions[i].idInstance).attr("disabled", "disabled");
					$("#update_freq_" + executions[i].idInstance).attr("disabled", "disabled");
				}
				service_instances[execution_param.instance] = {execution:execution_state.idExecution};
				processServiceState(execution_param, execution_state);
				break;
			default:
				console.log("Unknown service key " + params.service);
				break;
		}
	}
	
	
}

var SA_template = 	'<div class="row clearfix"><div class="col-lg-4"><section id="service3" class="well"><h2 class="ra-well-title">Tweeter Sentiment Analysis Service</h2><div class="row"><div class="col-lg-5 col-sm-2"><img src="bootstrap-integration/images/service_tweeter.png" class="ra-avatar img-responsive" /></div><div class="col-lg-7 col-sm-2" style="height:120px"><span class="ra-first-name">Big Data Service</span><span class="ra-last-name">Sentiment Analysis</span><div class="ra-position">Demo service for beta release </div></div></div><div class="row" style="height:260px"><form id="SAForm_xxInstIdxx" onsubmit="submitService(\'SAForm_xxInstIdxx\'); return false" accept-charset=utf-8><br><div class="form-group"><label>Query Terms</label><input type="text" class="form-control" id="query_terms_xxInstIdxx" placeholder="Terms separated by commas"></div><div class="form-group"><label>Tracking End Date</label><input type="text" class="form-control" id="datetimepicker_xxInstIdxx" placeholder="Click to open the calendar"></div><div class="form-group"><label>Data Update Frequency</label><input type="text" class="form-control" id="update_freq_xxInstIdxx" placeholder="Seconds"></div><br><input type="submit" id="submit_xxInstIdxx" value="Submit" class="btn btn-primary"><button type="button" id="stop_xxInstIdxx" onclick="stopService(\'SAForm_xxInstIdxx\');" class="btn btn-default btn-stop" >Stop Service</button></form ></div></section></div><div class="col-lg-8"><div id="tabstripxxInstIdxx" class="ra-section"><ul><li class="k-state-active"><span class="km-icon"></span><span class="hidden-xs">Mood Analysis</span></li></ul><div style="height:430px"><div id="radial-words-mood_xxInstIdxx"></div><label class="execution-status" id="execution-status_xxInstIdxx"></label></div></div></div></div>'

var JobTable_code = '<div class="row clearfix"><div class="col-lg-4"><section id="resources" class="well"><h2 class="ra-well-title">Executed Resources</h2><div class="row"><div class="col-lg-5 col-sm-2"><img src="bootstrap-integration/images/process_running.jpg" class="ra-avatar img-responsive" /></div><div class="col-lg-7 col-sm-2" style="height:350px"><span class="ra-first-name">Resources</span><span class="ra-last-name">Background Processes</span><div class="ra-position">Jobs running in iMathCloud </div></div></div></section></div><div class="col-lg-8"><table id="exec-table" class="ra-section" border="0"><colgroup><col style="width:50px"/><col style="width:100px"/><col style="width:400px" /><col style="width:200px" /><col style="width:200px" /></colgroup><thead><tr><th> </th><th>Job#</th><th>Description</th><th>Started</th><th>% Completion</th></tr></thead><tbody id="jobsTBODY"></tbody></table></div></div>'
	
var FAQ_code = '<section id="faq" class="well"><h2 class="ra-well-title"><abbr title="Frequently Asked Questions">FAQ</abbr></h2><ul id="panelbar" class="ra-well-overlay"><li class="k-state-active">What is BigCloud?<div><p>BigCloud is a web platform that runs over iMath Cloud and that compiles a set of big data services.</p><p><a href="http://localhost:8080/iMathCloud">Access to iMath Cloud vi</a> and do it yourself!</p></div></li></ul></section>'