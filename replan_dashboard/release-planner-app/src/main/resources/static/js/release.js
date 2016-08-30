var app = angular.module('w5app');
app.controllerProvider.register('release-utilities', ['$scope', '$location', '$http',
                                                                    function ($scope, $location, $http) {
	/*
 	* REST methods
 	*/
	var baseURL = "http://localhost:3000/api/ui/v1/projects/1";
	
	$scope.getReleases = function () {
		return $http({
			method: 'GET',
			url: baseURL + '/releases'
		});
	}
	//Modifies a given release (modify only name, description and deadline )
	$scope.updateRelease = function (release){

		return $http({
			method: 'PUT',
			url: baseURL + '/releases/'+ release.id,
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
				url = url + "?ResourceId[" + i + "]=" + resourceIds[i];
			}
			else{
				url = url + "&ResourceId[" + i + "]=" + resourceIds[i];
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
			arr.push(featureIdObj);
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
	
	$scope.typeLabel = "Modify";
	$scope.update = true;
	$scope.releasesTORemove = [];
	$scope.releasesTOAdd = [];
	$scope.release={};
	$scope.release["resources"]=[];
	$scope.showResourcesAddListBox = false;
	
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
				},

				function(response) {
					$scope.showReleasePlan = false;
					$scope.messageReleasePlan = "Error: "+response.status + " " + response.statusText;
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

				//get index to remove
				var index = -1;
				for(var i = 0; i < $scope.release.resources.length; i++)
				{
					if($scope.release.resources[i].id == event.args.value){
						index = i;
						$scope.releasesTORemove.push($scope.release.resources[i].id);
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
	
	/*
	 * add resources buttons
	 */
	$scope.addDefaultResources = function($event){

		$scope.getProjectResources()
		.then(
				function(response) {

					for(var i=0; i< response.data.length; i++){
						var found = false;
						for(var j = 0; j< $scope.release.resources.length; j++){
							if($scope.release.resources[j].id == response.data[i].id){
								found = true;
								break;
							}
						}
						if(!found){
							$scope.release.resources.push(response.data[i]);
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
							             localdata: $scope.release.resources
					};

					$scope.dataAdapterResourcesListBox = new $.jqx.dataAdapter(source);

					//update the resourcesAddListBox
					var toggled = $("#addResouceToggleBtn").jqxToggleButton('toggled');

					if(toggled){
						refreshResourcesAddListBox();	
					}


				},

				function(response) {
					$scope.showFeatures = false;
					$scope.messageFeatures = "Error: "+response.status + " " + response.statusText;	
				}
		);

	};


	$("#addResouceToggleBtn").jqxToggleButton({toggled: false });

	$scope.addSpecificResources = function($event){

		$scope.showResourcesAddListBox = $("#"+ event.target.id).jqxToggleButton('toggled');

		refreshResourcesAddListBox();
	};

	/*
	 * deadline buttons
	 */
	$scope.toogleMonthsToggleBtns = function(event){

		var toggled = $("#"+ event.target.id).jqxToggleButton('toggled');

		//reset all toggle buttons
		//layout
		$("#3monthsToggleBtn").jqxToggleButton({toggled: false });
		$("#6monthsToggleBtn").jqxToggleButton({toggled: false });

		//set again
		$("#"+ event.target.id).jqxToggleButton({toggled: toggled});

	}

	addRemoveResourcesToFromReleaseDate = function(response){

		//delete and add resources
		if($scope.releasesTORemove.length > 0 && $scope.releasesTOAdd.length > 0){

			//1. delete resource
			$scope.deleteResoucesFromRelease($scope.release.id, $scope.releasesTORemove)
			.then(
					function(response) {

						//add resources
						$scope.addResoucesToRelease($scope.release.id, $scope.releasesTOAdd)
						.then(
								function(response) {
									$location.path("/release-planner-app/release_details").search({releaseId: ''+$scope.releases[i].id });
								},
								function(response) {
									alert("default", "Error: "+response.status + " " + response.statusText);
								}
						);

					},
					function(response) {
						alert("default", "Error: "+response.status + " " + response.statusText);
					}
			);

		}
		//remove only
		else if($scope.releasesTORemove.length > 0 && $scope.releasesTOAdd.length == 0){

			$scope.deleteResoucesFromRelease($scope.release.id, $scope.releasesTORemove)
			.then(
					function(response) {
						$location.path("/release-planner-app/release_details").search({releaseId: ''+$scope.releases[i].id });
					},
					function(response) {
						alert("default", "Error: "+response.status + " " + response.statusText);
					}
			);
		}
		//add only
		else if($scope.releasesTORemove.length == 0 && $scope.releasesTOAdd.length > 0){

			$scope.addResoucesToRelease($scope.release.id, $scope.releasesTOAdd)
			.then(
					function(response) {

						$location.path("/release-planner-app/release_details").search({releaseId: ''+$scope.releases[i].id });
					},
					function(response) {
						alert("default", "Error: "+response.status + " " + response.statusText);
					}
			);

		}else {

			$location.path("/release-planner-app/release_details").search({releaseId: ''+$scope.releases[i].id });
		}

	};
	
	/**
	 * Help methods
	 */
	addXMonthsToDate = function(deadLine, x){
		var deadlineDate = new Date(deadLine);
		deadlineDate.setMonth(deadlineDate.getMonth() + x);
		var month = deadlineDate.getMonth() + 1;
		var strDate =	deadlineDate.getFullYear() + "-" + month + "-" + deadlineDate.getDate();
		return strDate;
		//return "2016-08-31";
	};


	$scope.addUpdate = function(){
		if("Add" == $scope.typeLabel){
			return;
		}
		else{
			if($("#3monthsToggleBtn").jqxToggleButton('toggled')){
				$scope.release.deadline = addXMonthsToDate($scope.release.deadline, 1);
			}
			else if($("#6monthsToggleBtn").jqxToggleButton('toggled')){
				$scope.release.deadline = addXMonthsToDate($scope.release.deadline, 3);
			}

			$scope.updateRelease($scope.release)
			.then(
					function(response) {
						addRemoveResourcesToFromReleaseDate(response);
					},
					function(response) {
						alert("default", "Error: "+response.status + " " + response.statusText);
					}
			);
		}
	}

	$scope.cancel = function(){
		$location.path("/release-planner-app/release_details").search({releaseId: ''+$scope.releases[i].id });
	};
	
	/**
	 * start point method
	 */
	$scope.getRelease($location.search().releaseId);

}]);