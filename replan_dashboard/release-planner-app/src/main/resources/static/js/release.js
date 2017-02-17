var app = angular.module('w5app');
app.controllerProvider.register('release-utilities', ['$scope', '$location', '$http', function ($scope, $location, $http) {
	
	/*
 	* REST methods
 	*/
	var baseURL = "release-planner-app/replan/projects/1";
	
	$scope.getReleases = function () {
		return $http({
			method: 'GET',
			url: baseURL + '/releases'
		});
	}
	$scope.removeRelease = function (release){

		return $http({
			method: 'DELETE',
			url: baseURL + '/releases/'+ release.id
		});	 
	};
	
	//Modifies a given release (modify only name, description and deadline )
	$scope.updateRelease = function (release){

		return $http({
			method: 'PUT',
			url: baseURL + '/releases/'+ release.id,
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: release
		});	 
	};
	
	$scope.addReleaseToProject = function (release){

		return $http({
			method: 'POST',
			url: baseURL + '/releases/',
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: release
		});	
	};
	
	$scope.getProjectResources = function(){

		return $http({
			method: 'GET',
			url: baseURL + '/resources'
		});
	};

	$scope.deleteResoucesFromRelease = function (releaseId, resourceIds){

		var url =  baseURL + '/releases/'+ releaseId + '/resources';

		for(var i=0 ; i< resourceIds.length; i++){

			if(i == 0){
				url = url + "?ResourceId=" + resourceIds[i];
			}
			else{
				url = url + "," + resourceIds[i];
			}
		}  

		return $http({
			method: 'DELETE',
			url: url
		}); 
	};
	
	$scope.addResoucesToRelease = function (releaseId, resourceIds){

		//create data OBj like {"_json": [{"resource_id": 3}]}
		var arr = [];
		for(var i = 0 ; i< resourceIds.length; i++){
			var resourceIdObj = {};
			resourceIdObj["resource_id"] = resourceIds[i];
			arr.push(resourceIdObj);
		}

		var dataObj = {};
		dataObj["_json"] = arr;

		return $http({
			method: 'POST',
			url: baseURL + '/releases/'+ releaseId + '/resources',
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: dataObj
		});	
	};
	
	/*
 	* All methods
 	*/
	$scope.showRelease = false;
    $scope.messageRelease = "Loading ...";
    
	$scope.isUpdate = false;
	$scope.deadlineStyle = '';
	$scope.releasesTORemove = [];
	$scope.releasesTOAdd = [];
	$scope.release={};
	$scope.release["resources"]=[];
	$scope.showResourcesAddListBox = false;
	$scope.releasesOnLoad = [];
	
	$scope.getRelease = function (releaseId) {
		
		$scope.getReleases()
		.then(
				function(response) {
					
					$scope.showRelease = true;
					
					for(var i = 0; i< response.data.length; i++){
						if(response.data[i].id == parseInt(releaseId)){
							$scope.release = response.data[i];
							$scope.isUpdate = true;
							$scope.deadlineStyle = 'my-input-success';
							break;
						}
					}
					//store the initial releases
					$scope.releasesOnLoad = [];
					for(var i =0; i< $scope.release.resources.length; i++){
						$scope.releasesOnLoad.push($scope.release.resources[i]);
					}
					
					// Create a jqxListBox
					var source =
					{
							datatype: "json",
							datafields: [
							             { name: 'id' },
							             { name: 'name' }
							             ],
							             id: 'id',
							             localdata: $scope.release.resources
					};

					$scope.dataAdapterResourcesListBox = new $.jqx.dataAdapter(source);
					
					//data input
					$('#dateInputUpdate').jqxDateTimeInput('setDate', new Date($scope.release.deadline));
					//start at input
					$('#dateInputUpdateStartAt').jqxDateTimeInput('setDate', new Date($scope.release.starts_at));
					
				},

				function(response) {
					$scope.showRelease = false;
					$scope.messageRelease = "Error: "+response.status + " " + response.statusText;
				}
		);

	}
	
	// Create a jqxListBox
	// prepare the data for jqxListBox
	var source =
	{
			datatype: "json",
			datafields: [
			             { name: 'id' },
			             { name: 'name' }
			             ],
			             id: 'id',
			             localdata: $scope.release.resources
	};

	$scope.dataAdapterResourcesListBox = new $.jqx.dataAdapter(source);
	$scope.addClassHasErrorResourcesAreRequired = ''; 
	$scope.showResourcesAreRequired = false; 
	//settings
	$scope.settingsResourcesListBox = { 
			source: $scope.dataAdapterResourcesListBox,
			valueMember: "id",
			displayMember: "name", 
			width: '100%',
			height: '200',
			allowDrag: true,
			allowDrop: true,
			renderer: function (index, label, value) {

				var glyphicon = '<span class="glyphicon glyphicon-user" style="font-size:15px;"></span>';
				var table = '<table style="color: inherit; font-size: inherit; font-style: inherit;"><tr><td style="width: 35px;">' + glyphicon + '</td><td>' + label  + '-' + value +  '</td></tr></table>';
				return table;
			}
	};
	
	$scope.selectItemResourcesListBox = function (event) {
		if (event.args) {
			var item = event.args.item;
			if (item) {
				$scope.log =  "Label: " + item.label + " Value: " + item.value + " (index)";
			}
		}
	};
	
	//dragEnd refer to dragEnd selected item
	$("#resourcesListBox").on('dragEnd', function (event) {
		if (event.args.label) {
			var ev = event.args.originalEvent;
			var x = ev.pageX;
			var y = ev.pageY;
			if (event.args.originalEvent && event.args.originalEvent.originalEvent && event.args.originalEvent.originalEvent.touches) {
				var touch = event.args.originalEvent.originalEvent.changedTouches[0];
				x = touch.pageX;
				y = touch.pageY;
			}

			var id = "#resourcesListBox";
			var offset = $(id).offset();
			var width = $(id).width();
			var height = $(id).height();
			var right = parseInt(offset.left) + width;

			if (x >= parseInt(offset.left) && x > right) {
				
				//new logic 07.09.2016
				if(containsObject(event.args.value, $scope.releasesTOAdd)){
					removeElementWithId(event.args.value, $scope.releasesTOAdd);
				}
				else{
					$scope.releasesTORemove.push(event.args.value);	
				}
				
				
				//get index to remove
				var index = -1;
				for(var i = 0; i < $scope.release.resources.length; i++)
				{
					if($scope.release.resources[i].id == event.args.value){
						index = i;
						break;
					}
				}
				//remove index
				if(index != -1){
					$scope.release.resources.splice(index, 1);
				}

				//refresh
				var source =
				{
						datatype: "json",
						datafields: [
						             { name: 'id' },
						             { name: 'name' }
						             ],
						             id: 'id',
						             localdata: $scope.release.resources
				};

				$scope.dataAdapterResourcesListBox = new $.jqx.dataAdapter(source);
				if($scope.release.resources == 0){
					$scope.addClassHasErrorResourcesAreRequired = 'my-error-border-color';
					$scope.showResourcesAreRequired = true;
				}else{
					$scope.addClassHasErrorResourcesAreRequired = '';
					$scope.showResourcesAreRequired = false;	
				}
				//refresh the ResourcesAddListBox
				refreshResourcesAddListBox();
			}
		}
	});
	
	/*
	 * resourcesAddListBox
	 */
	var emptyArray = [];
	// Create a jqxListBox
 	// prepare the data for jqxListBox
	var source1 =
	{
			datatype: "json",
			datafields: [
			             { name: 'id' },
			             { name: 'name' }
			             ],
			             id: 'id',
			             localdata: emptyArray
	};

	$scope.dataAdapterResourcesAddListBox = new $.jqx.dataAdapter(source1);
	
	//settings
	$scope.settingsResourcesAddListBox = { 
			source: $scope.dataAdapterResourcesAddListBox,
			valueMember: "id",
			displayMember: "name", 
			width: '100%',
			height: '200',
			allowDrag: true,
			allowDrop: false,
			renderer: function (index, label, value) {
				var glyphicon = '<span class="glyphicon glyphicon-user" style="font-size:15px;"></span>';
				var table = '<table style="color: inherit; font-size: inherit; font-style: inherit;"><tr><td style="width: 35px;">' + glyphicon + '</td><td>' + label  + '-' + value +  '</td></tr></table>';
				return table;
			}
	};

	$scope.selectItemResourcesAddListBox = function (event) {
		if (event.args) {
			var item = event.args.item;
			if (item) {
				$scope.log1 =  "Label: " + item.label + " Value: " + item.value + " (index)";
			}
		}
	};
	

	//dragEnd refer to dragEnd selected item
	$("#resourcesAddListBox").on('dragEnd', function (event) {
		if (event.args.label) {
			var ev = event.args.originalEvent;
			var x = ev.pageX;
			var y = ev.pageY;
			if (event.args.originalEvent && event.args.originalEvent.originalEvent && event.args.originalEvent.originalEvent.touches) {
				var touch = event.args.originalEvent.originalEvent.changedTouches[0];
				x = touch.pageX;
				y = touch.pageY;
			}

			var id = "#resourcesListBox";
			var offset = $(id).offset();
			var width = $(id).width();
			var height = $(id).height();
			var right = parseInt(offset.left) + width;
			var right = parseInt(offset.left) + width;
			var bottom = parseInt(offset.top) + height;
			if (x >= parseInt(offset.left) && x <= right) {
				if (y >= parseInt(offset.top) && y <= bottom) {

					//new logic 07.09.2016
					if(!containsObject(event.args.value, $scope.releasesOnLoad)){
						$scope.releasesTOAdd.push(event.args.value);	
					}
					
					var myresources = {};
					myresources["id"]= event.args.value;
					myresources["name"]= event.args.label;
					
					$scope.release.resources.push(myresources);
					
					
					$scope.addClassHasErrorResourcesAreRequired = '';
					$scope.showResourcesAreRequired = false;
				}
			}
		}
	});

	refreshResourcesAddListBox = function(){

		$scope.getProjectResources()
		.then(
				function(response) {
					
					var resourcesAddListBox=[];
					
					for(var i=0; i< response.data.length; i++){

						var found = false;
						for(var j = 0; j< $scope.release.resources.length; j++){
							if($scope.release.resources[j].id == response.data[i].id){
								found = true;
								break;
							}
						}
						if(!found){
							resourcesAddListBox.push(response.data[i]);
						}

					}

					var source =
					{
							datatype: "json",
							datafields: [
							             { name: 'id' },
							             { name: 'name' }
							             ],
							             id: 'id',
							             localdata: resourcesAddListBox
					};

					$scope.dataAdapterResourcesAddListBox = new $.jqx.dataAdapter(source);

				},

				function(response) {
					$scope.showFeatures = false;
					$scope.messageFeatures = "Error: "+response.status + " " + response.statusText;	
				}
		);
	}
	//commented on 17.02.2017 before saner conference
//	/*
//	 * add resources buttons
//	 */
//	$scope.addDefaultResources = function($event){
//
//		$scope.getProjectResources()
//		.then(
//				function(response) {
//
//					for(var i=0; i< response.data.length; i++){
//						var found = false;
//						for(var j = 0; j< $scope.release.resources.length; j++){
//							if($scope.release.resources[j].id == response.data[i].id){
//								found = true;
//								break;
//							}
//						}
//						if(!found){
//							$scope.release.resources.push(response.data[i]);
//						}
//					}
//
//					var source =
//					{
//							datatype: "json",
//							datafields: [
//							             { name: 'id' },
//							             { name: 'name' }
//							             ],
//							             id: 'id',
//							             localdata: $scope.release.resources
//					};
//
//					$scope.dataAdapterResourcesListBox = new $.jqx.dataAdapter(source);
//					if($scope.release.resources == 0){
//						$scope.addClassHasErrorResourcesAreRequired = 'my-error-border-color';
//						$scope.showResourcesAreRequired = true;
//					}else{
//						$scope.addClassHasErrorResourcesAreRequired = '';
//						$scope.showResourcesAreRequired = false;	
//					}
//					
//					
//					//update the resourcesAddListBox
//					var toggled = $("#addResouceToggleBtn").jqxToggleButton('toggled');
//
//					if(toggled){
//						refreshResourcesAddListBox();	
//					}
//
//
//				},
//
//				function(response) {
//					$scope.showFeatures = false;
//					$scope.messageFeatures = "Error: "+response.status + " " + response.statusText;	
//				}
//		);
//
//	};


	$("#addResouceToggleBtn").jqxToggleButton({toggled: false });

	$scope.addSpecificResources = function(event){

		$scope.showResourcesAddListBox = $("#"+ event.target.id).jqxToggleButton('toggled');

		refreshResourcesAddListBox();
	};
	
	$("#dateInputUpdate").jqxDateTimeInput({ width: '100%', height: '25px', formatString: 'yyyy-MM-dd'/*, min: new Date(year, month, day)*/});
	$("#dateInputUpdateStartAt").jqxDateTimeInput({ width: '100%', height: '25px', formatString: 'yyyy-MM-dd'/*, min: new Date(year, month, day)*/});
	


	addRemoveResourcesToFromReleaseDate = function(){

		//delete and add resources
		if($scope.releasesTORemove.length > 0 && $scope.releasesTOAdd.length > 0){

			//1. delete resource
			$scope.deleteResoucesFromRelease($scope.release.id, $scope.releasesTORemove)
			.then(
					function(response) {

						//2. add resources
						$scope.addResoucesToRelease($scope.release.id, $scope.releasesTOAdd)
						.then(
								function(response) {
									$location.path("/release-planner-app/main");
								},
								function(response) {
									alert("Error: "+response.status + " " + response.statusText);
								}
						);

					},
					function(response) {
						alert("Error: "+response.status + " " + response.statusText);
					}
			);

		}
		//remove only
		else if($scope.releasesTORemove.length > 0 && $scope.releasesTOAdd.length == 0){
			//1. delete resource
			$scope.deleteResoucesFromRelease($scope.release.id, $scope.releasesTORemove)
			.then(
					function(response) {
						$location.path("/release-planner-app/main");
					},
					function(response) {
						alert("Error: "+response.status + " " + response.statusText);
					}
			);
		}
		//add only
		else if($scope.releasesTORemove.length == 0 && $scope.releasesTOAdd.length > 0){
			//1. add resources
			$scope.addResoucesToRelease($scope.release.id, $scope.releasesTOAdd)
			.then(
					function(response) {
						$location.path("/release-planner-app/main");
					},
					function(response) {
						alert("Error: "+response.status + " " + response.statusText);
					}
			);

		}else {

			$location.path("/release-planner-app/main");
		}
	};
	
	var now = new Date();
	var year = now.getFullYear();
	var month = now.getMonth();
	month = month + 1;
	var day = now.getDate();
	//new Date(year, month, day);
	$("#dateInput").jqxDateTimeInput({ width: '100%', height: '25px', formatString: 'yyyy-MM-dd', min: new Date(year, month, day)});
	$("#dateInputStartAt").jqxDateTimeInput({ width: '100%', height: '25px', formatString: 'yyyy-MM-dd', min: new Date(year, month, day)});
	
	
	var nowPlusOneMonth = new Date();
	nowPlusOneMonth.setMonth(nowPlusOneMonth.getMonth() + 1);
	$('#dateInput').jqxDateTimeInput('setDate', nowPlusOneMonth);
	$('#dateInputStartAt').jqxDateTimeInput('setDate', nowPlusOneMonth);
	
	$scope.add = function(){
		var dateDeadLine = $("#dateInput").jqxDateTimeInput('getDate');
		var strDateDeadLine = getStringSUPERSEDEDate(dateDeadLine.getTime());
		$scope.release.deadline = strDateDeadLine;

		var dateStartAt = $("#dateInputStartAt").jqxDateTimeInput('getDate');
		var strDateStartAt = getStringSUPERSEDEDate(dateStartAt.getTime());
		$scope.release.starts_at = strDateStartAt;

		
		if($scope.release.resources.length  == 0){
			$scope.addClassHasErrorResourcesAreRequired = 'my-error-border-color';
			$scope.showResourcesAreRequired = true;
		}
		else{
			//console.log($scope.release);
			$scope.addReleaseToProject($scope.release)
			.then(
					function(response) {
						$scope.release = response.data;
						addRemoveResourcesToFromReleaseDate();	
						
					},
					function(response) {
						alert("Error: "+response.status + " " + response.statusText);
					}
			);
			
			
		}
		
	}
	
	$scope.update = function(){

		//deadLine
		var dateDeadline = $("#dateInputUpdate").jqxDateTimeInput('getDate');
		var strDateDeadLine = getStringSUPERSEDEDate(dateDeadline.getTime());
		$scope.release.deadline = strDateDeadLine;
	
		//starts_at
		var dateStartAt = $("#dateInputUpdateStartAt").jqxDateTimeInput('getDate');
		var strDateStartAt = getStringSUPERSEDEDate(dateStartAt.getTime());
		$scope.release.starts_at = strDateStartAt;
		
		$scope.updateRelease($scope.release)
		.then(
				function(response) {
					$scope.release = response.data;
					addRemoveResourcesToFromReleaseDate();	
				},
				function(response) {
					alert("Error: "+response.status + " " + response.statusText);
				}
		);
	}
	$scope.cancel = function(){
		
		if($scope.isUpdate){
			$location.path("/release-planner-app/release_details").search({releaseId: ''+$scope.releases[i].id });
		}
		else{
			$location.path("/release-planner-app/main");
		}
	};
	
	$scope.remove = function(){
		
		var message= "Are you sure?";
		var removeReleaseWindow = document.getElementById("removeReleaseWindow");
		if(removeReleaseWindow == null){
			$(document.body).append('<div id="removeReleaseWindow">'+
					'<div>Remove Release</div>'+
					'<div>'+
					'<div style="margin-top: 15px; text-align: center;">'+
					message +
					'</div>'+
					'<div>'+
					'<div style="float: right; margin-top: 15px;">'+
					'<input type="button" id="removeOk" class="my-button" style="margin-right: 10px" value="Remove" />'+
					'<input type="button" id="removeCancel" class="my-button" value="Cancel" />'+
					'</div>'+
					'</div>'+
					'</div>'+


			'</div>');
		}
		
		$("#removeOk").jqxInput({ height: 25});
		$("#removeOk").on('click', function (){
			
			$("#removeReleaseWindow").jqxWindow('close');
			
			$scope.removeRelease($scope.release)
			.then(
					function(response) {
						$location.path("/release-planner-app/main");
						
					},
					function(response) {
						alert("Error: "+response.status + " " + response.statusText);
					}
			);
		});
		
		$("#removeCancel").jqxInput({ height: 25});
		$("#removeCancel").on('click', function (){
			$("#removeReleaseWindow").jqxWindow('close');
		});
		
		
		var featureForm = $('#resourcesListBox');
		var offset = featureForm.offset();
		var width = featureForm.width();
		var height = featureForm.height();
		
		var removeReleaseWindowJquery = $('#removeReleaseWindow');
		var checkDelayWindowHeight = removeReleaseWindowJquery.height();
		
		$("#removeReleaseWindow").jqxWindow({ height: 'auto', width: 400, position: { x: (offset.left+width), y: offset.top + (height/2) - checkDelayWindowHeight}, isModal: true, modalOpacity: 0.3, showCloseButton: false });
		$("#removeReleaseWindow").jqxWindow('open');

	};
	
	/**
	 * start point method
	 */
	$scope.getRelease($location.search().releaseId);

	
	/**
	 * Help methods
	 */
	addXMonthsToDate = function(deadLine, x){
		
		var deadlineDate = new Date(deadLine);
		deadlineDate.setMonth(deadlineDate.getMonth() + x);
		
		return getStringSUPERSEDEDate(deadlineDate.getTime());
		//return "2016-08-31";
	};
	
	function getStringSUPERSEDEDate(time){
		
//		var date = new Date(time);
//	
//		var month = date.getMonth() + 1;
//		var day = date.getDate();
//		
//		var strMonth;
//		if(month <= 9){
//			strMonth ="0" + month;
//		}else{
//			strMonth = ""+month;
//		}
//		
//		var strDay;
//		if(day <= 9){
//			strDay ="0" + day;
//		}else{
//			strDay = ""+day;
//		}
//		
//		var strDate =	date.getFullYear() + "-" + strMonth + "-" + strDay;
//		return strDate;
		var date = new Date(time);
		var n = date.toISOString();
		return n;
	}

	function removeElementWithId(id, list) {
	    var i;
	    var index = -1;
	    for (i = 0; i < list.length; i++) {
	        if (list[i] == id) {
	        	index = i;
	        }
	    }
	    if(index != -1){
	    	list.splice(index, 1);
		}
	}
	
	function containsObject(id, list) {
	    var i;
	    for (i = 0; i < list.length; i++) {
	        if (list[i] == id) {
	            return true;
	        }
	    }

	    return false;
	}
	
}]);