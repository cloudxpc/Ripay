// //config the exception handler
// (function () {
//
//     function exceptionHandler($delegate, $injector) {
//         return function (exception, cause) {
//             $delegate(exception, cause);
//             var exceptionHandlingService = $injector.get('exceptionHandlingService');
//             exceptionHandlingService.handleException(exception);
//         };
//     }
//
//     function exceptionHandlerConfig($provide) {
//         $provide.decorator("$exceptionHandler", exceptionHandler);
//     }
//
//     exceptionHandlerConfig.$inject = ['$provide'];
//     exceptionHandler.$inject = ['$delegate', '$injector'];
//
//     angular.module('ripayApp').config(exceptionHandlerConfig);
//
// })();
