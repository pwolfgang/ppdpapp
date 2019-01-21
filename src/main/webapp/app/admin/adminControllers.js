/* 
 * Copyright (c) 2018, Temple University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * All advertising materials features or use of this software must display 
 *   the following  acknowledgement
 *   This product includes software developed by Temple University
 * * Neither the name of the copyright holder nor the names of its 
 *   contributors may be used to endorse or promote products derived 
 *   from this software without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
var admin = angular.module('adminControllers', ['adminFactory']);


admin.controller('adminCtrl', ['$scope', '$location', 'adminAPI', 'tablesAPI', 'authInfo', 
    function ($scope, $location, adminAPI, tablesAPI, authInfo) {
                $scope.loaded = false;
                $scope.requestFailed = false;

                // call tablesAPI to get table names.
                tablesAPI.getAll(authInfo.token)
                        .success(function (res) {
                            $scope.dataset_type_dd_items = res;
                            $scope.loaded = true;
                            $scope.requestFailed = false;
                        });
                $scope.setDatasetType = function (tableObj) {
                    $scope.dataset_type = tableObj;
                };
                
                $scope.doPublish = function() {
                    $scope.pricessing = true;
                    $scope.success = false;
                    $scope.error = false;
                    adminAPI.publish(authInfo.token, $scope.dataset_type.TableName)
                            .success(function (res) {
                                $scope.processing = false;
                                $scope.success = res;
                            })
                            .error(function (err) {
                                $scope.error = err;
                                $scope.processing = false;                             
                            });
                };
                $scope.doUpdateCodes = function() {
                    $scope.pricessing = true;
                    $scope.error = false;
                    $scope.success = false;
                    adminAPI.update(authInfo.token, $scope.dataset_type.TableName)
                            .success(function (res) {
                                $scope.processing = false;
                                $scope.success = res;
                            })
                            .error(function (err) {
                                $scope.error = err;
                                $scope.processing = false;                             
                            });
                };
    }]);


