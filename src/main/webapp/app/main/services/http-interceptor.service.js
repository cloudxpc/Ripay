//declare http interceptor service
(function () {
    "use strict";
    function httpInterceptorService($rootScope, $q, $location) {
        var service = {};
        var request = function (config) {
            $rootScope.$broadcast('loading-started');
            return config;
        };

        var requestError = function (rejection) {
            return $q.reject(rejection);
        }


        var responseError = function (rejection) {
            $rootScope.$broadcast('loading-complete');
            if (rejection.data){
                alert(rejection.data.errorMessage + ' ' + rejection.data.description);
            }

            // if (rejection.status === 401) {
            //     $location.path('/login');
            // } else if (rejection.status === 500) {
            //     exceptionHandlingService.handleException(new ServerError(rejection));
            // } else { // if (rejection.status === -1 || !(rejection.status))
            //     exceptionHandlingService.handleException(new ServerError({
            //         data: {
            //             errorMessageKey: 'No response due to bad network connection',
            //             errorSource: 'When trying to get data'
            //         }
            //     }));
            // };
            return $q.reject(rejection);
        };

        var response = function(res) {
            $rootScope.$broadcast('loading-complete');
            return res;
        }

        service.request = request;
        service.requestError = requestError;
        service.response = response;
        service.responseError = responseError;
        return service;
    }
    httpInterceptorService.$inject = ['$rootScope', '$q', '$location'];
    angular.module("ripayApp").factory('httpInterceptorService', httpInterceptorService);
})();