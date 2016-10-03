var app = angular.module('w5app');
app.controllerProvider.register('main-utilities', ['$scope', '$location', '$http',
                            function ($scope, $location, $http) {
	/*
 	* REST methods
 	*/
	
	var baseURL = "http://62.14.219.13:8280/replan/projects/1";
	//var baseURL = "http://localhost:3000/api/ui/v1/projects/1";
	
 	$scope.getPendingFeature = function () {
 		return $http({
			method: 'GET',
			url: baseURL + '/features?status=pending'
		});
 	}
 	
 	$scope.getReleases = function () {
 		return $http({
			method: 'GET',
			url: baseURL + '/releases'
		});
 	}
 	
	/*
 	* FEATURES
 	*/

	$scope.showFeatures = false;
    $scope.messageFeatures = "Loading ...";
 	$scope.features = [];
 	$scope.featuresLength = 0;

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
			             localdata: $scope.features
	};

	$scope.dataAdapter = new $.jqx.dataAdapter(source);
	
	//settings
	$scope.settings = { 
			source: $scope.dataAdapter,
			valueMember: "id",
			displayMember: "name", 
			width: '100%',
			height: '400',
			allowDrag: true,
			renderer: function (index, label, value) {

				//var img = '<span class="glyphicon glyphicon-wrench" style="font-size:30px;color:red;"></span>';
				var glyphicon = '<span class="glyphicon glyphicon-wrench" style="font-size:15px;"></span>';

				var table = '<table style="color: inherit; font-size: inherit; font-style: inherit;"><tr><td style="width: 35px;">' + glyphicon + '</td><td>' + label  + '-' + value +  '</td></tr></table>';
				return table;
			}

	};

	$scope.selectItem = function (event) {
		if (event.args) {
			var item = event.args.item;
			if (item) {
				$scope.log =  "Label: " + item.label + " Value: " + item.value + " (index)";
			}
		}
	};

	$("#featureListBox").on('dragEnd', function (event) {
		if (event.args.label) {
			var ev = event.args.originalEvent;
			var x = ev.pageX;
			var y = ev.pageY;
			if (event.args.originalEvent && event.args.originalEvent.originalEvent && event.args.originalEvent.originalEvent.touches) {
				var touch = event.args.originalEvent.originalEvent.changedTouches[0];
				x = touch.pageX;
				y = touch.pageY;
			}
			$scope.addToRelease(event.args.value, x, y);
		}
	});

	$scope.addToRelease = function(idFeature, x, y ){
		
		for(var i = 0; i < $scope.releases.length; i++)
		{
			var id = "#release_" + $scope.releases[i].id;
			var offset = $(id).offset();
			var width = $(id).width();
			var height = $(id).height();
			var right = parseInt(offset.left) + width;
			var bottom = parseInt(offset.top) + height;
			if (x >= parseInt(offset.left) && x <= right) {
				if (y >= parseInt(offset.top) && y <= bottom) {
					//add to release
					$scope.releases[i].count ++;
					
					//$scope.$apply(); 
					//remove from listBox
					
					//get index to remove
					var index = -1;
					for(var i = 0; i < $scope.features.length; i++)
					{
						if($scope.features[i].id == idFeature){
							index = i;
							break;
						}
					}
					
					//remove index
					if(index != -1){
						$scope.features.splice(index, 1);
						$scope.featuresLength.length = $scope.featuresLength.length -1;
						
						var source =
						{
								datatype: "json",
								datafields: [
								             { name: 'id' },
								             { name: 'name' }
								             ],
								             id: 'id',
								             localdata: $scope.features
						};

						$scope.dataAdapter = new $.jqx.dataAdapter(source);
					}
					 $location.path("/release-planner-app/replan_release").search({featureId: ''+idFeature, releaseId: ''+$scope.releases[i].id });
					 //$location.path("/release-planner-app/replan_release");
				}
			}
		}
	}
	
	/**
	 * start point method
	 */
	$scope.getPendingFeature()
    .then(
	    function(response) {
	    	
	    	var data = response.data;
	    	$scope.showFeatures = true;
			$scope.featuresLength.length = 0;
			for(var i = 0; i < data.length; i++)
			{
				$scope.features.push(data[i]);
			}
			$scope.featuresLength = data.length;
			
			var source =
			{
				datatype: "json",
				datafields: [
				             { name: 'id' },
				             { name: 'name' }
				             ],
				             id: 'id',
				             localdata: $scope.features
			};
	
			$scope.dataAdapter = new $.jqx.dataAdapter(source);
	    },
	    
        function(response) {
        	$scope.showFeatures = false;
            $scope.messageFeatures = "Error: "+response.status + " " + response.statusText;	
        }
    );
	
 	/*
 	* RELEASES
 	*/

 	$scope.showReleases = false;
    $scope.messageReleases = "Loading ...";
 	$scope.releases = [];
 	$scope.releasesLength = 0;
 	
 	$scope.showReleaseDetails = function (release) {
        $location.path("/release-planner-app/release_details").search({releaseId: ''+release.id});
    }
 	
 	$scope.showRelease = function (release) {
        $location.path("/release-planner-app/release").search({releaseId: ''+release.id});
    }
 	
 	/**
	 * start point method
	 */
 	$scope.getReleases()
    .then(
        function(response) {
        		
        	    $scope.showReleases = true;
            	$scope.releases.length =0;
        	    for(var i = 0; i < response.data.length; i++)
     			{
     				response.data[i]["count"] = 0;
     				$scope.releases.push(response.data[i]);
     				//console.log(data[i].count);
     			}
                $scope.releases.length = response.data.length;
     			
   
        },
        
        function(response) {
        	$scope.showReleases = false;
            $scope.messageReleases = "Error: "+response.status + " " + response.statusText;
        }
    );
 	
 	/*
 	* PROJECT
 	*/
 	$scope.showEditProjectConfig = function () {
        $location.path("/release-planner-app/project");
    }
	 
}]);