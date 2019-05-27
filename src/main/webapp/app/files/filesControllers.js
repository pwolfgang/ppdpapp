angular.module('filesControllers', ['filesFactory'])
        .controller('filesCtrl', ['$scope', '$location', 'filesAPI', 'authInfo', function ($scope, $location, filesAPI, authInfo) {
                $scope.gridOptions = {
                    enableSorting: true,
                    enableRowSelection: true,
                    enableSelectAll: true,
                    multiSelect: true,
                    columnDefs: [
                        {name: 'ID', field: 'fileID'},
                        {field: 'name'},
                        {name: 'Creator', field: 'creator'},
                        {name: 'dateAdded', field: 'dateAdded', cellFilter: 'date:\'mediumDate\''},
                        {name: 'View', cellTemplate: 'app/files/partials/cellTemplate_files.html'}
                    ]
                };
                $scope.loaded = false;
                $scope.requestFailed = false;

                $scope.reloadFiles = function () {
                    filesAPI.getAll(authInfo.token)
                            .success(function (res) {
                                $scope.gridOptions.data = res;
                                $scope.loaded = true;
                                $scope.requestFailed = false;
                            })
                            .error(function (res, status) {
                                $scope.errMsg = res;
                                $scope.status = status;
                                $scope.loaded = false;
                                $scope.requestFailed = true;
                            });
                };
                $scope.reloadFiles();

                $scope.gridScope = {
                    viewUsers: function (fileObj) {
                        // get the batch_id this file belongs to. load that batch up.
                        filesAPI.findBatch(authInfo.token, fileObj.fileID).success(function (res) {
                            $location.path('/batches/' + res.BatchID + '/view/users');
                        });
                    }
                };
            }])
        .controller('fileCreateCtrl', ['$scope', '$location', '$upload', 'filesAPI', 'tablesAPI', 'batchesAPI', 'authInfo',
            function ($scope, $location, $upload, filesAPI, tablesAPI, batchesAPI, authInfo) {
                // Represents the loading state
                $scope.loaded = false;
                $scope.requestFailed = false;

                $scope.dt = new Date();
                $scope.minDate = new Date();
                $scope.processing = false;
                $scope.batch_type_dd_status = false;
                $scope.batch_type = null;

                $scope.today = function () {
                    $scope.dt = new Date();
                };
                $scope.clear = function () {
                    $scope.dt = null;
                };

                // call tablesAPI to get table names.
                tablesAPI.getAll(authInfo.token)
                        .success(function (res) {
                            $scope.batch_type_dd_items = res;
                            $scope.loaded = true;
                            $scope.requestFailed = false;
                        });
                $scope.setBatchType = function (tableObj) {
                    $scope.batch_type = tableObj;
                };

                $scope.onFileSelect = function ($files) {
                    $scope.processing = true;
                    var file = $files[0];
                    var batchObj = {
                        name: $scope.name,
                        dateDue: $scope.dt,
                        dateAdded: new Date(),
                        tablesID: $scope.batch_type.ID,
                        assignmentTypeID: 1,
                        assignmentDescription: 'Data Entry',
                        creator: authInfo.email
                    };

                    // actual file upload pointing to whatever server.
                    filesAPI.upload(authInfo.token, $scope.batch_type.ID, file)
                            .progress(function (evt) {
                                $scope.progress = parseInt(100.0 * evt.loaded / evt.total);
                            })
                            .success(function (fileObj) {
                                $scope.processing = false;
                                fileObj.name = $scope.name;
                                fileObj.dateAdded = new Date();
                                fileObj.creator = authInfo.email;
                                filesAPI.update(authInfo.token, fileObj)
                                        .success(function (file_obj) {
                                            batchObj.fileID = file_obj.fileID;
                                            batchesAPI.create(authInfo.token, batchObj)
                                                    .success(function (updatedBatchObj) {
                                                        filesAPI.addToBatch(authInfo.token, updatedBatchObj)
                                                                .success(function (res){
                                                                    $scope.processing = false;
                                                                    $location.path('/files');
                                                                })
                                                                .error(function (err){
                                                                    $scope.error=err;
                                                                    $scope.processing = false;
                                                                });
                                                    })
                                                    .error(function (err) {
                                                        $scope.error=err;
                                                        $scope.processing = false;
                                                    });
                                        })
                                        .error(function (err) {
                                            $scope.error=err;
                                            $scope.processing = false;                                                    
                                        });
                            })
                            .error(function (err) {
                                $scope.error = err;
                                $scope.processing = false;
                            });
                };
            }]);
