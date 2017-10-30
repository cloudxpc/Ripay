(function () {
    "use strict";

    function payController($scope, $rootScope, $state, PayService) {
        $scope.orderObj = {
            title: 'test',
            attach: '1243',
            payAmt: 0.01,
            paymentTypeCode: '00001',
            paymentModeCode: null,
            openId: 'oi1lR1MosT0AGJ1PKC8u6GHoo2CA'
        };

        var jsApiCall = function () {
            WeixinJSBridge.invoke(
                'getBrandWCPayRequest',
                {
                    "appId": $scope.jsApiData.appId,             //公众号名称，由商户传入
                    "timeStamp": $scope.jsApiData.timeStamp,     //时间戳，自1970年以来的秒数
                    "nonceStr": $scope.jsApiData.nonceStr,       //随机串
                    "package": $scope.jsApiData.package,
                    "signType": $scope.jsApiData.signType,       //微信签名方式
                    "paySign": $scope.jsApiData.paySign          //微信签名
                },
                function (res) {
                    if (res.err_msg == "get_brand_wcpay_request:ok") {
                        $state.go('pay-success', {jsApiData: $scope.jsApiData}, {location: 'replace'});
                    } else {
                        WeixinJSBridge.log(res.err_msg);
                        //alert(res.err_code + res.err_desc + res.err_msg);
                    }
                }
            );
        };

        var callPay = function () {
            if (typeof WeixinJSBridge == "undefined") {
                if (document.addEventListener) {
                    document.addEventListener('WeixinJSBridgeReady', jsApiCall, false);
                }
                else if (document.attachEvent) {
                    document.attachEvent('WeixinJSBridgeReady', jsApiCall);
                    document.attachEvent('onWeixinJSBridgeReady', jsApiCall);
                }
            }
            else {
                jsApiCall();
            }
        };

        $scope.unifiedOrder = function () {
            PayService.unifiedOrder($scope.orderObj).then(function (response) {
                if (response && response.data) {
                    $scope.jsApiData = response.data;
                    callPay();
                }
            });
        };

        PayService.getAllPaymentBrands().then(function (response) {
            if (response && response.data) {
                $scope.paymentBrands = response.data;
            }
        });
    }

    payController.$inject = ['$scope', '$rootScope', '$state', 'PayService'];
    angular.module('ripayApp').controller('PayController', payController);
})();