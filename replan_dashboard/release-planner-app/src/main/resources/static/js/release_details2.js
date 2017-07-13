var app = angular.module('w5app');
app.controllerProvider.register('release-details2', ['$scope', '$location', '$http', '$compile', '$rootScope',
                                    function ($scope, $location, $http,  $compile, $rootScope) {
	/*
	 * REST methods
	 */
	var baseURL = "release-planner-app/replan/projects/tenant";

	$scope.getReleaseFeatures = function (releaseId) {

		var url = baseURL + '/releases/' + releaseId + '/features';
		return $http({
			method: 'GET',
			url: url
		});
	}; 

	$scope.getReleasePlan = function (releaseId) {

		var url = baseURL + '/releases/' + releaseId + '/plan';
		return $http({
			method: 'GET',
			url: url
		});
	};

	$scope.getRelease = function (releaseId) {
		return $http({
			method: 'GET',
			url: baseURL + '/releases/' + releaseId
		});
	};

	//remove feature to release
	$scope.removeFeatureFromRelease = function (releaseId, featureIds){

		var url =  baseURL + '/releases/'+ releaseId + '/features';
		for(var i=0 ; i< featureIds.length; i++){
			if(i == 0){
				url = url + "?featureId=" + featureIds[i];
			}
			else{
				url = url + "," + featureIds[i];
			}
		}  

		return $http({
			method: 'DELETE',
			url: url
		});	 

	};

	$scope.removeReleasePlan = function (releaseId){

		var url = baseURL + '/releases/' + releaseId + '/plan';
		return $http({
			method: 'DELETE',
			url: url
		});	 

	};
	$scope.forceReleasePlan = function (releaseId, force) {

		var strForce = force.toString();

		var url = baseURL + '/releases/' + releaseId + '/plan?force_new='+strForce;
		return $http({
			method: 'GET',
			url: url
		});
	};


	/*
	 * All methods
	 */
	$scope.showReleasePlan = false;
	$scope.messageReleasePlan = "Loading ...";
	$scope.plan = {};
	$scope.planJqxgrid = {};
	$scope.release = {};
	$scope.featuresTORemove = [];

	//contains array of feature object.
	$scope.releaseFeatures = [];


	//name -> y
	//1472688000000 -> x
	$scope.startPoint = function (releaseId) {

		$scope.getReleaseFeatures(releaseId)
		.then(
				function(response) {

					//var myFeatures = '[{"id":118,"code":5065,"name":"ahp requirement 1","description":"","effort":"5.0","deadline":"2017-05-01","priority":5,"required_skills":[{"id":21,"name":"Java","description":"Object oriented programming"}],"depends_on":[],"release":{"release_id":16}},{"id":119,"code":5066,"name":"ahp requirement 2","description":"","effort":"10.0","deadline":"2017-05-17","priority":2,"required_skills":[{"id":21,"name":"Java","description":"Object oriented programming"}],"depends_on":[],"release":{"release_id":16}},{"id":120,"code":5067,"name":"ahp requirement 3","description":"","effort":"9.0","deadline":"2017-05-18","priority":2,"required_skills":[{"id":21,"name":"Java","description":"Object oriented programming"}],"depends_on":[{"id":119,"code":5066,"name":"ahp requirement 2","description":"","effort":"10.0","deadline":"2017-05-17","priority":2,"release":{"release_id":16}}],"release":{"release_id":16}},{"id":121,"code":5068,"name":"ahp requirement 4","description":"","effort":"1.0","deadline":"2017-05-25","priority":5,"required_skills":[{"id":21,"name":"Java","description":"Object oriented programming"}],"depends_on":[{"id":118,"code":5065,"name":"ahp requirement 1","description":"","effort":"5.0","deadline":"2017-05-01","priority":5,"release":{"release_id":16}},{"id":119,"code":5066,"name":"ahp requirement 2","description":"","effort":"10.0","deadline":"2017-05-17","priority":2,"release":{"release_id":16}}],"release":{"release_id":16}},{"id":200,"code":5068,"name":"barbara","description":"","effort":"1.0","deadline":"2017-05-25","priority":5,"required_skills":[{"id":21,"name":"Java","description":"Object oriented programming"}],"depends_on":[{"id":118,"code":5065,"name":"ahp requirement 1","description":"","effort":"5.0","deadline":"2017-05-01","priority":5,"release":{"release_id":16}},{"id":119,"code":5066,"name":"ahp requirement 2","description":"","effort":"10.0","deadline":"2017-05-17","priority":2,"release":{"release_id":16}}],"release":{"release_id":16}}]';
					//var obj = JSON.parse(myFeatures);
					//$scope.releaseFeatures = obj;

					$scope.releaseFeatures = response.data;

					$scope.getRelease(releaseId)
					.then(
							function(response) {
								$scope.release = response.data;

								$scope.getReleasePlan(releaseId)
								.then(
										function(response) {

											var responseData = response.data;
											$scope.plan = responseData;
											$scope.showReleasePlan = true;
											//$scope.draw();

											$scope.planJqxgrid = JSON.parse(JSON.stringify(responseData)); 
											addPropertiesTOPlanJqxgrid();
											$scope.initFeaturesJqxgrid();
										
											//google chart
											
											google.charts.setOnLoadCallback(drawTimelineChart);
											google.charts.setOnLoadCallback(drawGanttChart);
											
											//drawTimelineChart();
											//drawGanttChart();

										},
										function(response) {
											$scope.showReleasePlan = false;
											$scope.messageReleasePlan = "Error: "+response.status + " " + response.statusText;
										}
								);
							},

							function(response) {
								$scope.showReleasePlan = false;
								$scope.messageReleasePlan = "Error: "+response.status + " " + response.statusText;
							}
					);
				},
				function(response) {
					$scope.showReleasePlan = false;
					$scope.messageReleasePlan = "Error: "+response.status + " " + response.statusText;
				}
		);
	}

	//2017-05-17T11:00:00.000Z
	function getDate(supersedeDateAsString){

		var res = supersedeDateAsString.split("T");
		var dateAsString = res[0];
		var dateAsStrings = dateAsString.split("-");
		var year = dateAsStrings[0];
		var mounth = dateAsStrings[1];
		var day = dateAsStrings[2];

		var timeStampAsString = res[1];
		var timeStampAsStrings = res[1].split(".");
		var timeAsString = timeStampAsStrings[0];
		var timeAsStrings = timeAsString.split(":");
		var hours = timeAsStrings[0];
		var minutes = timeAsStrings[1];
		var startDate = new Date(parseInt(year),parseInt(mounth),parseInt(day),parseInt(hours),parseInt(minutes));
		return startDate;
	}

	function addPropertiesTOPlanJqxgrid() {

		for(var i = 0; i<$scope.planJqxgrid.jobs.length; ++i){ 
			//add my_dependencies to plan
			var dependencies = '';

			for(var j = 0; j<$scope.planJqxgrid.jobs[i].depends_on.length; ++j){
				if(j==0){
					dependencies = dependencies + $scope.planJqxgrid.jobs[i].depends_on[j].feature_id;
				}
				else{
					dependencies = dependencies +','+ $scope.planJqxgrid.jobs[i].depends_on[j].feature_id;
				}
			}
			var job = $scope.planJqxgrid.jobs[i];
			job.my_dependencies = dependencies;


			//add my_starts
			var res = $scope.planJqxgrid.jobs[i].starts.split("T");
			var time = res[1].split(":");
			job.my_starts=res[0] + " " + time[0] +":" + time[1];

			//add my_ends
			res = $scope.planJqxgrid.jobs[i].ends.split("T");
			time = res[1].split(":");
			job.my_ends=res[0] + " " + time[0] +":" + time[1];

			//add scheduled
			job.my_scheduled = true;

		}
		//add features not scheduled as job 
		for(var i = 0; i<$scope.releaseFeatures.length; ++i){
			//if false -> add
			if(!is_in_scheduled_jobs($scope.releaseFeatures[i].id)){
				var job = {};
				job.id = null;
				job.starts = null;
				job.ends = null;
				job.feature = $scope.releaseFeatures[i];
				job.resource = null;
				job.my_dependencies = '';
				job.my_starts = '';
				job.my_ends = '';
				job.my_scheduled = false;

				$scope.planJqxgrid.jobs.push(job);
			}
		}

	}

	function is_in_scheduled_jobs(id) {
		for(var i = 0; i<$scope.planJqxgrid.jobs.length; ++i){ 
			if($scope.planJqxgrid.jobs[i].feature.id == id){
				return true;
			}
		}
		return false;
	}



	$scope.initFeaturesJqxgrid = function(){

		$scope.blncreateFeaturesJqxgrid = false;

		// prepare the data
		var source =
		{
				datatype: "json",
				datafields: [
				             { name: 'id', map : 'feature>id', type: 'number' },
				             { name: 'name', map : 'feature>name', type: 'string'},
				             { name: 'effort', map : 'feature>effort', type: 'string'},
				             { name: 'priority', map : 'feature>priority', type: 'number'},
				             { name: 'my_starts', type: 'string'},
				             { name: 'my_ends', type: 'string' },
				             { name: 'my_dependencies', type: 'string' }
				             ],
				             id: 'id',
				             localdata: $scope.planJqxgrid.jobs
		};


		var dataAdapter = new $.jqx.dataAdapter(source);

		//tooltip header
		var tooltipHeaderRenderer = function (element) {
			$(element).parent().jqxTooltip({ position: 'mouse', content: $(element).text() });
		}
		//css style cell 
		var cellclass = function (row, columnfield, value) {
			if ($scope.planJqxgrid.jobs[row].my_scheduled) {
				return 'scheduledFeature';
			}
		}

		// create tooltip.
		$("#featuresJqxgrid").jqxTooltip();


		$scope.featuresJqxgridSettings =
		{
				width: '100%',
				autoheight: true,
				source: dataAdapter,
				enabletooltips: true,

				//trigger cell hover.(only for the body table)
				cellhover: function (element, pageX, pageY)
				{
					// update tooltip.
					$("#featuresJqxgrid").jqxTooltip({ content: element.innerHTML });
					// open tooltip.
					$("#featuresJqxgrid").jqxTooltip('open', pageX + 15, pageY + 15);
				},
				enablehover: true,
				columns: [
				          //width: 40 
				          { text: 'Id', datafield: 'id', rendered: tooltipHeaderRenderer, cellclassname: cellclass, width: 40},
				          { text: 'Name', datafield: 'name', rendered: tooltipHeaderRenderer, cellclassname: cellclass},
				          { text: 'Effort', datafield: 'effort', rendered: tooltipHeaderRenderer, cellclassname: cellclass, width: 40},
				          { text: 'Priority', datafield: 'priority', rendered: tooltipHeaderRenderer, cellclassname: cellclass, width: 40 },
				          { text: 'Start', datafield: 'my_starts', rendered: tooltipHeaderRenderer, cellclassname: cellclass},
				          { text: 'End', datafield: 'my_ends', rendered: tooltipHeaderRenderer, cellclassname: cellclass},
				          { text: 'Dependencies', datafield: 'my_dependencies' , rendered: tooltipHeaderRenderer, cellclassname: cellclass, width: 60},
				          { text: '', columntype: 'button', cellclassname: cellclass, width: 60,
				        	  cellsrenderer: function () {
				        		  return "Remove";
				        	  },
				        	  buttonclick: function (row) {

				        		  //if($scope.planJqxgrid.jobs[row].my_scheduled){
				        		  //update grid
				        		  $('#featuresJqxgrid').jqxGrid('deleterow', $scope.planJqxgrid.jobs[row].id);
				        		  //add feature id to remove
				        		  $scope.featuresTORemove.push($scope.planJqxgrid.jobs[row].feature.id);

				        		  //remove from scope
				        		  $scope.planJqxgrid.jobs.splice(row, 1);
				        		  $scope.plan.jobs.splice(row, 1);
				        		  
				        		  for(var i=0; i< $scope.plan.jobs.legth; i++){
				        			
				        			for (var j = $scope.plan.jobs[i].depends_on.length -1; j >= 0 ; j--) {
										if($scope.plan.jobs[i].depends_on[j].id == $scope.planJqxgrid.jobs[row].feature.id){
											elements.splice(j, 1);
										}
									}  
				        			
				        			
				        		  }
				        		  
				        		  
				        		  //redraw the chart
				        		  //$scope.draw();
				        		  //google chart
								  drawTimelineChart();
								  drawGanttChart();
									
				        		  //refresh the table
				        		  $("#featuresJqxgrid").jqxGrid("updatebounddata", "cells");
				        		  //}

				        	  }
				          },
				          { text: '', datafield: 'Edit', columntype: 'button', cellclassname: cellclass, width: 60, 
				        	  cellsrenderer: function () {
				        		  return "Edit";
				        	  },
				        	  buttonclick: function (row) {

				        		  if(row != -1){
				        			  var featureId = $scope.planJqxgrid.jobs[row].feature.id

				        			  $rootScope.$apply(function() {
				        				  $location.path("/release-planner-app/replan_release").search({featureId: featureId, releaseId: ''+$scope.release.id });
				        				  console.log($location.path());
				        			  });
				        		  }
				        	  }
				          },

				          ]
		};

		$scope.blncreateFeaturesJqxgrid = true;

	};

	function is_in_featuresTORemove(id) {
		for(var i = 0; i<$scope.featuresTORemove.length; ++i){ 
			if($scope.featuresTORemove[i]== id){
				return true;
			}
		}
		return false;
	}

	$scope.accept = function() {

		if($scope.featuresTORemove.length > 0){

			$scope.removeFeatureFromRelease($scope.release.id, $scope.featuresTORemove)
			.then(
					function(response) {

						$scope.featuresTORemove = [];
						$location.path("/release-planner-app/main");

					},
					function(response) {
						alert("Error: "+response.status + " " + response.statusText);
					}
			);   
		}
		else{
			$location.path("/release-planner-app/main");   
		}

	};

	$scope.forceTOReplan = function(bln) {

		if(bln){
			$scope.forceReleasePlan($scope.release.id, bln)
			.then(
					function(response) {
						$scope.startPoint($location.search().releaseId);
					},
					function(response) {
						alert("Error: "+response.status + " " + response.statusText);
					}
			); 
		}
		else{

			$scope.removeReleasePlan($scope.release.id)
			.then(
					function(response) {
						$scope.startPoint($scope.release.id);
					},
					function(response) {
						alert("Error: "+response.status + " " + response.statusText);
					}
			);

		}

	};

	/**
	 * Help methods
	 */
	function getDates(startDate, stopDate) {
		var dateArray = new Array();
		var currentDate = startDate;
		while (currentDate <= stopDate) {
			var date = new Date (currentDate);
			date.setHours(0,0,0,0);
			dateArray.push( date );
			currentDate = currentDate.addDays(1);
		}
		return dateArray;
	}

	Date.prototype.addDays = function(days) {
		var dat = new Date(this.valueOf())
		dat.setDate(dat.getDate() + days);
		return dat;
	}

	//http://stackoverflow.com/questions/11246758/how-to-get-unique-values-in-an-array
	//HOW TO 
	//var duplicates = [1,3,4,2,1,2,3,8];
	//var uniques = duplicates.unique(); // result = [1,3,4,2,8]
	Array.prototype.contains = function(v) {
		for(var i = 0; i < this.length; i++) {
			if(this[i] === v) return true;
		}
		return false;
	};

	Array.prototype.unique = function() {
		var arr = [];
		for(var i = 0; i < this.length; i++) {
			if(!arr.contains(this[i])) {
				arr.push(this[i]);
			}
		}
		return arr; 
	}

	/**
	 * Google chart functions
	 */
	
	function drawTimelineChart() {
		
		var container = document.getElementById('timeline_chart');
		var timelineChart = new google.visualization.Timeline(container);
		var dataTable = new google.visualization.DataTable();

		dataTable.addColumn({ type: 'string', id: 'Developer' });
		dataTable.addColumn({ type: 'string', id: 'TaskName' });
		dataTable.addColumn({ type: 'date', id: 'Start' });
		dataTable.addColumn({ type: 'date', id: 'End' });

		var numberOfRows =  dataTable.getNumberOfRows();
		if(numberOfRows > 0){
			dataTable.removeRows(0, numberOfRows)
		}
		
		var jobs = $scope.plan.jobs;
		for(var i = jobs.length-1; i>=0; i--){
			var job = jobs[i];
			var array = [];
			array[0] = job.resource.name;
			array[1] = ""+job.feature.id+"(Id)-"+job.feature.name;
			array[2] = getDate(job.starts);
			array[3] = getDate(job.ends);

			dataTable.addRow(array);
		}
		
		var trackHeight = 20;
		
		var options = {
				height: dataTable.getNumberOfRows() * trackHeight,
				timeline: {
					colorByRowLabel: true
				}
		};

		timelineChart.draw(dataTable, options);
	}
	
	function drawGanttChart() {

		var dataTable = new google.visualization.DataTable();
		dataTable.addColumn('string', 'Task ID');
		dataTable.addColumn('string', 'Task Name');
		dataTable.addColumn('string', 'Resource');
		dataTable.addColumn('date', 'Start Date');
		dataTable.addColumn('date', 'End Date');
		dataTable.addColumn('number', 'Duration');
		dataTable.addColumn('number', 'Percent Complete');
		dataTable.addColumn('string', 'Dependencies');
		var arrays = [];
		var jobs = $scope.plan.jobs;
		for(var i = jobs.length-1; i>=0; i--){
			var job = jobs[i];
			var array = [];
			array[0] = ""+job.id;
			array[1] = ""+job.feature.id+"(Id)-"+job.feature.name;
			array[2] = job.resource.name;
			array[3] = getDate(job.starts);
			array[4] = getDate(job.ends);
			array[5] = null;
			array[6] = 100;
			var depedencies = "";
			for(var j =0; j< job.depends_on.length; j++){
				if(j==0){
					depedencies = ""+job.depends_on[j].id;
				}else {
					depedencies = ","+job.depends_on[j].id;
				}
			}
			array[7] = depedencies;
			
			arrays[i] = array;
			
		}
		dataTable.addRows(arrays);
		var ganttChart = new google.visualization.Gantt(document.getElementById('gantt_chart'));
		
		var trackHeight = 50;
		var options = {
				height: dataTable.getNumberOfRows() * trackHeight
			
		};
		ganttChart.draw(dataTable, options);
	}	

	

	
	/**
	 * start point function
	 */
	google.charts.load('current', {'packages':['timeline', 'gantt']});
	$scope.startPoint($location.search().releaseId);


}]);