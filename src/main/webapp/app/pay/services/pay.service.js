(function () {
    "use strict";

    function payService($http, defaultAppSettings) {
        var baseUrl = defaultAppSettings.webApiBaseUrl;

        var getAllPaymentBrands = function () {
            return $http.get(baseUrl + 'data/getAllPaymentBrands');
        };

        var unifiedOrder = function(orderObj){
            return $http.post(baseUrl + 'pay/unifiedorder', orderObj);
        };

        return {
            getAllPaymentBrands: getAllPaymentBrands,
            unifiedOrder: unifiedOrder
        };
    }

    payService.$inject = ['$http', 'defaultAppSettings'];
    angular.module('ripayApp').factory('PayService', payService);
})();