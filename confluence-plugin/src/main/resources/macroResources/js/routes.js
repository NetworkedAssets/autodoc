angular.module('DoC').config(function($stateProvider, $urlRouterProvider) {
    $stateProvider
        .state('javadoc', {
            url: "/javadoc",
            name: "Javadoc",
            displayName: "Javadoc",
            templateUrl: doc_resourcePath + "partials/javadoc.html",
            controller: "JavadocCtrl as vm"
        })
        .state('javadoc.entity', {
            url: "/entity/{name}/{elementType}/{elementName}",
            params: {
                name: "",
                elementType: "",
                elementName: ""
            },
            templateUrl: doc_resourcePath + "partials/javadoc.entity.html?",
            controller: "JavadocEntityCtrl as vm"
        })
        .state('classDiagram', {
            url: "/classDiagram",
            displayName: "Class Diagram",
            templateUrl: function() {
                return doc_resourcePath + "partials/classDiagram.html";
            },
            controller: function() {
            }
        })
        .state('structureGraph', {
            template: '' +
            '<div class="aui-message aui-message-error" ng-show="error">' +
            '   <p class="title">' +
            '       <strong>An error occurred</strong>' +
            '   </p>' +
            '   <p>Try reloading the page.</p>' +
            '</div>' +
            '<div id="doc_structureGraphProgressbar" class="aui-progress-indicator" ng-show="loading"><span class="aui-progress-indicator-value"></span></div>' +
            '<div class="aui-message aui-message-info" id="doc_structureGraphTakesLongerThanUsual" ng-show="loadingTakesLonger">' +
                '<p class="title">' +
                    '<strong>It takes longer than usual...</strong>' +
                '</p>' +
                '<p>The structure diagram can be much less efficient for large projects.</p>' +
            '</div>' +
            '<div id="doc_structureGraph" ng-class="{\'loading\': loading}" ng-hide="error"></div>',
            url: "/structureGraph",
            displayName: "Structure Graph",
            controller: 'StructureGraphCtrl'
        });

    $urlRouterProvider.otherwise("/javadoc");
});