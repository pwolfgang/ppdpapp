var batches = angular.module('batches', ['batchesControllers']);

batches.config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/batches', {
            templateUrl: 'app/batches/batches.html',
            controller: 'batchesCtrl',
            resolve: {
                authenticated: function (authFactory) {
                    return authFactory.resolveIsLoggedIn();
                }
            }
        });

        $routeProvider.when('/batches/:batch_id/view/users', {
            templateUrl: 'app/batches/batch_view_users.html',
            controller: 'batchViewUsersCtrl',
            resolve: {
                authenticated: function (authFactory) {
                    return authFactory.resolveIsLoggedIn();
                }
            }
        });

        $routeProvider.when('/batches/create', {
            templateUrl: 'app/batches/batch_create.html',
            controller: 'batchCreateCtrl',
            resolve: {
                authenticated: function (authFactory) {
                    return authFactory.resolveIsLoggedIn();
                }
            }
        });
       
        $forEachTable
        $routeProvider.when('/batches/:batch_id/:action/${tableName}', {
            templateUrl: 'app/documents/${documentName}/${documentName}.html',
            caseInsensitiveMatch: true,
            controller: '${documentName}BatchCtrl',
            resolve: {
                authenticated: function (authFactory) {
                    return authFactory.resolveIsLoggedIn();
                }
            }
        });        
        $endFor
    }]);