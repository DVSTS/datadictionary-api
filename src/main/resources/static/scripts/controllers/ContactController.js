app.controller('ContactController', function($scope, $rootScope, $location, $http) {
    function initialize() {
      $scope.pastrecords = [];
    }
    
    $scope.init = function () {
    	$http.get('/v1/api/dictionary/swagger/uploadHistory')
    	.success(function(data) {
    		$scope.pastrecords = data;
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