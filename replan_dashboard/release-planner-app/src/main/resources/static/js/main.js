var app = angular.module('w5app');
app.controllerProvider.register('main-utilities', ['$scope', '$location', '$http',
                                  function ($scope, $location, $http) {
	/*
	 * REST methods
	 */
	var baseURL = "release-planner-app/replan/projects/tenant";

	$scope.getReleaseFeatures = function (releaseId) {
		return $http({
			method: 'GET',
			url: baseURL + '/releases/'+ releaseId +'/features'
		});
	}

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

				var datarecord = $scope.features[index];
				var glyphicon = '<span class="glyphicon glyphicon-wrench" style="font-size:15px;"></span>';
				var table = '<table style="color: inherit; font-size: inherit; font-style: inherit;"><tr><td style="width: 35px;" rowspan="3">' + glyphicon + '</td><td><b>' + label  + '</b></td></tr> <tr><td>Id:'+ datarecord.id +' Effort: '+ datarecord.effort +' Priority:'+ datarecord.priority + '</td></tr></table>';
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
			var idRelease = $scope.releases[i].id;
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

					$location.path("/release-planner-app/replan_release").search({featureId: ''+idFeature, releaseId: '' + idRelease });
					//$location.path("/release-planner-app/replan_release");
					break;
				}
			}
		}
	}

	/**
	 * start point feature method
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

	$scope.releaseFeatures = [];



	/**
	 * start point release method
	 */
	$scope.getReleases()
	.then(
			function(response) {

				$scope.showReleases = true;
				$scope.releases.length = 0;
				for(var i = 0; i < response.data.length; i++)
				{
					response.data[i]["count"] = 0;
					$scope.releases.push( response.data[i] ); 

				}
				$scope.releases.length = response.data.length;

				for (var i = 0; i < $scope.releases.length; i++) {

					var release = $scope.releases[i];

					$scope.getReleaseFeatures(release.id)
					.then(
							function(response1) {

								if(response1.data.length > 0){

									var releaseID = response1.data[0].release.release_id;

									// prepare the data
									var sourcefeatureDropDownList =
									{
											datatype: "json",
											datafields: [
											             { name: 'id' },
											             { name: 'name' }
											             ],
											             id: 'id',
											             localdata: response1.data
									};

									var dataAdapterFeaturesDropDownList = new $.jqx.dataAdapter(sourcefeatureDropDownList);


									var id = "featureDropDownId" + releaseID;

									$("#"+id).jqxDropDownList({
										source: dataAdapterFeaturesDropDownList,
										displayMember: "name",
										valueMember: "id",
										//checkboxes: true,
										enableSelection: false,
										selectedIndex: 0,
										width: '93%',
										height: '15'
									});

									var idbadge = "badgeId" + releaseID;



									var badge =  document.getElementById(idbadge);
									badge.innerHTML ='';
									badge.innerHTML =''+response1.data.length;
								}
							},

							function(response1) {

							}
					);
				}

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