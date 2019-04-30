var ${document} = angular.module('${document}Controllers', ['${document}Filters', '${document}Factory', 'batchesFactory', 'ngSanitize']);
${document}.controller('${document}Ctrl', ['$scope', '$routeParams', '$q', '$location', '${document}API', 'authInfo', 
                               function ($scope, $routeParams, $q, $location, ${document}API, authInfo) {
                $scope.loaded = false;
                $scope.requestFailed = false;
                $scope.grid${documentUC} = {
                    enableRowSelection: true,
                    enableSelectAll: true,
                    multiSelect: true,
                    columnDefs: [
                        {field: 'ID'},
                        ${textFieldDefs},
                        ${dateColumnDefs},
                        {field: 'Status'},
                        {name: 'View', cellTemplate: 'app/documents/${document}/partials/cellTemplate_${document}.html'}
                    ]
                };
                $scope.reload${documentUC} = function () {
                $scope.loaded = false;
                $scope.requestFailed = false;
                ${document}API.getAll(authInfo.token)
                        .success(function (res) {
                        for (i = 0; i < res.length; i++) {
                        if (res[i].${codeColumn} !== null && res[i].stat !== -1) {
                        res[i].Status = "complete";
                        } else if (res[i].stat === 0) {
                        res[i].Status = "needs first code";
                        } else if (res[i].stat === 1) {
                        res[i].Status = "needs second code";
                        } else if (res[i].stat === 2) {
                        res[i].Status = "needs tie break";
                        } else if (res[i].stat === -1) {
                        res[i].Status = "needs cluster resolution"
                        }
                        }
                        $scope.grid${documentUC}.data = res;
                                $scope.loaded = true;
                                $scope.requestFailed = false;
                        })
                        .error(function (res) {
                            $scope.errMsg = res
                            $scope.loaded = false;
                            $scope.requestFailed = true;
                        });
                };
                $scope.reload${documentUC}();
                $scope.load${documentNameSignular} = function (newsObj) {
                        $location.path('/documents/${document}/view/' + newsObj.ID);
                };
                $scope.saveRow = function (rowEntity) {
                var promise = ${document}API.update(authInfo.token, rowEntity);
                        $scope.gridApi.rowEdit.setSavePromise($scope.gridApi.grid, rowEntity, promise);
                        promise.success(function (res) {
                        });
                };
                $scope.grid${documentUC}.onRegisterApi = function (gridApi) {
        //set gridApi on scope
        $scope.gridApi = gridApi;
                gridApi.rowEdit.on.saveRow($scope, $scope.saveRow);
        };
        }])
        .controller('${document}BatchCtrl', ['$scope', '$routeParams', '$q', '$location', '${document}API', 'batchesAPI', 'authInfo', function ($scope, $routeParams, $q, $location, ${document}API, batchesAPI, authInfo) {
                console.log('${document}BatchCtrl called');
                console.log($routeParams);
                $scope.loaded = false;
                $scope.requestFailed = false;
                $scope.process_action = $routeParams.action;
                $scope.processing_action = false;
                $scope.grid${documentUC} = {
        enableRowSelection: true,
                enableSelectAll: true,
                multiSelect: true,
                columnDefs: [
                    {field: 'ID'},
                        ${textFieldDefs},
                        ${dateColumnDefs},
                {field: 'Status'}
                ]
        };
                $scope.reloadNoBatch = function (batch_id) {
                $scope.loaded = false;
                        $scope.requestFailed = false;
                        batchesAPI.find(authInfo.token, batch_id)
                        .success(function (res) {
                        var assignment_type = res.assignmentTypeID;
                                ${document}API.noBatch(authInfo.token, assignment_type, batch_id)
                                .success(function (res) {
                                for (i = 0; i < res.length; i++) {
                                if (res[i].Code !== null  && res[i].stat !== -1) {
                                res[i].Status = "complete";
                                } else if (res[i].stat === 0) {
                                res[i].Status = "needs first code";
                                } else if (res[i].stat === 1) {
                                res[i].Status = "needs second code";
                                } else if (res[i].stat === 2) {
                                res[i].Status = "need tie break";
                                } else if (res[i].stat === -1) {
                                res[i].Status = "needs cluster resolution"
                                }
                                }
                                $scope.grid${documentUC}.data = res;
                                        $scope.loaded = true;
                                        $scope.requestFailed = false;
                                })
                                .error(function (res) {
                                    $scope.errMsg = res;
                                    $scope.loaded = false;
                                    $scope.requestFailed = true;
                                });
                        });
                };
                $scope.reloadBatchDocs = function () {
                $scope.loaded = false;
                        $scope.requestFailed = false;
                        batchesAPI.getDocuments(authInfo.token, batch_id)
                        .success(function (res) {
                        $scope.grid${documentUC}.data = res;
                                $scope.loaded = true;
                                $scope.requestFailed = false;
                        })
                        .error(function (res) {
                            $scope.errMsg = res;
                            $scope.loaded = false;
                            $scope.requestFailed = true;
                        });
                };
                var batch_id = $routeParams.batch_id;
                if (typeof batch_id === 'undefined') {
        $scope.loaded = false;
                $scope.requestFailed = false;
                ${document}API.getAll(authInfo.token)
                .success(function (res) {
                $scope.grid${documentUC}.data = res;
                        $scope.loaded = true;
                        $scope.requestFailed = false;
                })
                .error(function (res) {
                    $scope.errMsg = res;
                    $scope.loaded = false;
                    $scope.requestFailed = true;
                });
        } else {
        switch ($routeParams.action) {
        case 'add':
                $scope.reloadNoBatch(batch_id);
                break;
                case 'view':
                $scope.reloadBatchDocs();
                break;
        }
        }


        $scope.grid${documentUC}.onRegisterApi = function (gridApi) {
        $scope.gridApi = gridApi;
        };
                $scope.doBatchAction = function () {
                if ($scope.processing_action)
                        return;
                        var selectedRows = $scope.gridApi.selection.getSelectedRows();
                        var promises = [];
                        $scope.processing_action = true;
                        for (var i = 0; i < selectedRows.length; i++) {
                switch ($routeParams.action) {
                case 'add':
                        promises.push(batchesAPI.addDocument(authInfo.token, batch_id, selectedRows[i].ID));
                        break;
                        case 'view':
                        case 'delete':
                        promises.push(batchesAPI.deleteDocument(authInfo.token, batch_id, selectedRows[i].ID));
                        break;
                }
                }

                $q.all(promises).then(function () {
                $scope.processing_action = false;
                        //$location.path('/batches/' + $routeParams.batch_id + '/view/${document}');
                        switch ($routeParams.action) {
                case 'add':
                        $scope.reloadNoBatch(batch_id);
                        break;
                        case 'view':
                        case 'delete':
                        $scope.reloadBatchDocs(batch_id);
                        break;
                }
                });
                };
        }])
        .controller('${document}CodeCtrl', ['$scope', '$routeParams', '$q', '$location', 'authInfo', '${document}API', 'batchesAPI', function ($scope, $routeParams, $q, $location, authInfo, ${document}API, batchesAPI) {
        $scope.loaded = false;
                $scope.requestFailed = false;
                $scope.gridOptions = {};
                // the reason all results are returned is because the typeahead expects functions to return a new result
                // that reflects the current value. this method returns all the codes NO MATTER WHAT
                $scope.external = {
                    loading: false,
                    onSelect: function ($item, $model, $label, row) {
                        row.entity.Coding = $item.Code;
                    }
                };
                $scope.reloadBatchDocs = function () {
                $scope.loaded = false;
                        $scope.requestFailed = false;
                        ${document}API.noCode(authInfo.token, $routeParams.batch_id)
                        .success(function (res) {
                        batchesAPI.find(authInfo.token, $routeParams.batch_id)
                                .success(function (res_inner) {
                                $scope.loaded = true;
                                        $scope.requestFailed = false;
                                        if (res_inner.fileID !== null)
                                        $scope.has_file = true;
                                        $scope.file_id = res_inner.fileID;
                                        $scope.batch_id = $routeParams.batch_id;
                                });
                                $scope.gridOptions.data = res;
                        })
                        .error(function (res) {
                            $scope.errMsg = res;
                            $scope.loaded = false;
                            $scope.requestFailed = true;
                        });
                };
                $scope.reloadBatchDocs();
                $scope.add${documentUC}ToBatch = function (batchId) {
                $location.path('/documents/${document}/create/' + batchId);
                };
                $scope.editedRows = [];
                $scope.gridOptions.onRegisterApi = function (gridApi) {
                $scope.gridApi = gridApi;
                        gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                        if (typeof rowEntity.Coding !== 'undefined' && rowEntity.Coding.length > 0) {
                        $scope.editedRows[rowEntity.ID] = rowEntity;
                        } else {
                        $scope.editedRows[rowEntity.ID] = undefined;
                        }
                        });
                };
                $scope.codeDocs = function () {
                if (processing)
                        return;
                        var promises = [];
                        $scope.processing = true;
                        $scope.editedRows.forEach(function (row) {
                        if (typeof row !== 'undefined') {
                        promises.push(${document}API.addCode(authInfo.token, row.ID, $routeParams.batch_id, row.Coding));
                        }
                        });
                        $q.all(promises).then(function () {
                $scope.processing = false;
                        $scope.reloadBatchDocs();
                });
                };
                $scope.codeDoc = function (row) {
                ${document}API.addCode(authInfo.token, row.ID, $routeParams.batch_id, row.UserCode)
                        .error(function(res) {
                        alert('Error updating database\n' + res + '\nSee log');
                        });
                };
                $scope.viewDoc = function (rowID) {
                var route = '/documents/${document}/view/' + rowID + '/' + $routeParams.batch_id;
                        $location.path(route);
                };
        }])
        .controller('${document}TiebreakCtrl', ['$scope', '$routeParams', '$q', '$location', 'authInfo', '${document}API', 'batchesAPI', function ($scope, $routeParams, $q, $location, authInfo, ${document}API, batchesAPI) {
        $scope.loaded = false;
                $scope.requestFailed = false;
                $scope.gridOptions = {};
                // the reason all results are returned is because the typeahead expects functions to return a new result
                // that reflects the current value. this method returns all the codes NO MATTER WHAT
                $scope.external = {
                loading: false,
                        onSelect: function ($item, $model, $label, row) {
                        row.entity.Coding = $item.Code;
                        }
                };
                $scope.reloadBatchDocs = function () {
                $scope.loaded = false;
                        $scope.requestFailed = false;
                        ${document}API.tieBreak(authInfo.token, $routeParams.batch_id)
                        .success(function (res) {
                        $scope.gridOptions.data = res;
                                $scope.loaded = true;
                                $scope.requestFailed = false;
                        })
                        .error(function (res) {
                            $scope.errMst = res
                            $scope.loaded = false;
                            $scope.requestFailed = true;
                        });
                };
                $scope.reloadBatchDocs();
                $scope.add${documentUC}ToBatch = function (batchId) {
                $location.path('/documents/${document}/create/' + batchId);
                };
                $scope.editedRows = [];
                $scope.gridOptions.onRegisterApi = function (gridApi) {
                $scope.gridApi = gridApi;
                        gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                        if (typeof rowEntity.Coding !== 'undefined' && rowEntity.Coding.length > 0) {
                        $scope.editedRows[rowEntity.ID] = rowEntity;
                        } else {
                        $scope.editedRows[rowEntity.ID] = undefined;
                        }
                        });
                };
                $scope.codeDocs = function () {
                if (processing)
                        return;
                        var promises = [];
                        $scope.processing = true;
                        $scope.editedRows.forEach(function (row) {
                        if (typeof row !== 'undefined' && typeof row.Coding !== 'undefined') {
                        promises.push(${document}API.addCode(authInfo.token, row.ID, $routeParams.batch_id, row.Coding));
                        }
                        });
                        $q.all(promises).then(function () {
                $scope.processing = false;
                        $scope.reloadBatchDocs();
                });
                };
                $scope.codeDoc = function (row) {
                    if (typeof row.UserCode !== 'undefined') {
                        ${document}API.addCode(authInfo.token, row.ID, $routeParams.batch_id, row.UserCode)
                                .error(function(res) {
                                    alert('Error updating database\n' + res + '\nSee log');
                                });
                    }
                };
        }])
        .controller('${document}ClusterResolutionCtrl', ['$scope', '$routeParams', '$q', '$location', 'authInfo', '${document}API', 'batchesAPI', function ($scope, $routeParams, $q, $location, authInfo, ${document}API, batchesAPI) {
                $scope.loaded = false;
                $scope.requestFailed = false;
                $scope.gridOptions = {};
                // the reason all results are returned is because the typeahead expects functions to return a new result
                // that reflects the current value. this method returns all the codes NO MATTER WHAT
                $scope.external = {
                loading: false,
                        onSelect: function ($item, $model, $label, row) {
                        row.entity.Coding = $item.Code;
                        }
                };
                $scope.reloadBatchDocs = function () {
                    console.log("reloadBatchDocs called");
                $scope.loaded = false;
                        $scope.requestFailed = false;
                        ${document}API.clusterResolution(authInfo.token, $routeParams.batch_id)
                        .success(function (res) {
                        $scope.gridOptions.data = res;
                                $scope.loaded = true;
                                $scope.requestFailed = false;
                        })
                        .error(function (res) {
                            $scope.errMst = res
                            $scope.loaded = false;
                            $scope.requestFailed = true;
                        });
                };
                $scope.reloadBatchDocs();
                $scope.add${documentUC}ToBatch = function (batchId) {
                $location.path('/documents/${document}/create/' + batchId);
                };
                $scope.editedRows = [];
                $scope.gridOptions.onRegisterApi = function (gridApi) {
                $scope.gridApi = gridApi;
                        gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                        if (typeof rowEntity.Coding !== 'undefined' && rowEntity.Coding.length > 0) {
                        $scope.editedRows[rowEntity.ID] = rowEntity;
                        } else {
                        $scope.editedRows[rowEntity.ID] = undefined;
                        }
                        });
                };
                $scope.codeDocs = function () {
                if (processing)
                        return;
                        var promises = [];
                        $scope.processing = true;
                        $scope.editedRows.forEach(function (row) {
                        if (typeof row !== 'undefined' && typeof row.UserCode !== 'undefined') {
                        promises.push(${document}API.updateCode(authInfo.token, row.ID, row.UserCode));
                        }
                        });
                        $q.all(promises).then(function () {
                $scope.processing = false;
                        $scope.reloadBatchDocs();
                });
                };
                $scope.codeDoc = function (row) {
                    if (typeof row.UserCode !== 'undefined') {
                        ${document}API.updateCode(authInfo.token, row.ID, $routeParams.batch_id, row.UserCode)
                                .error(function(res) {
                                    alert('Error updating database\n' + res + '\nSee log');
                                });
                    }
                };
        }])
        .controller('${document}CAPCodeReviewCtrl', ['$scope', '$routeParams', '$q', '$location', 'authInfo', '${document}API', 'batchesAPI', function ($scope, $routeParams, $q, $location, authInfo, ${document}API, batchesAPI) {
                $scope.loaded = false;
                $scope.requestFailed = false;
                $scope.gridOptions = {};
                // the reason all results are returned is because the typeahead expects functions to return a new result
                // that reflects the current value. this method returns all the codes NO MATTER WHAT
                $scope.external = {
                loading: false,
                        onSelect: function ($item, $model, $label, row) {
                        row.entity.Coding = $item.Code;
                        }
                };
                $scope.reloadBatchDocs = function () {
                    console.log("reloadBatchDocs called");
                $scope.loaded = false;
                        $scope.requestFailed = false;
                        ${document}API.clusterResolution(authInfo.token, $routeParams.batch_id)
                        .success(function (res) {
                        $scope.gridOptions.data = res;
                                $scope.loaded = true;
                                $scope.requestFailed = false;
                        })
                        .error(function (res) {
                            $scope.errMst = res
                            $scope.loaded = false;
                            $scope.requestFailed = true;
                        });
                };
                $scope.reloadBatchDocs();
                $scope.add${documentUC}ToBatch = function (batchId) {
                $location.path('/documents/${document}/create/' + batchId);
                };
                $scope.editedRows = [];
                $scope.gridOptions.onRegisterApi = function (gridApi) {
                $scope.gridApi = gridApi;
                        gridApi.edit.on.afterCellEdit($scope, function (rowEntity, colDef, newValue, oldValue) {
                        if (typeof rowEntity.Coding !== 'undefined' && rowEntity.Coding.length > 0) {
                        $scope.editedRows[rowEntity.ID] = rowEntity;
                        } else {
                        $scope.editedRows[rowEntity.ID] = undefined;
                        }
                        });
                };
                $scope.codeDocs = function () {
                if (processing)
                        return;
                        var promises = [];
                        $scope.processing = true;
                        $scope.editedRows.forEach(function (row) {
                        if (typeof row !== 'undefined' && typeof row.UserCode !== 'undefined') {
                        promises.push(${document}API.updateCode(authInfo.token, row.ID, row.UserCode));
                        }
                        });
                        $q.all(promises).then(function () {
                $scope.processing = false;
                        $scope.reloadBatchDocs();
                });
                };
                $scope.codeDoc = function (row) {
                    if (typeof row.UserCode !== 'undefined') {
                        ${document}API.updateCode(authInfo.token, row.ID, $routeParams.batch_id, row.UserCode)
                                .error(function(res) {
                                    alert('Error updating database\n' + res + '\nSee log');
                                });
                    }
                };
        }])
        .controller('${document}CreateCtrl', ['$scope', '$routeParams', '$q', '$location', 'authInfo', '${document}API', 'newspapersAPI', 'batchesAPI', 'filesAPI', function ($scope, $routeParams, $q, $location, authInfo, ${document}API, newspapersAPI, batchesAPI, filesAPI) {
        // Represents the loading state
            $scope.loaded = false;
            $scope.requestFailed = false;
            ${initializeDateFields}
            ${dateFieldFunctions}
        $scope.processing = false;
        $scope.filters = [
            ${binaryFilters}
        ];
        $scope.open = function ($event) {
            $scope.status.opened = true;
        };
        $scope.status = {
            opened: false
        };
        ${multiValuedFiltersJs}
        ${typeAheadFieldsJs}
        $if(fileUploadJavaScript) ${fileUploadJavaScript}
        $scope.loaded=true;
            $scope.create = function () {
                $scope.processing = true;
                ${multiValuedFiltersSetDefaultJs}
                ${setLastDate}
            var ${document}Obj = {
                ${textFields},
                ${dateFields},
                $if(defineBinaryFilterFields)${defineBinaryFilterFields},
                ${multiValuedFiltersFields}
                };
            ${document}API.create(authInfo.token, ${document}Obj)
                        .success(function (doc_id) {
                        var batch_id = $routeParams.batch_id;
                            if (batch_id === 'none') {
                                $scope.processing = false;
                                $location.path('/documents/${document}');
                            } else {
                                batchesAPI.addDocument(authInfo.token, batch_id, doc_id)
                                    .success(function (res) {
                                        if (typeof($scope.UserCode) === 'undefined') { 
                                            $scope.processing = false;
                                            $location.path('/assignments/' + batch_id + '/view/${tableName}');
                                        } else {
                                            ${document}API.addCode(authInfo.token, doc_id, batch_id, $scope.UserCode)
                                                .success(function (res) {
                                                    $scope.processing = false;
                                                    $location.path('/assignments/' + batch_id + '/view/${tableName}');
                                                })
                                                .error(function (res) {
                                                    alert('Error adding code ' + res + '\nSee log');
                                                });
                                        }
                                    })
                                    .error(function (res) {
                                        alert('Error adding document to batch ' + res);
                                    });
                            }
                        })
                        .error(function (res) {
                            alert('Error saving ${document} ' + res + '\nSee log');
                        });
                };
                $scope.convertBoolToInt = function (num) {
                return (num) ? 1 : 0;
                };
                $scope.onFileSelect = function ($files) {
                     var ${document}Obj = {
                          ${textFields},
                          ${dateFields},
                          $if(defineBinaryFilterFields)${defineBinaryFilterFields},
                          ${multiValuedFiltersFields}
                    };
                    $scope.processing = true;
                    var file = $files[0];
                    ${document}API.upload(authInfo.token, ${document}Obj, file)
                            .progress(function (evt) {
                                $scope.progress = parseInt(100.0 * evt.loaded / evt.total);
                            })
                            .success(function (res) {
                                ${textFieldsSetValues}
                                $scope.processing = false;
                            })
                            .error(function (err) {
                                $scope.error = err;
                                $scope.processing = false;
                                alert('Error uploading file ' + err + '\nSee log');
                            });
                };
        }])
        .controller('${document}ViewCtrl', ['$scope', '$routeParams', '$q', '$location', 'authInfo', '${document}API', 'newspapersAPI', 'batchesAPI', 'filesAPI', function ($scope, $routeParams, $q, $location, authInfo, ${document}API, newspapersAPI, batchesAPI, filesAPI) {
        // Represents the loading state
        $scope.loaded = false;
                $scope.requestFailed = false;
                $scope.filters = [
                        ${binaryFilters}
                ];
                ${document}API.find(authInfo.token, $routeParams.doc_id)
                .success(function (res) {
                ${textFieldsSetValues}
                ${setDateFieldsFromRes}
                $if(binaryFiltersSetValue)${binaryFiltersSetValue}
                $scope.loaded = true;
                        $scope.requestFailed = false;
                        if (res.${codeColumn} !== null) {
                            $scope.UserCode = res.${codeColumn};
                        } else {
                            ${document}API.getCode(authInfo.token, $routeParams.doc_id)
                            .success(function (res2) {
                            $scope.UserCode = res2.Code;
                        })
                        .error(function (err) {
                        alert('Unable to get user policy code for ' + 
                        $routeParams.doc_id + '\n' + err + '\nSee log');
                        });
                }
                ${multiValuedFiltersValueJs}
                })
                .error(function(err) {
                            ('Unableto get document ' + $routeParams.doc_id 
                            + '\n' + err + '\nSee log');
                        });
                $scope.processing = false;
                ${dateFieldFunctions}
                ${typeAheadFieldsJs}
                $scope.loaded=true;
                $scope.create = function () {
                $scope.processing = true;
                        var ${document}Obj = {
                ID: $routeParams.doc_id,
                ${textFields},
                ${dateFields},
                $if(defineBinaryFilterFields)${defineBinaryFilterFields},
                ${multiValuedFiltersFields}
                };
                        ${document}API.update(authInfo.token, ${document}Obj)
                        .success(function (res) {
                        $scope.processing = false;
                        if (typeof($scope.UserCode) !== 'undefined') { 
                            ${document}API.addCode(authInfo.token, $routeParams.doc_id, $routeParams.batch_id, $scope.UserCode)
                            .success(function (res) {
                            // OK, do nothing
                            })
                            .error(function (res) {
                            alert('Error adding code ' + res + '\nSee log');
                            });
                        }
                        if (typeof($routeParams.batch_id) !== 'undefined') {
                            $location.path('/assignments/' + $routeParams.batch_id + '/view/${tableName}');
                        } else {
                            $location.path('/documents/${document}');
                        }
                        })
                        .error(function (err) {
                        alert('Error updating document ' + err + '\nSee log');
                                $scope.processing = false;
                        })
                        ;
                };
                $scope.convertBoolToInt = function (num) {
                return (num) ? 1 : 0;
                };
                $scope.onFileSelect = function ($files) {
                     var ${document}Obj = {
                          ${textFields},
                          ${dateFields},
                          $if(defineBinaryFilterFields)${defineBinaryFilterFields},
                          ${multiValuedFiltersFields}
                    };
                    $scope.processing = true;
                    var file = $files[0];
                    ${document}API.upload(authInfo.token, ${document}Obj, file)
                            .progress(function (evt) {
                                $scope.progress = parseInt(100.0 * evt.loaded / evt.total);
                            })
                            .success(function (res) {
                                ${textFieldsSetValues}
                                $scope.processing = false;
                            })
                            .error(function (err) {
                                $scope.error = err;
                                $scope.processing = false;
                                alert('Error uploading file ' + err + '\nSee log');
                            });
                };
        }]);
