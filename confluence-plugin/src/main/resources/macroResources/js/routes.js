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
            url: "/structureGraph",
            displayName: "Structure Graph",
            template: '' +
            '<div class="aui-message aui-message-error" ng-show="error">' +
            '   <p class="title">' +
            '       <strong>An error occurred</strong>' +
            '   </p>' +
            '   <p>Try reloading the page.</p>' +
            '</div>' +
            '<div id="doc_structureGraph" ng-hide="error"></div>',
            controller: 'StructureGraphCtrl'
        });

    $urlRouterProvider.otherwise("/javadoc");
});