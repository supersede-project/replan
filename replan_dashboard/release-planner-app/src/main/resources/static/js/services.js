var app = angular.module('w5app');

//example urls
////http://localhost:8083/projects/1/releases
////$http.get('release-planner-app/projects/'+ $scope.project_id +'/releases')
////$http.get('http://localhost:90/release-planner-app/projects/'+ $scope.project_id +'/releases')
//$http.get('http://localhost:3000/api/ui/v1/projects/1/releases')
//penso che questo sia quello giusto
//url: 'http://localhost:90/release-planner-app/projects/1/features?status=pending'
app.constant("myConfig", {
	"baseURL": "http://localhost:3000/api/ui/v1/projects/1",
	"projects_id": "1"
})
.service('releasePlannerFactory', ['$http', 'myConfig', '$localStorage', function($http, myConfig, $localStorage) {

	/**
	 * FEATURES
	 */
//	/*
//	 * SELECTED features methods
//	 */
//	var feature_id = -1;
//
//	this.setSelectedFeature_id = function(id){
//		feature_id = id;
//		$localStorage.feature_id = id;
//	};
//
//	this.getSelectedFeature = function(){
//
//		if(feature_id == -1){
//			feature_id = $localStorage.feature_id;
//		}
//		return $http({
//			method: 'GET',
//			url: myConfig.baseURL + '/features/'+ feature_id
//		});
//	};
	
//	/*
//	 * 
//	 */
//	this.getPendingFeature = function(){
//
//		return $http({
//			method: 'GET',
//			url: myConfig.baseURL + '/features?status=pending'
//		});
//	};

//	//Modifies a given feature
//	this.updateFeature = function (feature){
//
//		return $http({
//			method: 'PUT',
//			url: myConfig.baseURL + '/features/'+ feature.id,
//			headers: {"Content-Type": "application/json;charset=UTF-8"},
//			data: feature
//		});	 
//	};


	/**
	 * RELEASES
	 */
	
	/*
	 * SELECTED release methods
	 */
//	var release = {};
//
//	this.setSelectedRelease = function(release){
//		this.release = release;
//		//$localStorage.release_id = id;
//	};
//
//	this.getSelectedRelease = function(){
//		return this.release;  	 
//	};
	
//	/*
//	 * 
//	 */
//	this.getReleases = function(){
//		return $http({
//			method: 'GET',
//			url: myConfig.baseURL + '/releases'
//		});
//	}; 


//	//add feature to release
//	this.addFeatureToRelease = function (releaseId, featureId){
//		
//		//create data OBj like {"_json": [{"feature_id": 3}]}
//		
//		var featureIdObj = {};
//		featureIdObj["feature_id"] = featureId;
//		
//		var arr = [];
//		arr.push(featureIdObj);
//		
//		var dataObj = {};
//		dataObj["_json"] = arr;
//		
//		return $http({
//			method: 'POST',
//			url: myConfig.baseURL + '/releases/'+ releaseId + '/features',
//			headers: {"Content-Type": "application/json;charset=UTF-8"},
//			data: dataObj
//		});	 
//
//	};

//	//remove feature to release
//	this.removeFeatureFromRelease = function (releaseId, featureIds){
//
//		var url =  myConfig.baseURL + '/releases/'+ releaseId + '/features';
//		for(var i=0 ; i< featureIds.length; i++){
//			if(i == 0){
//				url = url + "?featureId[" + i + "]=" + featureIds[i];
//			}
//			else{
//				url = url + "&featureId[" + i + "]=" + featureIds[i];
//			}
//		}  
//
//
//		return $http({
//			method: 'DELETE',
//			url: url
//		});	 
//
//	};

//	//get release Plan
//	this.getReleasePlan = function(releaseId){
//		var url = myConfig.baseURL + '/releases/' + releaseId + '/plan';
//		return $http({
//			method: 'GET',
//			url: url
//		});
//	}; 

//	//Modifies a given release (modify only name, description and deadline )
//	this.updateRelease = function (release){
//
//		return $http({
//			method: 'PUT',
//			url: myConfig.baseURL + '/releases/'+ release.id,
//			headers: {"Content-Type": "application/json;charset=UTF-8"},
//			data: release
//		});	 
//	};
	
//	this.deleteResoucesFromRelease = function (releaseId, resourceIds){
//
//		var url =  myConfig.baseURL + '/releases/'+ releaseId + '/resources';
//
//		for(var i=0 ; i< resourceIds.length; i++){
//			
//			if(i == 0){
//				url = url + "?ResourceId[" + i + "]=" + resourceIds[i];
//			}
//			else{
//				url = url + "&ResourceId[" + i + "]=" + resourceIds[i];
//			}
//		}  
//
//		return $http({
//			method: 'DELETE',
//			url: url
//		}); 
//	};
	
//	this.addResoucesToRelease = function (releaseId, resourceIds){
//	    
//		//create data OBj like {"_json": [{"resource_id": 3}]}
//		var arr = [];
//		for(var i = 0 ; i< resourceIds.length; i++){
//			var resourceIdObj = {};
//			resourceIdObj["resource_id"] = resourceIds[i];
//			arr.push(featureIdObj);
//		}
//	
//		var dataObj = {};
//		dataObj["_json"] = arr;
//		
//		return $http({
//			method: 'POST',
//			url: myConfig.baseURL + '/releases/'+ releaseId + '/resources',
//			headers: {"Content-Type": "application/json;charset=UTF-8"},
//			data: dataObj
//		});	 
//	
//	};
	
	/**
	 * PROJECTS
	 */
	this.getProject = function(){

		return $http({
			method: 'GET',
			url: myConfig.baseURL
		});
	};
	
	//Modifies a given project (modify only effort_unit, hours_per_effort_unit and hours_per_week_and_full_time_resource )
	this.updateProject = function (project){

		return $http({
			method: 'PUT',
			url: myConfig.baseURL,
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: project
		});	 
	};
	
//	this.getProjectResources = function(){
//    
//		return $http({
//			method: 'GET',
//			url: myConfig.baseURL + '/resources'
//		});
//	};
	

}])