var app = angular.module('w5app');
app.controllerProvider.register('project-utilities', ['$scope', '$location', '$http',
                                     function ($scope, $location, $http) {
	/*
 	* REST methods
 	*/
	var baseURL = "http://localhost:3000/api/ui/v1/projects/1";
	
	$scope.getProject = function(){

		return $http({
			method: 'GET',
			url: baseURL
		});
	};
	
	//Modifies a given project (modify only effort_unit, hours_per_effort_unit and hours_per_week_and_full_time_resource )
	$scope.updateProject = function (project){

		return $http({
			method: 'PUT',
			url: baseURL,
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: project
		});	 
	};
	
	/*
 	* All methods
 	*/
	$scope.showProject = false;
	$scope.messageProject = "Loading ...";
	$scope.project = {resources: []};
	$scope.typeLabel = "Modify";
	$scope.update = true;

	$scope.topics = [
	                 "DV – (Generic) Development",
	                 "SE – Semantics", 	
	                 "DVF – Development Frontend",
	                 "WT – Web Toolkits",
	                 "DVB – Development Backend",
	                 "DA – Data Analytics",
	                 "DVT – Development Testing",
	                 "DB – Databases",
	                 "VI – Visualization",
	                 "SC – Security",
	                 ];

	$scope.update = function(){
		$scope.updateProject($scope.project)
	    .then(
	        function(response) {
	        	$location.path("/release-planner-app/main");
	        },
	        
	        function(response) {
	        	$scope.showReleases = false;
	            $scope.messageReleases = "Error: "+response.status + " " + response.statusText;
	        }
	    );
	};

	$scope.cancel = function(){
		$location.path("/release-planner-app/main");
	};
	
	$scope.add = function(){
		
	}
	/**
	 * start point method
	 */
	$scope.getProject()
	.then(
			function(response) {

				$scope.project = response.data;
				$scope.showProject = true;
				
				// Create a jqxGrid
			 	// prepare the data for jqxListBox
				var source =
				{
						localdata: $scope.project.resources,
						datatype: "array",
						datafields:
							[
							 { name: 'id', type: 'number' },
							 { name: 'availability', type: 'string' }
							 ]
				};

				var dataAdapter = new $.jqx.dataAdapter(source);

				
				$("#projectJqxgrid").jqxGrid({
					width: '100%',
					autoheight: true,
					source: dataAdapter,
					columns: [{
						text: 'Topics',
						align: 'center',
						dataField: 'id',
						width: '80%',
						'editable': true,
						cellsRenderer: function (row, column, value) {
							var rowData = $scope.project.resources[row];
							var container = '<div style="width: 100%; height: 100%;">'
								var leftcolumn = '<div style="float: left; width: 30%;">';
							var name = "<div style='margin: 10px;'><b>Name:</b> " + rowData.name + "</div>";
							leftcolumn += name;
							leftcolumn += "</div>";

							var rightcolumn = '<div style="float: left; width: 70%;">';
							var listBoxId = "listBox_" + row;
							var listBox = "<div id='"+ listBoxId +"'></div>";

							//var description = "<div style='margin: 10px;'><b>Description:</b> " + rowData.description + "</div>";
							//rightcolumn += description;
							rightcolumn += listBox;

							rightcolumn += "</div>";
							container += leftcolumn;
							container += rightcolumn;
							container += "</div>";
							return container;
						}
					},

					{ 
						text: 'Availability', datafield: 'availability', width: '20%', /*cellsalign: 'center', align: 'center',*/ 
						cellsRenderer: function (row, column, value, defaulthtml, columnproperties, rowdata) {

							var rowData = $scope.project.resources[row];
							var barId = "horizontalProgressBar_" + row;
							return "<div id='"+ barId +"'></div>";

						}}]
				});
				
				for(var i = 0; i< $scope.project.resources.length; i++){
					var barId = "horizontalProgressBar_" + i;
					var value = parseFloat($scope.project.resources[i].availability);
					$('#'+barId).jqxProgressBar({value: value, showText: true, width: "100%", height: "85%"});

					var listBoxId = "listBox_" + i;
					$('#'+listBoxId).jqxDropDownList({ source: $scope.topics, selectedIndex: 0, disabled: true,  checkboxes: true, width: '100%', height: '25'});

				}

				$("#projectJqxgrid").on('cellclick', function (event) {
					// event arguments.
					var args = event.args;
					// get the column's text.
					var column = $("#projectJqxgrid").jqxGrid('getcolumn', event.args.datafield).text;
					// column data field.
					var dataField = event.args.datafield;
					// row's bound index.
					var rowBoundIndex = event.args.rowindex;
					// cell value
					var value = args.value;

					for(var i = 0; i< $scope.project.resources.length; i++){
						var listBoxId = "listBox_" + i;
						$('#'+listBoxId).jqxDropDownList({ disabled: true});
					}
					var listBoxId = "listBox_" + rowBoundIndex;
					$('#'+listBoxId).jqxDropDownList({ disabled: false});

				});

			},

			function(response) {
				$scope.showProject = false;
				$scope.messageProject = "Error: "+response.status + " " + response.statusText;	
			}
	);

}]);