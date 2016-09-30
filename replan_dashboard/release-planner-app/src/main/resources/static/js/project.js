var app = angular.module('w5app');
app.controllerProvider.register('project-utilities', ['$scope', '$location', '$http',
                                     function ($scope, $location, $http) {
	/*
 	* REST methods
 	*/
	var baseURL = "http://62.14.219.13:3000/api/ui/v1/projects/1";
	
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
	
	$scope.addNewResourceToProject = function (resource){

		return $http({
			method: 'POST',
			url: baseURL+'/resources',
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: resource
		});	 
	};	
	
	//Modifies a given resource 
	$scope.updateResourceForProject = function (resource){

		return $http({
			method: 'PUT',
			url: baseURL + '/resources/'+resource.id,
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: resource
		});	 
	};
	
	$scope.removeResourceFromProject = function (resource){

		return $http({
			method: 'DELETE',
			url: baseURL +'/resources/' + resource.id,
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: resource
		});	 
	};
	
	$scope.getProjectSkills = function(){

		return $http({
			method: 'GET',
			url: baseURL + '/skills'
		});
	};
	
	$scope.addSkillsToResource = function(resource, arraySkillIds){

		//create data OBj like {"_json": [{"skill_id": 3}]}
		var arr = [];
		for(var i = 0 ; i< arraySkillIds.length; i++){
			var obj = {};
			obj["skill_id"] = arraySkillIds[i];
			arr.push(obj);
		}

		var dataObj = {};
		dataObj["_json"] = arr;

		return $http({
			method: 'POST',
			url: baseURL + '/resources/'+ resource.id + '/skills',
			headers: {"Content-Type": "application/json;charset=UTF-8"},
			data: dataObj
		});	
	
	};
	
	/*
 	* All methods Top down
 	*/
	$scope.showProject = false;
	$scope.messageProject = "Loading ...";
	$scope.project = {resources: []};
	$scope.update = true;
	$scope.resource = { availability:"", description:"", id: -1, name:""};
	$scope.resource["skills"] = [];
	$scope.typeLabel = "Add";
	$scope.skills = [];
	$scope.showAddUpdateResouceForm = false;

	
	
	
	
	$scope.initTable = function(response){
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
				text: 'Skills',
				align: 'center',
				dataField: 'id',
				width: '80%',
				'editable': true,
				cellsRenderer: function (row, column, value) {
					var rowData = $scope.project.resources[row];
					var container = '<div class="jqx-grid-cell" style="width: 100%; height: 95%;">'
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
				}}

				]
		});
		
		//initialize
		//Progress bars and list boxes
		for(var i = 0; i< $scope.project.resources.length; i++){
			var barId = "horizontalProgressBar_" + i;
			var value = parseFloat($scope.project.resources[i].availability);
			$('#'+barId).jqxProgressBar({value: value, showText: true, width: "100%", height: "85%"});
			
		     // prepare the data
            var source =
            {
                datatype: "json",
                datafields: [
                    { name: 'id' },
                    { name: 'name' }
                ],
                id: 'id',
                localdata: $scope.project.resources[i].skills
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
         	
			var listBoxId = "listBox_" + i;
			
			$('#'+listBoxId).jqxDropDownList({ source: dataAdapter , displayMember: "name", valueMember: "id", selectedIndex: 0, disabled: true, checkboxes: true, enableSelection:false, width: '100%', height: '25'});
			$('#'+listBoxId).jqxDropDownList('checkAll'); 
		}
		

		$("#projectJqxgrid").on('cellclick', function (event) {
			
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

			
			$scope.resource = $scope.project.resources[event.args.rowindex];
			$scope.typeLabel = "Update Resource";
			
			
			//jqxDropDownList in FORM
			
			// prepare the data
		    var sourceSkillsListBoxId =
		    {
		        datatype: "json",
		        datafields: [
		            { name: 'id' },
		            { name: 'name' }
		        ],
		        id: 'id',
		        localdata: $scope.resource.skills
		    };
		    $scope.dataAdapterSkillsListBoxId = new $.jqx.dataAdapter(sourceSkillsListBoxId);
		    //$scope.dataAdapterSkillsListBoxId.dataBind();
		    
//		    var items = $("#skillsListBoxId").jqxDropDownList('getItems'); 
//		    for(var i=0; i< items.lenght; i++){
//		    	var index = i+1;
//		    	$("#skillsListBoxId").jqxDropDownList({selectedIndex: index });
//		    }
//		    $("#skillsListBoxId").jqxDropDownList('clearSelection'); 
//		    $('#skillsListBoxId').jqxDropDownList('checkAll'); 

		
		});		
	};

	/**
	 * FORM methods
	 */
	
	//skillsListBoxId
	$scope.resourceTopicRequired = '';
	$scope.resourceTopicRequiredBln = false;
	
	// prepare the data
    var sourceSkillsListBoxId =
    {
        datatype: "json",
        datafields: [
            { name: 'id' },
            { name: 'name' }
        ],
        id: 'id',
        localdata: $scope.skills
    };
    $scope.dataAdapterSkillsListBoxId = new $.jqx.dataAdapter(sourceSkillsListBoxId);
	
    //settings
	$scope.dropDownListSettings = { 
			source: $scope.dataAdapterSkillsListBoxId,
			displayMember: "name",
			valueMember: "id",
			//selectedIndex: 0,
			checkboxes: true,
			enableSelection: true,
			width: '93%',
			height: '15'

	};
	
	$('#skillsListBoxId').on('checkChange', function (event){
	    var args = event.args;
	    if (args) {
	        var items = $("#skillsListBoxId").jqxDropDownList('getCheckedItems'); 
		    if(items.length == 0){
		    	$scope.resourceTopicRequired = 'has-error';
		    	$scope.resourceTopicRequiredBln = true;
		    }
		    else{
		    	$scope.resourceTopicRequired = '';
		    	$scope.resourceTopicRequiredBln = false;
		    }
	    }
	    		
	});
	$('#skillsListBoxId').on('open', function (event){
		$('#skillsListBoxId').jqxDropDownList('checkAll'); 
	    		
	});
	
	
	//resoureAvailabilityInput
	$scope.resourceAvailabilityRequired = '';
	$scope.resourceAvailabilityRequiredBln = false;
	$('#resoureAvailabilityInput').on('change', function (event) {
	    var value = event.args.value;
	    var type = event.args.type; // keyboard, mouse or null depending on how the value was changed.
	    
	    if(value <= 0 || value >100){
	    	$scope.resourceAvailabilityRequired = 'has-error';
	    	$scope.resourceAvailabilityRequiredBln = true;
	    }
	    else{
	    	$scope.resourceAvailabilityRequired = '';
	    	$scope.resourceAvailabilityRequiredBln = false;
	    }
	});
	
	
	
	
	
	
	
	
	
	
	
	
	
	$scope.resetFormAddUpdateResouceForm = function(){
	
		$scope.addUpdateResouceForm.$setPristine();
		 
		$scope.typeLabel = "Add";
		$scope.resource = { availability:"", description:"", name:""};
		$scope.resource["skills"] = [];
		$("#skillsListBoxId").jqxDropDownList('uncheckAll'); 

		$scope.showAddUpdateResouceForm = true;
		
		$scope.getProjectSkills()
		.then(
				function(response) {
					
					//skillsListBoxId
					$scope.skills = response.data;
					// prepare the data
				    var sourceSkillsListBoxId =
				    {
				        datatype: "json",
				        datafields: [
				            { name: 'id' },
				            { name: 'name' }
				        ],
				        id: 'id',
				        localdata: $scope.skills
				    };
				    $scope.dataAdapterSkillsListBoxId = new $.jqx.dataAdapter(sourceSkillsListBoxId);
				    
				    $scope.resourceTopicRequired = '';
			    	$scope.resourceTopicRequiredBln = false;
				   
				},

				function(response) {
					$scope.showProject = false;
					$scope.messageProject = "Error: "+response.status + " " + response.statusText;	
				}
		);
	};
	
	$scope.editFormAddUpdateResouceForm = function(){
		//$scope.addUpdateResouceForm.$setPristine();
		if($scope.resource.id != -1){
			$scope.showAddUpdateResouceForm = true;
		}
	};
	
	$scope.hideAddUpdateResouceForm = function(){
		$scope.showAddUpdateResouceForm = false;
	};
	
	$scope.removeResource = function(){
		$scope.removeResourceFromProject($scope.resource).then(
		        function(response) {
		        	$scope.getProject()
		        	.then(
		        			function(response) {
		        				$scope.initTable(response);
		        				$scope.showAddUpdateResouceForm = false;
		        			},

		        			function(response) {
		        				$scope.showProject = false;
		        				$scope.messageProject = "Error: "+response.status + " " + response.statusText;	
		        			}
		        	);
		        	
		        },
		        
		        function(response) {
		        	$scope.showReleases = false;
		            $scope.messageReleases = "Error: "+response.status + " " + response.statusText;
		        }
		    );
	};
	
	$scope.addUpdateResource = function(){
		
		
		
		var value = $('#resoureAvailabilityInput').val();
		if(value <= 0 || value >100){
	    	$scope.resourceAvailabilityRequired = 'has-error';
	    	$scope.resourceAvailabilityRequiredBln = true;
	    	return;
		}
	
		var items = $("#skillsListBoxId").jqxDropDownList('getCheckedItems'); 
	    if(items.length == 0){
	    	$scope.resourceTopicRequired = 'has-error';
	    	$scope.resourceTopicRequiredBln = true;
	    	return;
	    }
	   
	    var arraySkillIds = []; 
	    
	    for(var i=0; i< items.length ; i++){
	    	arraySkillIds[i] = items[i].value;
	    }
		
		if($scope.typeLabel = "Add"){
			
			$scope.addNewResourceToProject($scope.resource)
		    .then(
		        function(response) {
		        	
		        	$scope.addSkillsToResource(response.data, arraySkillIds)
				    .then(
				        function(response) {
				       
				         	//update the table
				        	$scope.getProject()
				        	.then(
				        			function(response) {
				        				$scope.initTable(response);
				        				$scope.showAddUpdateResouceForm = false;
				        			},

				        			function(response) {
				        				$scope.showProject = false;
				        				$scope.messageProject = "Error: "+response.status + " " + response.statusText;	
				        			}
				        	);
				        	
				        },
				        
				        function(response) {
				        	$scope.showReleases = false;
				            $scope.messageReleases = "Error: "+response.status + " " + response.statusText;
				        }
				    );
	
		        },
		        
		        function(response) {
		        	$scope.showReleases = false;
		            $scope.messageReleases = "Error: "+response.status + " " + response.statusText;
		        }
		    );
			
		}
		else{
			$scope.updateResourceForProject($scope.resource)
		    .then(
		        function(response) {
		        	//$location.path("/release-planner-app/project");
		        	$scope.getProject()
		        	.then(
		        			function(response) {
		        				$scope.initTable(response);
		        				$scope.showAddUpdateResouceForm = false;
		        			},

		        			function(response) {
		        				$scope.showProject = false;
		        				$scope.messageProject = "Error: "+response.status + " " + response.statusText;	
		        			}
		        	);
		        },
		        
		        function(response) {
		        	$scope.showReleases = false;
		            $scope.messageReleases = "Error: "+response.status + " " + response.statusText;
		        }
		    );
		}
	};
	
	//updateProject
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

	//cancel button Project
	$scope.cancel = function(){
		$location.path("/release-planner-app/main");
	};

	
	/**
	 * start point method
	 */
	$scope.getProject()
	.then(
			function(response) {
				$scope.initTable(response);
			},

			function(response) {
				$scope.showProject = false;
				$scope.messageProject = "Error: "+response.status + " " + response.statusText;	
			}
	);

}]);