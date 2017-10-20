//declare module contants to store the settings
//local development environment
(function() {
    'use strict';
    angular.module('ripayApp')
        .constant('defaultAppSettings', {
            // webApiBaseUrl: 'https://wechat.xiaoqi.com/wechat/api/'
            webApiBaseUrl: 'http://localhost:8080/api/'
        });
})();