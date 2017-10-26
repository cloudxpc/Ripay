//config route
(function () {
    "use strict";

    function routeConfig($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise('/');
        $stateProvider.state('main',{
        	url:'/',
        	views:{
        		'content':{
        			templateUrl:'app/main/views/welcome.html',
        		}
        	}
        }).state("pay-success", {
            params: { jsApiData: null },
            views: {
                'content': {
                    templateUrl: 'app/pay/views/pay-success.html'
                }
            }
        }).state("pay-error", {
            views: {
                'content': {
                    templateUrl: 'app/pay/views/pay-error.html'
                }
            }
        }).state("pay-confirm", {
            params: { jsApiData: null },
            views: {
                'content': {
                    templateUrl: 'app/pay/views/pay-confirm.html',
                    // controller: 'PayConfirmController'
                }
            }
        }).state("pay", {
            url: '/pay',
            views: {
                'content': {
                    templateUrl: 'app/pay/views/pay.html',
                    controller: 'PayController'
                }
            }
        });
    };

    routeConfig.$inject = ["$stateProvider", "$urlRouterProvider"];
    angular.module("ripayApp").config(routeConfig);
})();
