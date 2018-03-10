angular.module('${document}', ['${document}Controllers'])

.config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/documents/${document}', {
            templateUrl: 'app/documents/${document}/${document}.html',
            controller: '${document}Ctrl',
            resolve: {
                authenticated: function (authFactory) {
                    return authFactory.resolveIsLoggedIn();
                }
            }
        });

        $routeProvider.when('/documents/${document}/create/:batch_id', {
            templateUrl: 'app/documents/${document}/${document}_create.html',
            controller: '${document}CreateCtrl',
            resolve: {
                authenticated: function (authFactory) {
                    return authFactory.resolveIsLoggedIn();
                }
            }
        });

        $routeProvider.when('/documents/${document}/view/:doc_id', {
            templateUrl: 'app/documents/${document}/${document}_create.html',
            controller: '${document}ViewCtrl',
            resolve: {
                authenticated: function (authFactory) {
                    return authFactory.resolveIsLoggedIn();
                }
            }
        });

        $routeProvider.when('/documents/${document}/view/:doc_id/:batch_id', {
            templateUrl: 'app/documents/${document}/${document}_create.html',
            controller: '${document}ViewCtrl',
            resolve: {
                authenticated: function (authFactory) {
                    return authFactory.resolveIsLoggedIn();
                }
            }
        });
    }]);