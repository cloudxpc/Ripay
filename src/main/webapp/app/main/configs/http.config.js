//config http interceptor
(function () {
    "use strict";
    function httpConfig($httpProvider) {
        $httpProvider.interceptors.push('httpInterceptorService');
    }
    httpConfig.$inject = ['$httpProvider'];
    angular.module('ripayApp').config(httpConfig);
})();