var acct = angular.module('accountFactory', ['base64']);

acct.factory('accountAPI', ['$http', '$base64', 'apiRoot', function ($http, $base64, apiRoot) {
        var dataFactory = {};
        var urlBase = apiRoot + 'account/';

        dataFactory.postLogin = function (user, pass) {
            var authData = $base64.encode(user + ':' + pass);
            $http.defaults.headers.common['Authorization'] = 'Basic ' + authData;
            return $http.post(urlBase);
        };
        
        dataFactory.postLogout = function (user) {
            return $http.post(urlBase + "logout/" + user.email);
        };

        return dataFactory;
    }]);