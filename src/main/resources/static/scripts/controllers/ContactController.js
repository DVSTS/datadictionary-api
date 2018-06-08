app.controller('ContactController', function($scope, $rootScope, $location, $http) {
    function initialize() {
      $scope.pastrecords = [];
      $scope.facets = {};
      $scope.selectedFacets = {};
      
      $scope.searchRequest = {
    		  "selectedFacets" :{},
    		  "searchText":""
      }
    }
    
    $scope.search = function(){
    	$http.post('/v1/api/dictionary/search', $scope.searchRequest, {
            headers: { 
                'Content-Type': "application/json",
            },
          })
          .success(function(data) {
             alert(data);
         });
    }
    
    $scope.selectedFields =function(ele) {
    	$scope.searchRequest.selectedFacets[ele.key] = ele.key$index;
    	console.log($scope.searchRequest);
    }
    
    $scope.init = function () {
    	$http.get('/v1/api/dictionary/swagger/uploadHistory')
    	.success(function(data) {
    		$scope.pastrecords = data;
    	});
    	$http.get('/v1/api/dictionary/facets')
    	.success(function(data) {
    		$scope.facets = data;
    		console.log();
    	});
    }
    
    $scope.uploadSwagger = function() {
        if (!confirm('Are you sure you want to upload this Swagger?')) {
            return;
        }
        
        var file = document.getElementById('swagger').files[0];
        var formData = new FormData();
        formData.append('file', file);

        $http.post('/v1/api/dictionary/swagger/upload', formData, {
                headers: { 
                    'Content-Type': undefined,
                },
                transformRequest: angular.identity,
              })
              .success(function(data) {
                 alert(data);
                 $scope.init();
             });
    };
    
    initialize();
});