var app = angular.module('w5app');
app.controllerProvider.register('release-details', ['$scope', '$location', '$http', '$compile',
	                            function ($scope, $location, $http,  $compile) {
	/*
 	* REST methods
 	*/
	//grazie Romana
	var baseURL = "http://62.14.219.13:3000/api/ui/v1/projects/1";
	$scope.getReleasePlan = function (releaseId) {
		
		var url = baseURL + '/releases/' + releaseId + '/plan';
		return $http({
			method: 'GET',
			url: url
		});
	}
	
	$scope.getReleases = function () {
 		return $http({
			method: 'GET',
			url: baseURL + '/releases'
		});
 	}
	

	/*
 	* All methods
 	*/
	$scope.showReleasePlan = false;
    $scope.messageReleasePlan = "Loading ...";
    $scope.plan = {};
    $scope.release = {};
    $scope.featuresTORemove = [];

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
			
			"fontsize": 13,
			"fontfamily": "Helvetica Neue",
	};


	var mappingXDateJSONObject = {};
	mappingXDateJSONObject["Xinterval"] = 0;
	mappingXDateJSONObject["Yinterval"] = 0;
	//name -> y
	//1472688000000 -> x
 	
	
	$scope.getRelease = function (releaseId) {

 		$scope.getReleases()
 	    .then(
 	        function(response) {
 	        	   
 	        	for(var i =0; i< response.data.length; i++){
 	        		   if(response.data[i].id == parseInt(releaseId)){
 	        			 $scope.release = response.data[i];
 	        			  break;
 	        		   }
 	        	 }
 	        	
 	        	 $scope.getReleasePlan(releaseId)
      		    .then(
      		        function(response) {
      				    $scope.plan = response.data;
      			        $scope.showReleasePlan = true;
      			        $scope.draw();
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
	
	//remove feature to release
	$scope.removeFeatureFromRelease = function (releaseId, featureIds){

		var url =  baseURL + '/releases/'+ releaseId + '/features';
		for(var i=0 ; i< featureIds.length; i++){
			if(i == 0){
				//url = url + "?featureId[" + i + "]=" + featureIds[i];
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
	
	$scope.draw = function(){
		
		//adapt CONSTANT by chart element
		var chart = document.getElementById("chart");
	    var newValue = chart.parentNode.clientWidth - CONSTANT.xStart;
	    CONSTANT["xEnd"] = newValue;
	    CONSTANT["xEndAsString"] = ""+newValue;
	    
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
		var resources = [];
	    var jobs = $scope.plan.jobs;
	    dates.push(deadLineRelease)
		for (var i = 0; i < jobs.length; i++) {
	    	dates.push(new Date(jobs[i].starts));
	    	dates.push(new Date(jobs[i].ends));
	    	resources.push(jobs[i].resource.name);
		}
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
		var interval =  (CONSTANT.xEnd - CONSTANT.xStart) / numberOfInterval;
		mappingXDateJSONObject["Xinterval"] = interval;
		
		//draw vertical lines
		drawVerticalGridLines(interval, numberOfInterval, indexDeadLine);
		//draw vertical days
		var nextMaxDayPlus1 = new Date(maxDate.getTime());
		nextMaxDayPlus1.setDate(deadLineRelease.getDate()+1);
		
		var dateArray = getDates(minDate, nextMaxDayPlus1); 
		drawLabelVerticalGridLines(interval, numberOfInterval, dateArray);
		
		//draw horizontal lines
		var uniques = resources.unique();
		
		numberOfInterval = uniques.length + CONSTANT.plusNumberHorizontallGrid;
		interval =  (CONSTANT.yEnd - CONSTANT.yStart) / numberOfInterval;
		mappingXDateJSONObject["Yinterval"] = interval;
		drawHorizontalGridLines(interval, numberOfInterval);
		drawLabelHorizontalGridLines(interval,numberOfInterval,uniques);
		
		drawData($scope.plan.jobs);
	
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

	function drawLabelHorizontalGridLines(interval, numberOfInterval, uniques) {
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
			
			var result = font_size_to_fit(uniques[i], CONSTANT.fontfamily, CONSTANT.xStart);
			if(result < CONSTANT.fontsize){
				element.setAttribute("style", "font-size: " + result + "px");
			}
			
			element.textContent  = uniques[i];
		
			mappingXDateJSONObject[uniques[i]] = y;
			
			var horizontalLineGrid = document.getElementById("horizontalLabelLineGrid");
			horizontalLineGrid.appendChild(element); 	
			
		}
	}
//ok
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
	
	function drawData(jobs) {
		
		for(var i = jobs.length-1; i>=0; i--){
			//rectangle
			var job = jobs[i];
			
			var element = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
			var date = new Date(job.starts);
			var newX = mappingXDateJSONObject[""+date.getTime()];
			var newY = mappingXDateJSONObject[""+job.resource.name]-(mappingXDateJSONObject.Yinterval/2);
			element.setAttribute("id", ""+job.feature.id);
			element.setAttribute("x", "" + newX);
			element.setAttribute("y", "" + newY);
			
			var startDate = new Date(jobs[i].starts);
			var endDate = new Date(jobs[i].ends);
			var timeDiff = Math.abs(endDate.getTime() - startDate.getTime());
			var diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24)); 
			
			var width = mappingXDateJSONObject.Xinterval * diffDays;
			element.setAttribute("width", "" + width);
			element.setAttribute("height", "" + mappingXDateJSONObject.Yinterval);
			element.setAttribute("transform", "matrix(1 0 0 1 0 0)");
			element.setAttribute("ng-mousedown", "selectElement($event)");
			element.setAttribute("style", "cursor: move;");
	
			var data = document.getElementById("data");
			
			data.appendChild(element);
			$compile(element)($scope);
			
			
			//text
			var element = document.createElementNS('http://www.w3.org/2000/svg', 'text');
			var newXText = newX + (width/2);
			var newYText = newY + (mappingXDateJSONObject.Yinterval/2);
			element.setAttribute("id", "text_"+job.feature.id);
			element.setAttribute("x", newXText);
			element.setAttribute("y", newYText);
			element.setAttribute("alignment-baseline", "middle");
			element.setAttribute("text-anchor", "middle");
			element.setAttribute("fill", "white");
			element.setAttribute("transform", "matrix(1 0 0 1 0 0)");
			element.setAttribute("style", "word-break: keep-all;");
			element.setAttribute("ondblclick", "showReplanRelease(event)");
			//element.textContent  = job.feature.name;
			
			var myText = document.getElementById("myText");
			var name = job.feature.name;
			for (var x = name.length-3; x>0; x-=3){
				myText.textContent = name.substring(0,x);
		        if ( myText.clientWidth <= width){
	            	var truncateName  = name.substring(0,x)+"...";
	            	element.innerHTML   = "<title>" + job.feature.id + " " + name + "</title>" + truncateName;
	            	break;
	            }
	        }
			myText.textContent ="";
			data.appendChild(element);
			$compile(element)($scope);

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

		if(selectedElement != 0){
			
			if(!isRemovable($evt)){
				
				 $scope.draw();
			}
			else{
				
				var jobs = $scope.plan.jobs;
				
				//get index to remove
				var index = -1;
				for(var i = 0; i < jobs.length; i++)
				{
					if(jobs[i].feature.id == parseInt(selectedElement.id) ){
						index = i;
						$scope.featuresTORemove.push(jobs[i].feature.id);
						break;
					}
				}
		
				//remove index
				if(index != -1){
					$scope.plan.jobs.splice(index, 1);
					
					selectedElement.removeAttributeNS(null, "ng-mousemove");
					selectedElement.removeAttributeNS(null, "ng-mousedown");
					selectedElement.removeAttributeNS(null, "ng-mouseup");
					$compile(selectedElement)($scope);
					selectedElement = 0;
					
					$scope.draw();
				}
				
			}				
		}
	}
	
	function isRemovable(evt){

		console.log("evt.clientX " + evt.clientX + " evt.clientY " + evt.clientY );
		
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
		console.log(rectYdxGrid.top, rectYdxGrid.right, rectYdxGrid.bottom, rectYdxGrid.left);
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
    
	
	/**
	 * Help methods
	 */
	function getDates(startDate, stopDate) {
	    var dateArray = new Array();
	    var currentDate = startDate;
	    while (currentDate <= stopDate) {
	        dateArray.push( new Date (currentDate) )
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
 	$scope.getRelease($location.search().releaseId);
	
}]);