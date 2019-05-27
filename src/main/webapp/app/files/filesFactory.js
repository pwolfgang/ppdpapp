angular.module('filesFactory', [])
        .factory('filesAPI', ['$http', '$upload', 'apiRoot', function ($http, $upload, apiRoot) {
                var dataFactory = {};
                var urlBase = apiRoot + 'files';

                dataFactory.getAll = function (token) {
                    return $http.get(urlBase + '?token=' + token);
                };
                dataFactory.find = function(token, file_id) {
                    return $http.get(urlBase + '/' + file_id + '?token=' + token);
                }
                dataFactory.findBatch = function (token, file_id) {
                    return $http.get(urlBase + '/' + file_id + '/batches/?token=' + token);
                };
                dataFactory.update = function (token, fileObj) {
                    return $http.post(urlBase + '/update/?token=' + token, fileObj);
                };
                dataFactory.upload = function (token, tableId, file) {
                    return $upload.upload({
                        url: urlBase + '/upload/?token=' + token + '&tableId=' + tableId,
                        file: file
                    });
                };
                dataFactory.addToBatch = function (token, batchObj) {
                    return $http.post(urlBase + '/addToBatch/?token=' + token, batchObj);
                }
                return dataFactory;
            }]);
