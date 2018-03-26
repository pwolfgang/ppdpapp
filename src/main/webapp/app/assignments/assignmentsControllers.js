var acct = angular.module('assignmentsControllers', ['assignmentsFactory', 'ui.grid.selection']);

acct.controller('assignmentsCtrl', ['$scope', '$location', 'assignmentsAPI', 'tablesAPI', 'authInfo', function ($scope, $location, assignmentsAPI, tablesAPI, authInfo) {
        // Represents the loading state
        $scope.loaded = false;
        $scope.requestFailed = false;
        // http://ui-grid.info/docs/#/tutorial/205_row_editable
        // http://ui-grid.info/docs/#/tutorial/215_paging
        $scope.gridAssignments = {
            enableSorting: true,
            enableRowSelection: true,
            multiSelect: true,
            columnDefs: [
                {name: 'Created By', field: 'creator'},
                {name: 'Assignment Type', field: 'assignmentDescription'},
                {name: 'Date Added', field: 'dateAdded', cellFilter: 'date:\'mediumDate\''},
                {name: 'Date Due', field: 'dateDue', cellFilter: 'date:\'mediumDate\''},
                {name: 'Assigned To', field: 'users[0].lastName'},
                {name: 'View', cellTemplate: 'app/assignments/partials/cellTemplate_assignments.html'}
            ]
        };

        $scope.refresh = function () {
            $scope.loaded = false;
            $scope.requestFailed = false;
            assignmentsAPI.getAll(authInfo.token)
                    .success(function (res) {
                        $scope.gridAssignments.data = res;
                        $scope.loaded = true;
                        $scope.requestFailed = false;
                    })
                    .error(function () { // when no assignments, was causing an error...
                        $scope.loaded = true;
                        $scope.requestFailed = false;
                    });
        };
        $scope.refresh();

        $scope.loadBatch = function (batchObj) {
                console.log("loadBatch called");
                if (batchObj.assignmentTypeID === 4) {
                    tablesAPI.find(authInfo.token, batchObj.tablesID)
                            .success(function (res) {
                                $location.path('/assignments/' + batchObj.batchID + '/tiebreak/' + res.TableName);
                            });
                } else {
                    tablesAPI.find(authInfo.token, batchObj.tablesID)
                            .success(function (res) {
                                $location.path('/assignments/' + batchObj.batchID + '/view/' + res.TableName);
                            });
                }
            };

        $scope.gridAssignments.onRegisterApi = function (gridApi) {
            $scope.gridApi = gridApi;
            gridApi.selection.on.rowSelectionChanged($scope, function (row) {
                console.log(row.entity.batchID);
            });
        };
    }]);
