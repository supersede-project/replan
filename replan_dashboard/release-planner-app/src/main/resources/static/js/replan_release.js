var app = angular.module('w5app');
app.controllerProvider.register('replan-release', ['$scope', '$location', '$http', 
                                  function ($scope, $location, $http) {

	/*
	 * REST methods
	 */
	var baseURL = "release-planner-app/replan/projects/tenant";

	$scope.getFeature = function(featureId){

		return $http({
			method: 'GET',
			url: baseURL + '/features/'+ featureId
		});
	}

	//Modifies a given feature
	$scope.updateFeature = function (feature){

		return $http({
			method: 'PUT',
			url: baseURL + '/features/'+ feature.id,
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: feature
		});	 
	};

	$scope.getReleases = function () {
		return $http({
			method: 'GET',
			url: baseURL + '/releases'
		});
	}

	//ok
	$scope.getReleaseFeatures = function (releaseId) {

		var url = baseURL + '/releases/' + releaseId + '/features';
		return $http({
			method: 'GET',
			url: url
		});
	}
	//ok
	$scope.deleteDependenciesFromFeature = function (featureId, featureIdsToRemove){

		var url =  baseURL + '/features/'+ featureId + '/dependencies';
		for(var i=0 ; i< featureIdsToRemove.length; i++){
			if(i == 0){
				url = url + "?feature_id=" + featureIdsToRemove[i];
			}
			else{
				url = url + "," + featureIdsToRemove[i];
			}
		}  

		return $http({
			method: 'DELETE',
			url: url
		});
	};

	$scope.addDependenciesToFeature = function (featureId, featureIdsToAdd){

		//create data OBj like {"_json": [{"feature_id": 1}]}
		var arr = [];
		for(var i = 0 ; i< featureIdsToAdd.length; i++){
			var resourceIdObj = {};
			resourceIdObj["feature_id"] = featureIdsToAdd[i];
			arr.push(resourceIdObj);
		}

		var dataObj = {};
		dataObj["_json"] = arr;

		return $http({
			method: 'POST',
			url: baseURL + '/features/'+ featureId + '/dependencies',
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: dataObj
		});	
	};
	
	
	$scope.deleteSkillsFromFeature = function (featureId, skillIdsToRemove){

		var url =  baseURL + '/features/'+ featureId + '/skills';
		for(var i=0 ; i< skillIdsToRemove.length; i++){
			if(i == 0){
				url = url + "?skill_id[]=" + skillIdsToRemove[i];
			}
			else{
				url = url + "&skill_id[]=" + skillIdsToRemove[i];
			}
		}  

		return $http({
			method: 'DELETE',
			url: url
		});
	};
	
	$scope.addSkillsToFeature = function (featureId, skillIdsToAdd){

		var arr = [];
		for(var i = 0 ; i< skillIdsToAdd.length; i++){
			var skillIdObj = {};
			skillIdObj["skill_id"] = skillIdsToAdd[i];
			arr.push(skillIdObj);
		}

		var dataObj = {};
		dataObj["_json"] = arr;

		return $http({
			method: 'POST',
			url: baseURL + '/features/'+ featureId + '/skills',
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: dataObj
		});	
	};
	
	
	
	
	//ok
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

	$scope.getProjectSkills = function(){

		return $http({
			method: 'GET',
			url: baseURL + '/skills'
		});
	};

	/*
	 * All methods
	 */
	$scope.showFeature = false;
	$scope.messageFeature = "Loading ...";
	$scope.feature = {code: -1, deadline:"", description:"", effort:"", id: -1, name:"", priority: -1, release:{}};

	$scope.release = {};
	//contains array of feature object.
	$scope.releaseFeatures = [];
	$scope.skills = [];

	/*
	 * Dependencies
	 */

	$scope.dependencies = [];
	// prepare the data
	var sourceDependencyDropDownList =
	{
			datatype: "json",
			datafields: [
			             { name: 'id' },
			             { name: 'name' }
			             ],
			             id: 'id',
			             localdata: $scope.dependencies
	};
	$scope.dataAdapterDependencyDropDownList = new $.jqx.dataAdapter(sourceDependencyDropDownList);

	//settings
	$scope.dropDownListSettingsDependency = { 
			renderer: function (index, label, value) 
			{
			    var datarecord = $scope.dependencies[index];
			    return datarecord.name + "-" + datarecord.id +"(Id)";
			},
			//displayMember: "name",
			valueMember: "id",
			checkboxes: true,
			enableSelection: true,
			width: '93%',
			height: '15',
			source: $scope.dataAdapterDependencyDropDownList
	};

	$("#dependency").on('bindingComplete', function (event) { 
		//select all items in skillDropDownListId
		var items = $("#dependency").jqxDropDownList('getItems');
		for(var i = 0; i< items.length; i++){
			var item = items[i];
			for (var y = 0; y < $scope.feature.depends_on.length; y++) {
				var dependency = $scope.feature.depends_on[y];
				if(dependency.id == item.value){
					$("#dependency").jqxDropDownList('checkIndex', i); 
				}
			}
		}

	});


	/*
	 * Skills 
	 */
	var emptyArray = [];
	// prepare the data
	var sourceSkillDropDownList =
	{
			datatype: "json",
			datafields: [
			             { name: 'id' },
			             { name: 'name' }
			             ],
			             id: 'id',
			             localdata: emptyArray
	};
	$scope.dataAdapterSkillDropDownList = new $.jqx.dataAdapter(sourceSkillDropDownList);

	//settings
	$scope.dropDownListSettingsSkill = { 

			displayMember: "name",
			valueMember: "id",
			checkboxes: true,
			//enableSelection: false,
			width: '93%',
			height: '15',
			source: $scope.dataAdapterSkillDropDownList
	};

	$("#skill").on('bindingComplete', function (event) { 
		//select all items in skillDropDownListId
		var items = $("#skill").jqxDropDownList('getItems');
		for(var i = 0; i< items.length; i++){
			var item = items[i];
			for (var y = 0; y < $scope.feature.required_skills.length; y++) {
				var skill = $scope.feature.required_skills[y];
				if(skill.id == item.value){
					$("#skill").jqxDropDownList('checkIndex', i); 
				}
			}
		}

	});
	
    $("#skill").on('checkChange', function (event){
    	var items = $("#skill").jqxDropDownList('getCheckedItems'); 
        if(items.length == 0){
        	$scope.skillRequired = 'has-error';
        	$scope.skillRequiredBln = true;
        	$scope.featureForm.$invalid = true;
        }else{
        	$scope.skillRequired = '';
        	$scope.skillRequiredBln = false;
        	$scope.featureForm.$invalid = false;
        }
    		
	});
    

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
								if($scope.feature.deadline == null){
									$scope.feature.deadline = getStringSUPERSEDEDateNow();
								}
									
								//data input
								$("#dateInputDeadline").jqxDateTimeInput({ width: '100%', height: '25px', formatString: 'yyyy-MM-dd'/*, min: new Date(year, month, day)*/});
								$('#dateInputDeadline').jqxDateTimeInput('setDate', new Date($scope.feature.deadline));

								//dependencies
								$scope.getReleaseFeatures($scope.release.id)
								.then(
										function(response) {

											$scope.releaseFeatures = response.data;
											$scope.dependencies = [];
											for (var y = 0; y < $scope.releaseFeatures.length; y++) {
												if($scope.releaseFeatures[y].id !=$location.search().featureId ){
													$scope.dependencies.push($scope.releaseFeatures[y]);
												}
											}

											// prepare the data
											var sourceDependencyDropDownList =
											{
													datatype: "json",
													datafields: [
													             { name: 'id' },
													             { name: 'name' }
													             ],
													             id: 'id',
													             localdata: $scope.dependencies
											};

											$scope.dataAdapterDependencyDropDownList = new $.jqx.dataAdapter(sourceDependencyDropDownList,{
												loadComplete: function () {
													var items = $scope.dataAdapterDependencyDropDownList.records;

												}
											});



											$scope.getProjectSkills()
											.then(
													function(response) {
														$scope.showFeature = true;

														$scope.skills = response.data;

														// prepare the data
														var sourceSkillDropDownList =
														{
																datatype: "json",
																datafields: [
																             { name: 'id' },
																             { name: 'name' }
																             ],
																             id: 'id',
																             localdata: $scope.skills
														};
														$scope.dataAdapterSkillDropDownList = new $.jqx.dataAdapter(sourceSkillDropDownList);
													},

													function(response) {

														$scope.showFeature = false;
														$scope.messageFeature = "Error: "+response.status + " " + response.statusText;
													}
											);
										},
										function(response) {
											$scope.showFeature = false;
											$scope.messageFeature = "Error: "+response.status + " " + response.statusText;
										}
								);
							},
							function(response) {
								$scope.showFeature = false;
								$scope.messageFeature = "Error: "+response.status + " " + response.statusText;
							}
					);

				},

				function(response) {
					$scope.showFeature = false;
					$scope.messageFeature = "Error: "+response.status + " " + response.statusText;
				}
		);
	}

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
			$("#successOk").one('click', function (){
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
			$scope.update = false;
			$("#delayOk").one('click', function (){

				//console.log("update " + new Date());
				$("#delayWindow").jqxWindow('close');
				//appendReplaneWindows("success", "Re-plan success.");
	
				//1.update feature
				//2.remove all dependencies
				//3.add dependencies
				//4.add features to release	

				//update feature only name, description, effort, deadline, priority
				$scope.updateFeature($scope.feature)
				.then(
						function(response) {
							addRemoveSkills();
						},
						function(response) {
							appendReplaneWindows("default", "Error: "+response.status + " " + response.statusText);
						}
				);
			

			});
			$("#delayCancel").jqxInput({ height: 25});
			$("#delayCancel").one('click', function (){
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
			$("#omittedOk").one('click', function (){
				$("#omittedWindow").jqxWindow('close');
				//1.update feature
				//2.remove all dependencies
				//3.add dependencies
				//4.add features to release	

				//update feature only name, description, effort, deadline, priority
				$scope.updateFeature($scope.feature)
				.then(
						function(response) {
							addRemoveSkills();
						
						},
						function(response) {
							appendReplaneWindows("default", "Error: "+response.status + " " + response.statusText);
						}
				);
			});
			$("#omittedCancel").jqxInput({ height: 25});
			$("#omittedCancel").one('click', function (){
				$("#omittedWindow").jqxWindow('close');
			});
			var omittedWindow = $('#omittedWindow');
			var heightomittedWindow = omittedWindow.height();

			$("#omittedWindow").jqxWindow({ height: 'auto', width: 400, position: { x: offset.left + (width/2)-150, y: offset.top + (height/2) - heightomittedWindow}, isModal: true, modalOpacity: 0.3, showCloseButton: false });
			$("#omittedWindow").jqxWindow('open');

			break;

			// only for message
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
		$("#defaultCancel").one('click', function (){
			$("#defaultWindow").jqxWindow('close');
		});
		var defaultWindow = $('#defaultWindow');
		var heightdefaultWindow = defaultWindow.height();

		$("#defaultWindow").jqxWindow({ height: 'auto', width: 400, position: { x: offset.left + (width/2)-150, y: offset.top + (height/2) - heightdefaultWindow}, isModal: true, modalOpacity: 0.3, showCloseButton: false });
		$("#defaultWindow").jqxWindow('open');

		break;
		}

	}

	$scope.arrayFeatureDepend_on_to_add = [];
	$scope.arrayFeatureDepend_on_to_remove = [];
	
	$scope.arraySkill_to_add = [];
	$scope.arraySkill_to_remove = [];
	

	$scope.replanRelease = function(){

		
		//1. fill the Depend_ons to add and the Depend_on to remove
		$scope.arrayFeatureDepend_on_to_add = []; 
		var items = $("#dependency").jqxDropDownList('getCheckedItems');
		for(var i = 0; i< items.length ; i++){
			$scope.arrayFeatureDepend_on_to_add[i] = items[i].value;
		}
		$scope.arrayFeatureDepend_on_to_remove = []; 
		var items = $("#dependency").jqxDropDownList('getItems');
		for(var i = 0; i< items.length ; i++){
			$scope.arrayFeatureDepend_on_to_remove[i] = items[i].value;
		}
		
		$scope.arraySkill_to_add = [];
		var items = $("#skill").jqxDropDownList('getCheckedItems');
		for(var i = 0; i< items.length ; i++){
			$scope.arraySkill_to_add[i] = items[i].value;
		}
		$scope.arraySkill_to_remove = []; 
		var items = $("#skill").jqxDropDownList('getItems');
		for(var i = 0; i< items.length ; i++){
			$scope.arraySkill_to_remove[i] = items[i].value;
		}
		
		//2. update the deadline in feature
		var date = $("#dateInputDeadline").jqxDateTimeInput('getDate');
		var strDate = getStringSUPERSEDEDate(date.getTime());
		$scope.feature.deadline = strDate;

		//3.check priority parameters
		if(!($scope.feature.priority >=0 && $scope.feature.priority <=5)){
			appendReplaneWindows("default", "The feature priority must be between 1 and 5." );
		}
		else if(new Date($scope.feature.deadline) >= new Date($scope.release.deadline)){
			appendReplaneWindows("delay", "The release will be delayed if this feature is included." );
		}
		// some_variable is either null, undefined, 0, NaN, false, or an empty string
		else if(!$scope.feature.deadline){
			appendReplaneWindows("omitted", "The release will be on time but the feature deadline has been omitted.");
		}
		else{
			//1.update feature
			//2.remove all dependencies
			//3.add dependencies
			//4.add features to release	

			//update feature only name, description, effort, deadline, priority
			$scope.updateFeature($scope.feature)
			.then(
					function(response) {
						addRemoveSkills();
					},
					function(response) {
						appendReplaneWindows("default", "Error: "+response.status + " " + response.statusText);
					}
			);
		}
	}

	function addRemoveSkills(){
		
		/*
		 *  remove all skills
		 *	add skills
		 *	call internalUpdate
		 *
		 */
		$scope.deleteSkillsFromFeature($scope.feature.id, $scope.arraySkill_to_remove)
		.then(
				function(response) {
					if($scope.arraySkill_to_add.length >0){
						
						$scope.addSkillsToFeature($scope.feature.id, $scope.arraySkill_to_add)
						.then(
								function(response) {
									internalUpdate();
								},
								function(response) {
									appendReplaneWindows("default", "Error: "+response.status + " " + response.statusText);
								}
						);
					}else{
						internalUpdate();
					}
				
				},
				function(response) {
					appendReplaneWindows("default", "Error: "+response.status + " " + response.statusText);
				}
		);
		
	}
	
	function internalUpdate(){
		
		if($scope.arrayFeatureDepend_on_to_remove.length >0){
			
			/*
			 *  remove all dependencies
			 *	add dependencies
			 *	add feature to release
			 *
			 */
			
			//remove all dependencies
			$scope.deleteDependenciesFromFeature($scope.feature.id, $scope.arrayFeatureDepend_on_to_remove)
			.then(
					function(response) {
						//add dependencies
						if($scope.arrayFeatureDepend_on_to_add.length >0){
							$scope.addDependenciesToFeature($scope.feature.id, $scope.arrayFeatureDepend_on_to_add)
							.then(
									function(response) {
										//add feature to release
										$scope.addFeatureToRelease($scope.release.id, $scope.feature.id)
										.then(
												function(response) {
													//appendReplaneWindows("success", "Re-plan success.");
													$location.path("/release-planner-app/main");
												},
												function(response) {
													appendReplaneWindows("default", "Error: "+response.status + " " + response.statusText);
												}
										);
									},
									function(response) {
										appendReplaneWindows("default", "Error: "+response.status + " " + response.statusText);
									}
							);
						}else{
							//add feature to release
							$scope.addFeatureToRelease($scope.release.id, $scope.feature.id)
							.then(
									function(response) {
										//appendReplaneWindows("success", "Re-plan success.");
										$location.path("/release-planner-app/main");
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
		
		/*
		 *  
		 *	add dependencies
		 *	add feature to release
		 *
		 */
		//add dependencies
		else if($scope.arrayFeatureDepend_on_to_add.length >0){
				$scope.addDependenciesToFeature($scope.feature.id, $scope.arrayFeatureDepend_on_to_add)
				.then(
						function(response) {
							//add feature to release
							$scope.addFeatureToRelease($scope.release.id, $scope.feature.id)
							.then(
									function(response) {
										//appendReplaneWindows("success", "Re-plan success.");
										$location.path("/release-planner-app/main");
									},
									function(response) {
										appendReplaneWindows("default", "Error: "+response.status + " " + response.statusText);
									}
							);
						},
						function(response) {
							appendReplaneWindows("default", "Error: "+response.status + " " + response.statusText);
						}
				);
			}
			else{
				/*
				 *  
				 *	
				 *	add feature to release
				 *
				 */
				//add feature to release
				$scope.addFeatureToRelease($scope.release.id, $scope.feature.id)
				.then(
						function(response) {
							//appendReplaneWindows("success", "Re-plan success.");
							$location.path("/release-planner-app/main");
						},
						function(response) {
							appendReplaneWindows("default", "Error: "+response.status + " " + response.statusText);
						}
				);
			}
	
	}
	
	function getStringSUPERSEDEDate(time){

		var date = new Date(time);

		var month = date.getMonth() + 1;
		var day = date.getDate();

		var strMonth;
		if(month <= 9){
			strMonth ="0" + month;
		}else{
			strMonth = ""+month;
		}

		var strDay;
		if(day <= 9){
			strDay ="0" + day;
		}else{
			strDay = ""+day;
		}

		var strDate =	date.getFullYear() + "-" + strMonth + "-" + strDay;
		return strDate;
	}
	
	function getStringSUPERSEDEDateNow(){

		var date = new Date();

		var month = date.getMonth() + 1;
		var day = date.getDate();

		var strMonth;
		if(month <= 9){
			strMonth ="0" + month;
		}else{
			strMonth = ""+month;
		}

		var strDay;
		if(day <= 9){
			strDay ="0" + day;
		}else{
			strDay = ""+day;
		}

		var strDate =	date.getFullYear() + "-" + strMonth + "-" + strDay;
		return strDate;
	}
	


	/**
	 * start point method
	 */
	$scope.getRelease($location.search().releaseId);

}]);