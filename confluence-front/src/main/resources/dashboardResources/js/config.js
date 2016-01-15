angular.module('DoC').config(function($stateProvider,$urlRouterProvider) {
// For any unmatched url, redirect to /state1
        $urlRouterProvider.otherwise("/javadoc");

        // Now set up the states
        $stateProvider
            .state('javadoc', {
                url: "/javadoc",
                name: "Javadoc",
                displayName: "Javadoc",
                templateUrl: doc_resourcePath + "partials/javadoc.html",
                controller: "javadocCtrl as vm"
            })
            .state('javadoc.entity', {
                url: "/entity/{name}",
                templateUrl: doc_resourcePath + "partials/javadoc.entity.html?",
                controller: "javadocEntityCtrl as vm"
            })
            .state('classDiagram', {
                url: "/classDiagram",
                displayName: "ClassDiagram",
                templateUrl: function() {
                    return doc_resourcePath + "partials/classDiagram.html";
                },
                controller: 'classDiagramCtrl'
            });
    })

    .run(function ($state,$rootScope,$injector) {
        $rootScope.$state = $state;
        debugInjector = $injector;
    });