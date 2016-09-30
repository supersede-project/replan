var app = angular.module('w5app');
app.controllerProvider.register('replan-release', ['$scope', '$location', '$http', 
                                  function ($scope, $location, $http) {

	/*
	 * REST methods
	 */

	var baseURL = "http://62.14.219.13:3000/api/ui/v1/projects/1";

	$scope.getFeature = function(featureId){

		return $http({
			method: 'GET',
			url: baseURL + '/features/'+ featureId
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
	$scope.showFeature = false;
	$scope.messageFeature = "Loading ...";
	$scope.feature = {code: null, deadline:"", description:"", effort:"", id: null, name:"", priority: null, release: ""};

	$scope.release = {};

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

					$scope.getFeature($location.search().featureId)
					.then(
							function(response) {
								$scope.feature = response.data;
								$scope.showFeature = true;
							},
							function(response) {
								$scope.showFeature = false;
								$scope.messageFeature = "Error: "+response.status + " " + response.statusText;
							}
					);

				},

				function(response) {
					$scope.showReleasePlan = false;
					$scope.messageReleasePlan = "Error: "+response.status + " " + response.statusText;
				}
		);
	}

	//add feature to release
	$scope.addFeatureToRelease = function (releaseId, featureId){

		//create data OBj like {"_json": [{"feature_id": 3}]}
		var featureIdObj = {};
		featureIdObj["feature_id"] = featureId;

		var arr = [];
		arr.push(featureIdObj);

		var dataObj = {};
		dataObj["_json"] = arr;

		return $http({
			method: 'POST',
			url: baseURL + '/releases/'+ releaseId + '/features',
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: dataObj
		});	 

	};

	//Modifies a given feature
	$scope.updateFeature = function (feature){

		return $http({
			method: 'PUT',
			url: baseURL + '/features/'+ feature.id,
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: feature
		});	 
	};

	appendReplaneWindows = function(type, message) {

		var featureForm = $('#featureForm');
		var offset = featureForm.offset();
		var width = featureForm.width();
		var height = featureForm.height();

		switch (type) {
		case "success":

			var checkSuccessWindow = document.getElementById("successWindow");
			if(checkSuccessWindow == null){
				$(document.body).append('<div id="successWindow">'+
						'<div>Re-plan Status</div>'+
						'<div>'+
						'<div style="margin-top: 15px; text-align: center;">'+
						message +
						'</div>'+
						'<div>'+
						'<div style="text-align: center; margin-top: 15px;">'+
						'<input type="button" id="successOk" style="margin-right: 10px; width: 350px" value="See releases" />'+
						'</div>'+
						'</div>'+
						'</div>'+
				'</div>');
			}
			$("#successOk").jqxInput({ height: 25});
			$("#successOk").on('click', function (){
				$("#successWindow").jqxWindow('close');
				$location.path("/release-planner-app/release_details");
			});

			var successWindow = $('#successWindow');
			var heightsuccessWindow = successWindow.height();

			$("#successWindow").jqxWindow({ height: 'auto', width: 400, position: { x: offset.left + (width/2)-150, y: offset.top + (height/2) - heightsuccessWindow}, isModal: true, modalOpacity: 0.3, showCloseButton: false });
			$("#successWindow").jqxWindow('open');

			break;

		case "delay":
			var checkDelayWindow = document.getElementById("delayWindow");
			if(checkDelayWindow == null){
				$(document.body).append('<div id="delayWindow">'+
						'<div>Re-plan Status</div>'+
						'<div>'+
						'<div style="margin-top: 15px; text-align: center;">'+
						message +
						'</div>'+
						'<div>'+
						'<div style="float: right; margin-top: 15px;">'+
						'<input type="button" id="delayOk" class="my-button" style="margin-right: 10px" value="Accept" />'+
						'<input type="button" id="delayCancel" class="my-button" value="Cancel" />'+
						'</div>'+
						'</div>'+
						'</div>'+


				'</div>');
			}
			$("#delayOk").jqxInput({ height: 25});
			$("#delayOk").on('click', function (){
				//appendReplaneWindows("success", "Re-plan success.");
				$("#delayWindow").jqxWindow('close');
				$scope.addFeatureToRelease($scope.release.id, $scope.feature.id)
				.then(
						function(response) {
							appendReplaneWindows("success", "Re-plan success.");
						},
						function(response) {
							appendReplaneWindows("default", "Error: "+response.status + " " + response.statusText);
						}
				);

			});
			$("#delayCancel").jqxInput({ height: 25});
			$("#delayCancel").on('click', function (){
				$("#delayWindow").jqxWindow('close');
			});
			var delayWindow = $('#delayWindow');
			var heightdelayWindow = delayWindow.height();

			$("#delayWindow").jqxWindow({ height: 'auto', width: 400, position: { x: offset.left + (width/2)-150, y: offset.top + (height/2) - heightdelayWindow}, isModal: true, modalOpacity: 0.3, showCloseButton: false });
			$("#delayWindow").jqxWindow('open');

			break;

		case "omitted":
			var checkOmittedWindow = document.getElementById("omittedWindow");
			if(checkOmittedWindow == null){
				$(document.body).append('<div id="omittedWindow">'+
						'<div>Re-plan Status</div>'+
						'<div>'+
						'<div style="margin-top: 15px; text-align: center;">'+
						message +
						'</div>'+
						'<div>'+
						'<div style="float: right; margin-top: 15px;">'+
						'<input type="button" id="omittedOk" class="my-button" style="margin-right: 10px" value="Accept" />'+
						'<input type="button" id="omittedCancel" class="my-button" value="Cancel" />'+
						'</div>'+
						'</div>'+
						'</div>'+


				'</div>');
			}
			$("#omittedOk").jqxInput({ height: 25});
			$("#omittedOk").on('click', function (){
				$("#omittedWindow").jqxWindow('close');
				//appendReplaneWindows("success", "Re-plan success.");
				$scope.addFeatureToRelease($scope.release.id, $scope.feature.id)
				.then(
						function(response) {
							appendReplaneWindows("success", "Re-plan success.");
						},
						function(response) {
							appendReplaneWindows("default", "Error: "+response.status + " " + response.statusText);
						}
				);
			});
			$("#omittedCancel").jqxInput({ height: 25});
			$("#omittedCancel").on('click', function (){
				$("#omittedWindow").jqxWindow('close');
			});
			var omittedWindow = $('#omittedWindow');
			var heightomittedWindow = omittedWindow.height();

			$("#omittedWindow").jqxWindow({ height: 'auto', width: 400, position: { x: offset.left + (width/2)-150, y: offset.top + (height/2) - heightomittedWindow}, isModal: true, modalOpacity: 0.3, showCloseButton: false });
			$("#omittedWindow").jqxWindow('open');

			break;

		default:
			var checkDefaultWindow = document.getElementById("defaultWindow");
			if(checkDefaultWindow == null){
				$(document.body).append('<div id="defaultWindow">'+
						'<div>Re-plan Status</div>'+
						'<div>'+
						'<div style="margin-top: 15px; text-align: center;">'+
						message +
						'</div>'+
						'<div>'+
						'<div style="float: right; margin-top: 15px;">'+
						'<input type="button" id="defaultCancel" class="my-button" value="Cancel" />'+
						'</div>'+
						'</div>'+
						'</div>'+
				'</div>');
			}

			$("#defaultCancel").jqxInput({ height: 25});
			$("#defaultCancel").on('click', function (){
				$("#defaultWindow").jqxWindow('close');
			});
			var defaultWindow = $('#defaultWindow');
			var heightdefaultWindow = defaultWindow.height();
	
			$("#defaultWindow").jqxWindow({ height: 'auto', width: 400, position: { x: offset.left + (width/2)-150, y: offset.top + (height/2) - heightdefaultWindow}, isModal: true, modalOpacity: 0.3, showCloseButton: false });
			$("#defaultWindow").jqxWindow('open');
	
			break;
		}

	}

	$scope.replanRelease = function(){

		$scope.updateFeature($scope.feature)
		.then(
				function(response) {
					//the 4 windows

					if(new Date($scope.feature.deadline) <= new Date()){
						appendReplaneWindows("default", "The feature deadline is before today." );
					}
					else if(new Date($scope.feature.deadline) >= new Date($scope.release.deadline)){
						appendReplaneWindows("delay", "The release will be delayed if this feature is included." );
					}
					// some_variable is either null, undefined, 0, NaN, false, or an empty string
					else if(!$scope.feature.deadline){
						appendReplaneWindows("omitted", "The release will be on time but the feature deadline has been omitted.");
					}
					else{
						//appendReplaneWindows("success", "Re-plan success.");
						$scope.addFeatureToRelease($scope.release.id, $scope.feature.id)
						.then(
								function(response) {
									appendReplaneWindows("success", "Re-plan success.");
								},
								function(response) {
									appendReplaneWindows("default", "Error: "+response.status + " " + response.statusText);
								}
						);
					}
				},
				function(response) {
					appendReplaneWindows("default", "Error: "+response.status + " " + response.statusText);
				}
		);
	}

	/**
	 * start point method
	 */
	$scope.getRelease($location.search().releaseId);

}]);