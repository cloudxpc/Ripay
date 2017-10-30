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
            views: {
                'content': {
                    templateUrl: 'app/pay/views/pay-success.html'
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
