var ${document} = angular.module('${document}Factory', []);

${document}.factory('${document}API', ['$http', '$upload', 'apiRoot', function ($http, $upload, apiRoot) {
        var dataFactory = {};
        var urlBase = apiRoot + 'documents/${tableName}';

        dataFactory.getCount = function (token) {
            return $http.get(urlBase + '/count?token=' + token);
        };
        dataFactory.getPage = function (token, pageNo) {
            return $http.get(urlBase + '/page/' + pageNo + '?token=' + token);
        };
        dataFactory.getAll = function (token) {
            return $http.get(urlBase + '?token=' + token);
        };
        dataFactory.find = function (token, doc_id) {
            return $http.get(urlBase + '/' + doc_id + '?token=' + token);
        };
        dataFactory.getCode = function (token, doc_id) {
            return $http.get(urlBase + '/' + doc_id + '/code?token=' + token);
        };
        dataFactory.noBatch = function (token, assignment_type, batch_id) {
            return $http.get(urlBase + '/nobatch/' + assignment_type + '/' + batch_id + '?token=' + token);
        };
        dataFactory.noCode = function (token, batch_id) {
            return $http.get(urlBase + '/batch/' + batch_id + '/nocodes/?token=' + token);
        };
        dataFactory.tieBreak = function (token, batch_id) {
            return $http.get(urlBase + '/batch/' + batch_id + '/tiebreak/?token=' + token);
        };
        dataFactory.addCode = function (token, doc_id, batch_id, code_id) {
            return $http.post(urlBase + '/' + doc_id + '/batch/' + batch_id + '/add/code/' + code_id + '?token=' + token);
        };
        dataFactory.update = function (token, doc_obj) {
            return $http.put(urlBase + '?token=' + token, doc_obj);
        };
        dataFactory.create = function (token, doc_obj) {
            return $http.post(urlBase + '?token=' + token, doc_obj);
        };
        dataFactory.getType = function (token) {
            return $http.get(urlBase + '/type?token=' + token);
        };
        dataFactory.upload = function (token, docObj, file) {
            return $upload.upload({
                url: urlBase + '/upload/?token=' + token + '&docObj=' + JSON.stringify(docObj),
                file: file
            });
        };
        return dataFactory;
    }]);