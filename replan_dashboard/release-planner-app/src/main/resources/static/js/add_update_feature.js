var app = angular.module('w5app');
app.controllerProvider.register('add-update-feature', ['$scope', '$location', '$http', 
                                      function ($scope, $location, $http) {

	/*
	 * REST methods
	 */

	var baseURL = "release-planner-app/replan/projects/tenant";
	var baseURLWitoutTenant = "release-planner-app/replan";

	$scope.getFeature = function(featureId){

		return $http({
			method: 'GET',
			url: baseURL + '/features/'+ featureId
		});
	};

	//Add a new feature
	$scope.addFeature = function (feature){

		return $http({
			method: 'POST',
			url: baseURL + '/features/create_one/',
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: feature
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

	/*
	 * All methods
	 */
	$scope.showFeature = true;
	$scope.messageFeature = "Loading ...";
	$scope.feature = {priority: 1, effort: 1};
	//$scope.feature = {code: -1, deadline:"", description:"", effort:-1, id: -1, name:"", priority: -1, release:{}};

	$scope.skills = [];
	$scope.isAddFeature = true;
	$scope.addUpdateFeatureLabel = "Add";


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


	/*
	 * Priority(1-5) list
	 */
	$("#priorityDropDownList").on('bindingComplete', function (event) { 
		//select all items in skillDropDownListId
		var items = $("#priorityDropDownList").jqxDropDownList('getItems');
		for(var i = 0; i< items.length; i++){
			var item = items[i];
			for (var y = 0; y < $scope.priorityArray.length; y++) {
				var priority = $scope.priorityArray[y];
				if(priority == item.value){
					$("#priorityDropDownList").jqxDropDownList('selectIndex', y ); 
				}
			}
		}
	});

	$("#priorityDropDownList").on('change', function (event){
		var item = $("#priorityDropDownList").jqxDropDownList('getSelectedItem'); 
		$scope.feature.priority = Number(item.value);
	});


	$scope.addUpdateFeature = function(){

		//$scope.feature = {code: -1, deadline:"", description:"", effort:-1, id: -1, name:"", priority: -1, release:{}};
		
		//update the deadline in feature
		var date = $("#dateInputDeadline").jqxDateTimeInput('getDate');
		var strDate = getStringSUPERSEDEDate(date.getTime());
		$scope.feature.deadline = strDate;
		
		//add
		if($scope.isAddFeature){
			//add feature to release
			$scope.addFeature($scope.feature)
			.then(
					function(response) {
						$location.path("/release-planner-app/main");
					},
					function(response) {
						appendAddUpdateFeatureWindows(/*"Error: "+response.data.code + " " + */response.data.message);
					}
			);
			//alert("add: "+ $scope.feature);
		}
		//update	
		else{
			
			//update feature
			$scope.updateFeature($scope.feature)
			.then(
					function(response) {
						$location.path("/release-planner-app/main");
					},
					function(response) {
						appendAddUpdateFeatureWindows(/*"Error: "+response.data.code + " " + */response.data.message);
					}
			);
			
			//alert("update");
		}
	}


	var randomFixedInteger = function (length) {
		return Math.floor(Math.pow(10, length-1) + Math.random() * (Math.pow(10, length) - Math.pow(10, length-1) - 1));
	}


	appendAddUpdateFeatureWindows = function(message) {
		var featureForm = $('#featureForm');
		var offset = featureForm.offset();
		var width = featureForm.width();
		var height = featureForm.height();

		var checkDefaultWindow = document.getElementById("defaultWindow");
		if(checkDefaultWindow == null){
			$(document.body).append('<div id="defaultWindow">'+
					'<div>Add Update Status</div>'+
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
	}

	$scope.oninputFunction = function (){

		if ($scope.feature.code.toString().length > 4) {
			var newCode = Number($scope.feature.code.toString().slice(0,4)); 
			$scope.feature.code = newCode;
		}
	}


	/**
	 * start point method
	 */
	//priority list
	$scope.priorityArray = ["1","2","3","4","5"];

	$scope.startPoint = function(){
		//general
		$scope.isAddFeature = true;
		$scope.addUpdateFeatureLabel = "Add";
		
		//settings priority dropDownList
		$scope.dropDownListSettingsPriority = { 
				width: '93%',
				height: '15',
				selectedIndex: 0,
				source: $scope.priorityArray
		};
		
		//data input
		$("#dateInputDeadline").jqxDateTimeInput({ width: '100%', height: '25px', formatString: 'yyyy-MM-dd'/*, min: new Date(year, month, day)*/});
		$('#dateInputDeadline').jqxDateTimeInput('setDate', new Date(getStringSUPERSEDEDateNow()));
	
		//the form is update feature
		var booleanHasfeatureId= $location.search().hasOwnProperty('featureId');
	    var featureID = $location.search().featureId;
		if(booleanHasfeatureId){

			$scope.isAddFeature = false;
			$scope.addUpdateFeatureLabel = "Update";

			$scope.getFeature($location.search().featureId)
			.then(
					function(response) {
//						var feature = response.data;
//						var effortAsNumber = Number(response.data.effort);
//						response.data.effort = effortAsNumber;
						$scope.feature =  response.data;
						
						//data input
						$("#dateInputDeadline").jqxDateTimeInput({ width: '100%', height: '25px', formatString: 'yyyy-MM-dd'/*, min: new Date(year, month, day)*/});
						$('#dateInputDeadline').jqxDateTimeInput('setDate', new Date($scope.feature.deadline));
						
						
					},
					function(response) {
						$scope.showFeature = false;
						$scope.messageFeature ="An error is occurred, please contanct your administrator";


					}
			);
		}
	}

	$scope.startPoint();

}]);