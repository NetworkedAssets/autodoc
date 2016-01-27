angular.module('DoC').config(function($stateProvider,$urlRouterProvider) {
// For any unmatched url, redirect to /state1

        // Now set up the states
        $stateProvider
            .state('javadoc', {
                url: "/javadoc",
                name: "Javadoc",
                displayName: "Javadoc",
                templateUrl: doc_resourcePath + "partials/javadoc.html",
                controller: "JavadocCtrl as vm"
            })
            .state('javadoc.entity', {
                url: "/entity/:name",
                templateUrl: doc_resourcePath + "partials/javadoc.entity.html?",
                controller: "JavadocEntityCtrl as vm"
            })
            .state('classDiagram', {
                url: "/classDiagram",
                displayName: "Class Diagram",
                templateUrl: function() {
                    return doc_resourcePath + "partials/classDiagram.html";
                },
                controller: 'ClassDiagramCtrl'
            })
            .state('structureGraph',{
                url: "/structureGraph",
                displayName: "Structure Graph",
                template: '<div id="doc_structureGraph"></div>',
                controller: 'StructureGraphCtrl'
            });

        $urlRouterProvider.otherwise("/javadoc");
    })

    .run(function ($state,$rootScope,$injector) {
        $rootScope.$state = $state;
        debugInjector = $injector;
    });