var acct = angular.module('assignments', ['assignmentsControllers']);

acct.config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/assignments', {
            templateUrl: 'app/assignments/assignments.html',
            controller: 'assignmentsCtrl',
            resolve: {
                authenticated: function (authFactory) {
                    return authFactory.resolveIsLoggedIn();
                }
            }
        });
        
        $forEachTable
        $routeProvider.when('/assignments/:batch_id/view/${tableName}', {
            templateUrl: 'app/documents/${document}/${document}_code.html',
            caseInsensitiveMatch: true,
            controller: '${document}CodeCtrl',
            resolve: {
                authenticated: function (authFactory) {
                    return authFactory.resolveIsLoggedIn();
                }
            }
        });
        
        $routeProvider.when('/assignments/:batch_id/tiebreak/${tableName}', {
            templateUrl: 'app/documents/${document}/${document}_tiebreak.html',
            caseInsensitiveMatch: true,
            controller: '${document}TiebreakCtrl',
            resolve: {
                authenticated: function (authFactory) {
                    return authFactory.resolveIsLoggedIn();
                }
            }
        });
        
        $routeProvider.when('/assignments/:batch_id/cluster_resolution/${tableName}', {
           templateUrl: 'app/documents/${document}/${document}_cluster_resolver.html',
           caseInsensitiveMatch: true,
           controller: '${document}ClusterResolutionCtrl',
           resolve: {
               authenticated: function (authFactory) {
                   return authFactory.resolveIsLoggedIn();
               }
           }
        });

        $endFor
            
    }]);