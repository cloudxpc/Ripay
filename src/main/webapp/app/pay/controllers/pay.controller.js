(function () {
    "use strict";
    function payController($scope, $rootScope, PayService) {
        $scope.orderObj = {
            title: '',
            attach: '',
            payAmt: 0,
            paymentTypeCode: '00001',
            paymentModeCode: null,
            openId: ''
        };

        PayService.getAllPaymentBrands().then(function (response) {
            if (response && response.data){
                $scope.paymentBrands = response.data;
            }
        });
    }
    payController.$inject = ['$scope', '$rootScope', 'PayService'];
    angular.module('ripayApp').controller('PayController', payController);
})();