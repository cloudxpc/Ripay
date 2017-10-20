(function () {
    "use strict";
    var ripayApp = angular.module("ripayApp", [
        'ui.router'
    ]);

    //Init App
    function initApp(
        $rootScope,
        $state,
        $stateParams,
        $location,
        $log
    ) {
    	$rootScope.loadingCount = 0;
    	
        $rootScope.$on('loading-started', function (e) {
            $rootScope.loadingCount++;
            if ($rootScope.loadingCount > 0) {
                $rootScope.loading = true;
            } 
        });

        $rootScope.$on('loading-complete', function (e) {
            $rootScope.loadingCount--;
            if ($rootScope.loadingCount <= 0) {
                $rootScope.loading = false;
            }
        });
    }

    initApp.$inject = [
		"$rootScope",
		"$state",
		"$stateParams",
		"$location",
		"$log",
    ];

    ripayApp.run(initApp);
})();