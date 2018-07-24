<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en" ng-app="ppdpapp.v2">
    <head>
        <!-- General info -->
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>PPDPAPP.V2</title>

        <!-- API info -->
        <link id="apiroot" href="api/" />

        <!-- CSS files -->
        <link rel="stylesheet" href="css/styles.css" />
        <link rel="stylesheet" href="libs/font-awesome-4.7.0/css/font-awesome.min.css" />
        <link rel="stylesheet" href="node_modules/angular-ui-grid/ui-grid.css" />

        <!-- Angular dependencies -->
        <script src="libs/angularfileupload/angular-file-upload-html5-shim.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular-route.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular-animate.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular-cookies.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.26/angular-touch.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.11.2/ui-bootstrap.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.11.2/ui-bootstrap-tpls.min.js"></script>
        <script src="libs/angularfileupload/angular-file-upload.min.js"></script>
        <script src="node_modules/angular-ui-grid/ui-grid.js"></script>
        <script src="node_modules/angular-sanitize/angular-sanitize.min.js"></script>
        <script src="libs/angularjs/angular-base64.min.js"></script>
        
    </head>
    <body ng-controller="appCtrl">
        <!-- Header -->
        <nav class="navbar navbar-default navbar-fixed-top navbar-cherry" role="navigation">
            <!-- Mobile version -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#main-navbar">
                    <span class="sr-only">Toggle Navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="#/"><img src="images/logo_temple.png" alt="TU" style="height: 16px; margin-top: 3px;" /></a>
            </div>
            <!-- Desktop version -->
            <div class="collapse navbar-collapse" id="main-navbar">
                <div class="navbar-right" style="margin-right: 15px;" ng-show="loggedIn()">
                    <p class="navbar-text">Signed in as {{nameOfUser()}}</p>
                    <button type="button" class="btn btn-default navbar-btn" ng-click="logOut()">Log Out</button>
                </div>
            </div>
        </nav>

        <!-- Main -->
        <ul id="navigation" ng-class="{hidden: isNavActive('/login')}">
            <li ng-class="{active: isNavActive('/assignments'), hidden: !isNavAllowed('/assignments')}"><a href="#/assignments"><i class="fa fa-refresh"></i> Assignments</a></li>
            <li ng-class="{active: isNavActive('/documents'), hidden: !isNavAllowed('/documents')}" class="group"><a href="#/documents"><i class="fa fa-archive"></i> Documents</a></li>
            <c:forEach var="table" items="${tables}">
            <li ng-class ="{active: isNavActive('/documents/${table.documentName}'), hidden: !isNavAllowed('/documents/${table.documentName}')}" class="indented last"><a href="#/documents/${table.documentName}"><i class="fa fa-newspaper-o"></i> ${table.documentUC}</a></li>
            </c:forEach>
            <li ng-class="{active: isNavActive('/batches'), hidden: !isNavAllowed('/batches')}"><a href="#/batches"><i class="fa fa-folder"></i> Batches</a></li>
            <li ng-class="{active: isNavActive('/files'), hidden: !isNavAllowed('/files')}"><a href="#/files"><i class="fa fa-file"></i> Files</a></li>
            <li ng-class="{active: isNavActive('/users'), hidden: !isNavAllowed('/users')}"><a href="#/users"><i class="fa fa-user"></i> Users</a></li>
            <li ng-class="{active: isNavActive('/admin'), hidden: !isNavAllowed('/admin')}"><a href="#/admin"><i class="fa fa-user-plus"></i> Admin</a></li>
        </ul>
        <div id="content" class="animate fader" ng-class="{expanded: isNavActive('/login')}" ng-view></div>

        <!-- Footer -->
        <!--
                        <footer>  
                                <div id="footer-top">
                                        <div class="container">
                                                <div class="row">
                                                        <div class="col-sm-8 col-md-8" id="footer-left">
                                                                <div>
                                                                        <h1>Temple University</h1>
                                                                        <h2>PA Policy Database Project</h2>
                                                                        <p>
                                                                                College of Liberal Arts<br />
                                                                                1114 Polett Walk, 840 Anderson Hall<br />
                                                                                Philadelphia, PA 19122
                                                                        </p>
                                                                </div>
                                                        </div>
                                                        <div class="hidden-xs col-sm-4 col-md-4" id="footer-right">
                                                                <div>
                                                                        <p><a href="#/"><span class="glyphicon glyphicon-book"></span> Directory</a></p>
                                                                        <p><a href="#/"><span class="glyphicon glyphicon-map-marker"></span> Maps and Directions</a></p>
                                                                        <p><a href="#/"><span class="glyphicon glyphicon-phone-alt"></span> Contact</a></p>
                                                                </div>
                                                        </div>
                                                </div>
                                        </div>
                                </div>
                                <div id="footer-bottom" class="hidden-xs">
                                        <div class="container">
                                                <div class="row"><ul>
                                                        <li><a href="#/">TUPortal</a></li>
                                                        <li><a href="#/">TUMail</a></li>
                                                        <li><a href="#/">Site Map</a></li>
                                                        <li><a href="#/">Accessibility</a></li>
                                                        <li><a href="#/">Policies</a></li>
                                                        <li><a href="#/">Careers At Temple</a></li>
                                                </ul></div>
                                        </div>
                                </div>
                        </footer>
        -->

        <!-- Load third party js files -->
        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>

        <!-- Load angularjs application files -->
        <script src="app/app.js"></script>
        <script src="app/common/directives.js"></script>
        <script src="app/common/factories.js"></script>
        <script src="app/common/filters.js"></script>
        <script src="app/account/account.js"></script>
        <script src="app/account/accountControllers.js"></script>
        <script src="app/account/accountFactory.js"></script>
        <script src="app/assignments/assignments.js"></script>
        <script src="app/assignments/assignmentsControllers.js"></script>
        <script src="app/assignments/assignmentsFactory.js"></script>
        <c:forEach var="table" items="${tables}">
            <script src="app/documents/${table.document}/${table.document}.js"></script>
            <script src="app/documents/${table.document}/${table.document}Controllers.js"></script>
            <script src="app/documents/${table.document}/${table.document}Factory.js"></script>
            <script src="app/documents/${table.document}/${table.document}Filters.js"></script>
        </c:forEach>
        <script src="app/batches/batches.js"></script>
        <script src="app/batches/batchesControllers.js"></script>
        <script src="app/batches/batchesFactory.js"></script>
        <script src="app/users/users.js"></script>
        <script src="app/users/usersControllers.js"></script>
        <script src="app/users/usersFactory.js"></script>
        <script src="app/files/files.js"></script>
        <script src="app/files/filesControllers.js"></script>
        <script src="app/files/filesFactory.js"></script>
        <script src="app/admin/admin.js"></script>
        <script src="app/admin/adminControllers.js"></script>
        <script src="app/admin/adminFactory.js"></script>
    </body>
</html>
