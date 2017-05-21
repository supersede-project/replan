var app = angular.module('w5app');
app.controllerProvider.register('release-details', ['$scope', '$location', '$http', '$compile',
                                   function ($scope, $location, $http,  $compile) {
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

	$scope.getReleases = function () {
		return $http({
			method: 'GET',
			url: baseURL + '/releases'
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
	$scope.release = {};
	$scope.featuresTORemove = [];

	//contains array of feature object.
	$scope.releaseFeatures = [];

	var CONSTANT = {
			
			"xStart": 90,
			"xEnd": 630,
			"xStartAsString": "90",
			"xEndAsString": "630",

			"yStart": 5,
			"yEnd": 430,
			"yStartAsString": "5",
			"yEndAsString": "430",

			"plusNumberVerticalGrid": 2,
			"plusNumberHorizontallGrid": 1,

			"yOffsetLabel": 30,
			"xOffsetLabel": 5,

			"fontsize": 11,
			"fontfamily": "Helvetica Neue",
	};


	var mappingXDateJSONObject = {};
	mappingXDateJSONObject["Xinterval"] = 0;
	mappingXDateJSONObject["Yinterval"] = 0;
	
	//name -> y
	//1472688000000 -> x
	$scope.startPoint = function (releaseId) {
		
		$scope.getReleaseFeatures(releaseId)
		.then(
				function(response) {
					$scope.releaseFeatures = response.data;
					//alert("features release: " + $scope.releaseFeatures);
					$scope.getReleases()
					.then(
							function(response) {

								for(var i =0; i< response.data.length; i++){
									if(response.data[i].id == parseInt(releaseId)){
										$scope.release = response.data[i];
										//alert("release: " + $scope.release);
										break;
									}
								}

								$scope.getReleasePlan(releaseId)
								.then(
										function(response) {
											
											//var mydata = JSON.parse('{"id":214,"created_at":"2016-10-13T08:30:25.455Z","release_id":1,"jobs":[{"starts":"2016-10-14","ends":"2016-10-16","feature":{"id":1,"code":111,"name":"Fix auto upload","description":"Bla, bla, bla","effort":"2.0","deadline":"2016-11-11","priority":5,"release":{"release_id":1}},"resource":{"id":9,"name":"George","description":"","availability":"2.0","skills":[{"id":3,"name":"JavaScript","description":"JavaScript Programming Language"},{"id":1,"name":"Java","description":"Java Programming Language"}]}},{"starts":"2016-10-17","ends":"2016-10-19","feature":{"id":2,"code":222,"name":"New login","description":"Bla, bla, bla","effort":"5.0","deadline":"2016-10-12","priority":4,"release":{"release_id":1}},"resource":{"id":9,"name":"George","description":"","availability":"2.0","skills":[{"id":3,"name":"JavaScript","description":"JavaScript Programming Language"},{"id":1,"name":"Java","description":"Java Programming Language"}]}},{"starts":"2016-10-20","ends":"2016-10-22","feature":{"id":3,"code":334,"name":"Enrollment refactoring","description":"Bla, bla, bla","effort":"5.0","deadline":"2016-10-13","priority":3,"release":{"release_id":1}},"resource":{"id":2,"name":"Bob","description":"Bob Bonaplata","availability":"55.0","skills":[{"id":2,"name":"Ruby","description":"Ruby Programming Language"},{"id":3,"name":"JavaScript","description":"JavaScript Programming Language"}]}},{"starts":"2016-10-23","ends":"2016-10-25","feature":{"id":4,"code":454,"name":"New channel","description":"Bla, bla, bla","effort":"4.0","deadline":"2016-10-18","priority":2,"release":{"release_id":1}},"resource":{"id":2,"name":"Bob","description":"Bob Bonaplata","availability":"55.0","skills":[{"id":2,"name":"Ruby","description":"Ruby Programming Language"},{"id":3,"name":"JavaScript","description":"JavaScript Programming Language"}]}},{"starts":"2016-10-26","ends":"2016-10-28","feature":{"id":5,"code":556,"name":"Email reply","description":"Bla, bla, bla","effort":"5.0","deadline":"2016-10-20","priority":1,"release":{"release_id":1}},"resource":{"id":3,"name":"Calvin","description":"Calvin California","availability":"90.0","skills":[{"id":1,"name":"Java","description":"Java Programming Language"},{"id":3,"name":"JavaScript","description":"JavaScript Programming Language"}]}}]}');
										
											addDependenciesTOPlan(response.data);
											$scope.plan = response.data;
											
											$scope.showReleasePlan = true;
											
											$scope.draw();
											
											$scope.initFeaturesJqxgrid();
						
											//to add tooltip to buttons use the code below
											//$("#cancelJqxButton").jqxTooltip({ position: 'top', content: 'This is a jqxButton.', theme: 'shinyblack' });
											
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
	
	function addDependenciesTOPlan(plan) {
		
		for(var i = 0; i<plan.jobs.length; ++i){ 
			var dependencies = '';
			
			for(var j = 0; j<plan.jobs[i].depends_on.length; ++j){
				if(j==0){
					dependencies = dependencies + plan.jobs[i].depends_on[j].feature_id;
				}
				else{
					dependencies = dependencies +','+ plan.jobs[i].depends_on[j].feature_id;
				}
			}
			var job = plan.jobs[i];
			job.dependencies = dependencies;
		}
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
			    { name: 'starts', type: 'string'},
			    { name: 'ends', type: 'string' },
			    { name: 'dependencies', type: 'string' }
			    
			    
			],
			id: 'id',
			localdata: $scope.plan.jobs
		};
		
		
		var dataAdapter = new $.jqx.dataAdapter(source);
		 // create tooltip.
        $("#featuresJqxgrid").jqxTooltip();
       
        
		$scope.featuresJqxgridSettings =
		{
			width: '100%',
			autoheight: true,
			source: dataAdapter,
			 enabletooltips: true,
			//editable: true,
			// trigger cell hover.
            cellhover: function (element, pageX, pageY)
            {
                // update tooltip.
                $("#featuresJqxgrid").jqxTooltip({ content: element.innerHTML });
                // open tooltip.
                $("#featuresJqxgrid").jqxTooltip('open', pageX + 15, pageY + 15);
            },
			columns: [
			    { text: 'Id', datafield: 'id', width: 60 },
			    { text: 'Name', datafield: 'name' },
			    { text: 'Start', datafield: 'starts' },
			    { text: 'End', datafield: 'ends' },
			    { text: 'Dependencies', datafield: 'dependencies' },
			    
			    { text: '', datafield: 'Remove', columntype: 'button', width: 67, 
			    	cellsrenderer: function () {
			    		return "Remove";
			    	},
			      	buttonclick: function (row) {
		                
			      		if(row != -1){
			      			
			      			//update grid
			      			$('#featuresJqxgrid').jqxGrid('deleterow', $scope.plan.jobs[row].id);
							//add feature id to remove
			      			$scope.featuresTORemove.push($scope.plan.jobs[row].feature.id);
			      			
			      			//remove from scope
							$scope.plan.jobs.splice(row, 1);
							//redraw the chart
							$scope.draw();
					 	}
		            }	
				},
				{ text: '', datafield: 'Edit', columntype: 'button', width: 67, 
			    	cellsrenderer: function () {
                    return "Edit";
			    	},
			      	buttonclick: function (row) {
		                
			      		if(row != -1){
			      			var featureId = $scope.plan.jobs[row].feature.id
			      			$location.path("/release-planner-app/replan_release").search({featureId: featureId, releaseId: ''+$scope.release.id });
					 	}
		            }	
				},
				
			]
		};
		
		$scope.blncreateFeaturesJqxgrid = true;

	};

	$scope.draw = function(){

		//adapt CONSTANT by chart element
		var chart = document.getElementById("chart");
		var xEnd = chart.parentNode.clientWidth - CONSTANT.xStart;
		CONSTANT["xEnd"] = xEnd;
		CONSTANT["xEndAsString"] = ""+xEnd;
		
		//clear the svg elements
		var ids = ["yGrid", "ydxGrid", "xGrid", "verticalLineGrid", "verticalDeadLineLineGrid", "verticalLabelLineGrid", "horizontalLineGrid", "horizontalLabelLineGrid", "data"];
		for(var i = 0; i< ids.length; i++){

			var element = document.getElementById(ids[i]);
			while (element.firstChild) {
				element.removeChild(element.firstChild);
			}
		}

		drawAxis();

		var deadLineRelease = new Date($scope.release.deadline);
		//calculate min Day  
		var dates = [];
		var resourceNames = [];
		var resourceIds = [];
		var jobs = $scope.plan.jobs;
		dates.push(deadLineRelease)
		for (var i = 0; i < jobs.length; i++) {
			dates.push(new Date(jobs[i].starts));
			dates.push(new Date(jobs[i].ends));
			resourceNames.push(jobs[i].resource.name);
			resourceIds.push(jobs[i].resource.id);
		}
		dates.sort();
		//get IndexDeadLine
		var indexDeadLine = 0;
		for (var i = 0; i < dates.length; i++) {
			if(dates[i].getTime() == deadLineRelease.getTime()){
				indexDeadLine = i;
			}
		}
		//calculate diff Days
		var minDate = new Date(Math.min.apply(null,dates));
		var maxDate = new Date(Math.max.apply(null,dates));

		var timeDiff = Math.abs(maxDate.getTime() - minDate.getTime());
		var diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24)); 
		var numberOfInterval = diffDays + CONSTANT.plusNumberVerticalGrid;
		var xinterval =  (CONSTANT.xEnd - CONSTANT.xStart) / numberOfInterval;
		mappingXDateJSONObject["Xinterval"] = xinterval;

		var timeDiffDeadLine = Math.abs(deadLineRelease.getTime() - minDate.getTime());
		var diffDaysDeadLine = Math.ceil(timeDiffDeadLine / (1000 * 3600 * 24));


		//draw vertical lines
		drawVerticalGridLines(xinterval, numberOfInterval, diffDaysDeadLine);
		//draw vertical days
		var nextMaxDayPlus1 = new Date(maxDate.getTime());
		nextMaxDayPlus1.setDate(maxDate.getDate()+1);

		var dateArray = getDates(minDate, nextMaxDayPlus1); 
		drawLabelVerticalGridLines(xinterval, numberOfInterval, dateArray);

		//draw horizontal lines
		var uniqueResourceNames = resourceNames.unique();

		numberOfInterval = uniqueResourceNames.length + CONSTANT.plusNumberHorizontallGrid;
		var yinterval =  (CONSTANT.yEnd - CONSTANT.yStart) / numberOfInterval;
		mappingXDateJSONObject["Yinterval"] = yinterval;
		drawHorizontalGridLines(yinterval, numberOfInterval);
		drawLabelHorizontalGridLines(yinterval,numberOfInterval,uniqueResourceNames);
		
		drawData(jobs);

	}
	
	
	/**
	 * draw methods
	 */
	function drawAxis() {
		/*
		 * draw y 
		 */
		var element = document.createElementNS('http://www.w3.org/2000/svg', 'line');
		element.setAttribute("x1", CONSTANT.xStartAsString);
		element.setAttribute("y1", CONSTANT.yStartAsString);
		element.setAttribute("x2", CONSTANT.xStartAsString);
		element.setAttribute("y2", CONSTANT.yEndAsString);

		var yGrid = document.getElementById("yGrid");
		yGrid.appendChild(element); 	

		/*
		 * draw x 
		 */
		element = document.createElementNS('http://www.w3.org/2000/svg', 'line');
		element.setAttribute("x1", CONSTANT.xStartAsString);
		element.setAttribute("y1", CONSTANT.yEndAsString);
		element.setAttribute("x2", CONSTANT.xEndAsString);
		element.setAttribute("y2", CONSTANT.yEndAsString);

		var xGrid = document.getElementById("xGrid");
		xGrid.appendChild(element); 	
	
	}

	function drawHorizontalGridLines(interval, numberOfInterval) {

		var y = CONSTANT.yStart;

		for(var i = 0; i< numberOfInterval; i++){
			y = y + interval;
			/*
			 * draw verticalLineGrid
			 */
			var element = document.createElementNS('http://www.w3.org/2000/svg', 'line');
			element.setAttribute("x1", CONSTANT.xStartAsString);
			element.setAttribute("y1", ""+y);
			element.setAttribute("x2", CONSTANT.xEndAsString);
			element.setAttribute("y2", ""+y);

			var horizontalLineGrid = document.getElementById("horizontalLineGrid");
			horizontalLineGrid.appendChild(element); 	
		}
	}

	function drawLabelHorizontalGridLines(interval, numberOfInterval, uniqueResourceNames) {
		var y = CONSTANT.yStart;

		for(var i = 0; i< numberOfInterval-1; i++){

			y = y + interval;

			/*
			 * draw verticalLineGrid
			 */
			var element = document.createElementNS('http://www.w3.org/2000/svg', 'text');
			var x = CONSTANT.xStart - CONSTANT.xOffsetLabel;
			element.setAttribute("x", ""+x);
			element.setAttribute("y", ""+y);

			var result = font_size_to_fit(uniqueResourceNames[i], CONSTANT.fontfamily, CONSTANT.xStart);
			if(result < CONSTANT.fontsize){
				element.setAttribute("style", "font-size: " + result + "px");
			}

			element.textContent  = uniqueResourceNames[i];

			mappingXDateJSONObject[uniqueResourceNames[i]] = y;

			var horizontalLineGrid = document.getElementById("horizontalLabelLineGrid");
			horizontalLineGrid.appendChild(element); 	

		}
	}


	function drawVerticalGridLines(interval, numberOfInterval, indexDeadLine) {

		var x = CONSTANT.xStart;

		for(var i = 0; i< numberOfInterval; i++){

			x = x + interval;

			/*
			 * draw verticalLineGrid
			 */
			var element = document.createElementNS('http://www.w3.org/2000/svg', 'line');
			element.setAttribute("x1", ""+x);
			element.setAttribute("y1", CONSTANT.yStartAsString);
			element.setAttribute("x2", ""+x);
			element.setAttribute("y2", CONSTANT.yEndAsString);

			var verticalLineGrid;

			//draw the deadLine
			if(i == indexDeadLine){
				verticalLineGrid = document.getElementById("verticalDeadLineLineGrid");
			}
			else if(i == numberOfInterval - 1){
				verticalLineGrid = document.getElementById("ydxGrid");
			}
			else{
				verticalLineGrid = document.getElementById("verticalLineGrid");
			}

			verticalLineGrid.appendChild(element); 	
		}
	}

	function drawLabelVerticalGridLines(interval, numberOfInterval, dateArray) {

		var x = CONSTANT.xStart;

		for(var i = 0; i< numberOfInterval-1; i++){

			x = x + interval;

			/*
			 * draw verticalLineGrid
			 */
			var element = document.createElementNS('http://www.w3.org/2000/svg', 'text');
			element.setAttribute("x", ""+x);
			var y = CONSTANT.yEnd + CONSTANT.yOffsetLabel;
			element.setAttribute("y", ""+y);

			var day = dateArray[i].getDate();
			var month =  dateArray[i].getMonth() +1;
			var text = "" + day + "/" + month;
			element.textContent  = text;


			var result = font_size_to_fit("01/01", CONSTANT.fontfamily, interval);
			if(result < CONSTANT.fontsize){
				element.setAttribute("style", "font-size: " + result + "px");
			}else{
				element.setAttribute("style", "font-size: " + CONSTANT.fontsize + "px");
			}

			var date = new Date(""+ dateArray[i]);

			mappingXDateJSONObject[""+date.getTime()] = x;

			var verticalLabelLineGrid = document.getElementById("verticalLabelLineGrid");
			verticalLabelLineGrid.appendChild(element); 	
		}
	}
	//http://stackoverflow.com/questions/18019886/calculate-font-size-required-for-text-to-fill-desired-space
	var font_size_to_fit = (function() {
		var $test = $("<span id='test'/>").appendTo("body").css({
			visibility: "hidden", 
			border: 0, 
			padding: 0, 
			whiteSpace: "pre"
		});
		var minFont = 10, maxFont = 100; 
		return function(txt, fontFamily, size) {
			$test.appendTo("body").css({fontFamily: fontFamily})
			.text(txt);
			$test.css({fontSize: maxFont + "px"});
			var maxWidth = $test.width();
			$test.css({fontSize: minFont + "px"});
			var minWidth = $test.width();
			var width = (size - minWidth) * (maxFont - minFont) /
			(maxWidth - minWidth) + minFont;
			$test.detach();
			return width;
		};
	}());


	// Given a polygon/polyline, create intermediary points along the
	// "straightaways" spaced no closer than `spacing` distance apart.
	// Intermediary points along each section are always evenly spaced.
	// Modifies the polygon/polyline in place.
	function midMarkers(poly,spacing){
		var svg = poly.ownerSVGElement;
		for (var pts=poly.points,i=1;i<pts.numberOfItems;++i){
			var p0=pts.getItem(i-1), p1=pts.getItem(i);
			var dx=p1.x-p0.x, dy=p1.y-p0.y;
			var d = Math.sqrt(dx*dx+dy*dy);
			var numPoints = Math.floor( d/spacing );
			dx /= numPoints;
			dy /= numPoints;
			for (var j=numPoints-1;j>0;--j){
				var pt = svg.createSVGPoint();
				pt.x = p0.x+dx*j;
				pt.y = p0.y+dy*j;
				pts.insertItemBefore(pt,i);
			}
			if (numPoints>0) i += numPoints-1;
		}
	}

	function drawData(jobs) {
		
		var data = document.getElementById("data");

		for(var i = jobs.length-1; i>=0; i--){

			//rectangle
			
			var startDate = new Date(jobs[i].starts);
			var timeSpentPercStartDate = ((startDate.getHours() + 1)/24);
			
			var element = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
			
			var date0000 = new Date(jobs[i].starts);
			date0000.setHours(0,0,0,0);
			
			var newX = mappingXDateJSONObject[""+date0000.getTime()];
			var newX2 = newX + timeSpentPercStartDate * mappingXDateJSONObject.Xinterval;
			
			var newY = mappingXDateJSONObject[""+jobs[i].resource.name]-(mappingXDateJSONObject.Yinterval/2);
			element.setAttribute("id", ""+ jobs[i].feature.id);
			element.setAttribute("x", "" + newX2);
			element.setAttribute("y", "" + newY);
			
			
			
			var endDate = new Date(jobs[i].ends);
			timeSpentPercEndDate = ((endDate.getHours() + 1)/24);
			var timeDiff = Math.abs(endDate.getTime() - startDate.getTime());
			var diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24)); 

			var width = 0;
			var width2 = 0;
		
			width = ((mappingXDateJSONObject.Xinterval * diffDays));
			width2 = width - (timeSpentPercEndDate * mappingXDateJSONObject.Xinterval);
			element.setAttribute("width", "" + width2);
			
			var height = (2 * (mappingXDateJSONObject.Yinterval/2));
			
			element.setAttribute("height", "" + height );
			element.setAttribute("transform", "matrix(1 0 0 1 0 0)");
			element.setAttribute("ng-mousedown", "selectElement($event)");
			element.setAttribute("style", "cursor: move;stroke:rgb(135,206,235)");
			//var name = jobs[i].feature.name;
			mappingXDateJSONObject["x1"+ jobs[i].feature.id] = newX2;
			mappingXDateJSONObject["y1"+ jobs[i].feature.id] = newY +(height/2);
			mappingXDateJSONObject["x2"+ jobs[i].feature.id] = newX2 + width2;
			mappingXDateJSONObject["y2"+ jobs[i].feature.id] = newY+ (height/2);
			//console.log("name: " + jobs[i].feature.name + " id: "+ jobs[i].feature.id + " x1: " + newX2  + " y1: "+ newY + " width: " + width + " height: "+ height + " x2: " + mappingXDateJSONObject["x2"+ jobs[i].feature.id]  + " y2: "+ mappingXDateJSONObject["y2"+ jobs[i].feature.id] );
			
			
			
			data.appendChild(element);
			$compile(element)($scope);

			//text
			var element = document.createElementNS('http://www.w3.org/2000/svg', 'text');
			var newXText = newX2 + (width2/2);
			var newYText = newY + (mappingXDateJSONObject.Yinterval/2);
			element.setAttribute("id", "text_"+jobs[i].feature.id);
			element.setAttribute("x", newXText);
			element.setAttribute("y", newYText);
			element.setAttribute("alignment-baseline", "middle");
			element.setAttribute("text-anchor", "middle");
			element.setAttribute("fill", "black");
			element.setAttribute("transform", "matrix(1 0 0 1 0 0)");
			element.setAttribute("style", "word-break: keep-all;");
			element.setAttribute("ondblclick", "showReplanRelease(event)");
			element.textContent  = jobs[i].feature.id;
			
			data.appendChild(element);
			$compile(element)($scope);
		}

		var dependenciesTag = document.getElementById("dependencies");
		//draw dependency after
		for(var i = jobs.length-1; i>=0; i--){
			
			//dependencies
			//check if the job feature has dependencies
			for (var z = 0; z < $scope.releaseFeatures.length; z++) {
				var releaseFeature = $scope.releaseFeatures[z];
				//if has dependency
				if(jobs[i].feature.id == releaseFeature.id && releaseFeature.depends_on.length > 0){

					for (var a = 0; a < releaseFeature.depends_on.length; a++) {

						//check if dependency exist in jobs
						for(var p = jobs.length-1; p>=0; p--){
							var jobEnd = jobs[p];
							if(jobEnd.feature.id == releaseFeature.depends_on[a].id){
								
								var lineXstart = mappingXDateJSONObject["x1"+ jobs[i].feature.id];
								var lineYstart = mappingXDateJSONObject["y1"+ jobs[i].feature.id];
								var element = document.createElementNS('http://www.w3.org/2000/svg', 'line');
								element.setAttribute("x1", ""+lineXstart);
								element.setAttribute("y1", ""+lineYstart);


								//draw line
								var lineXend = mappingXDateJSONObject["x2"+ releaseFeature.depends_on[a].id];
								var lineYend = mappingXDateJSONObject["y2"+ releaseFeature.depends_on[a].id];

								element.setAttribute("x2", ""+lineXend);
								element.setAttribute("y2", ""+lineYend);
								//element.setAttribute("style", "stroke:rgb(255,0,0);stroke-linecap:round;stroke-dasharray='5,10,5'");
								element.setAttribute("stroke", "rgb(255,0,0)");
								element.setAttribute("stroke-dasharray", "5,5,5");
							
								//console.log("name: "+ name + " x1d: " + lineXstart  + " y1d: "+lineYstart + " x2d: " + lineXend + " y2d: "+ lineYend );
								
								dependenciesTag.appendChild(element);
								$compile(element)($scope);
							}
						}


					}
					break;
				}
			}

		}
		
		
		
	}
	
	

	/**
	 *  add drag and drop in chart
	 */
	//How to drag and drop
	//http://www.petercollingridge.co.uk/interactive-svg-components/draggable-svg-element

	var selectedElement = 0;
	var currentX = 0;
	var currentY = 0;
	var currentMatrix = 0;
	var currentMatrixText = 0;

	$scope.selectElement = function($evt){
		selectedElement = $evt.target;
		currentX = $evt.clientX;
		currentY = $evt.clientY;

		currentMatrix = selectedElement.getAttributeNS(null, "transform").slice(7,-1).split(' ');

		for(var i=0; i<currentMatrix.length; i++) {
			currentMatrix[i] = parseFloat(currentMatrix[i]);
		}

		selectedElement.setAttributeNS(null, "ng-mousemove", "moveElement($event)");
		selectedElement.setAttributeNS(null, "ng-mouseup", "deselectElement($event)");
		$compile(selectedElement)($scope);
	}

	showReplanRelease = function(evt){

		var selectedElement = evt.target;
		var idStr=selectedElement.id;
		var featureId = idStr.replace("text_", "").trim();
		$location.path("/release-planner-app/replan_release").search({featureId: featureId, releaseId: ''+$scope.release.id });
	}

	$scope.moveElement = function($evt){

		dx = $evt.clientX - currentX;
		dy = $evt.clientY - currentY;
		currentMatrix[4] += dx;
		currentMatrix[5] += dy;

		newMatrix = "matrix(" + currentMatrix.join(' ') + ")";
		selectedElement.setAttributeNS(null, "transform", newMatrix);
		//text
		selectedElement.nextSibling.setAttributeNS(null, "transform", newMatrix);

		// end text
		currentX = $evt.clientX;
		currentY = $evt.clientY;

	}

	$scope.deselectElement = function($evt){

//		if(selectedElement != 0){
//
//			if(!isRemovable($evt)){
//
//				$scope.draw();
//			}
//			else{
//
//				var jobs = $scope.plan.jobs;
//
//				//get index to remove
//				var index = -1;
//				for(var i = 0; i < jobs.length; i++)
//				{
//					if(jobs[i].feature.id == parseInt(selectedElement.id) ){
//						index = i;
//						$scope.featuresTORemove.push(jobs[i].feature.id);
//						break;
//					}
//				}
//
//				//remove index
//				if(index != -1){
//					$scope.plan.jobs.splice(index, 1);
//
//					selectedElement.removeAttributeNS(null, "ng-mousemove");
//					selectedElement.removeAttributeNS(null, "ng-mousedown");
//					selectedElement.removeAttributeNS(null, "ng-mouseup");
//					$compile(selectedElement)($scope);
//					selectedElement = 0;
//
//					$scope.draw();
//				}
//
//			}				
//		}
		
		if(selectedElement != 0){
			$scope.draw();
		}
	}

	function isRemovable(evt){

		//console.log("evt.clientX " + evt.clientX + " evt.clientY " + evt.clientY );

		var xGrid = document.getElementById("xGrid");
		var rectXGrid = xGrid.getBoundingClientRect();
		//console.log(rectXGrid.top, rectXGrid.right, rectXGrid.bottom, rectXGrid.left);
		//if under the x axis  -> true
		if(evt.clientY > rectXGrid.top){
			return true;
		}

		//if on the left of y axis -> true
		var yGrid = document.getElementById("yGrid");
		var rectYGrid = yGrid.getBoundingClientRect();
		//console.log(rectYGrid.top, rectYGrid.right, rectYGrid.bottom, rectYGrid.left);
		if(evt.clientX < rectXGrid.left){
			return true;
		}

		//if on the right of ydx axis -> true
		var ydxGrid = document.getElementById("ydxGrid");
		var rectYdxGrid = ydxGrid.getBoundingClientRect();
		//console.log(rectYdxGrid.top, rectYdxGrid.right, rectYdxGrid.bottom, rectYdxGrid.left);
		if(evt.clientX > rectXGrid.right){
			return true;
		}

		return false;
	}

	$scope.cancel = function() {

		$scope.getReleasePlan($scope.release.id)
		.then(
				function(response) {
					$scope.featuresTORemove = [];
					addDependenciesTOPlan(response.data);
					$scope.plan = response.data;
					$scope.showReleasePlan = true;

					$scope.draw();
				},
				function(response) {
					$scope.showReleasePlan = false;
					$scope.messageReleasePlan = "Error: "+response.status + " " + response.statusText;
				}
		);

	};

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
		$scope.forceReleasePlan($scope.release.id, bln)
		.then(
				function(response) {
					$scope.startPoint($location.search().releaseId);
				},
				function(response) {
					alert("Error: "+response.status + " " + response.statusText);
				}
		);   
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
	 * start point function
	 */
	$scope.startPoint($location.search().releaseId);


}]);