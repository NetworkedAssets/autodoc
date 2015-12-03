/**
 * Created by Jakub on 16/11/15.
 */

var doc_resourcePath = AJS.Data.get("base-url")+"/" + AJS.Meta.get("dashboardResourcePath");

angular.module('DoC',['ui.router','ngSanitize']);

angular.module('DoC').config(function($stateProvider,$urlRouterProvider,$sanitizeProvider) {
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
            templateUrl: doc_resourcePath + "partials/classDiagram.html",
            controller: function($scope) {
                var cd = new ClassDiagram({
                    elem: $("#doc_classDiagram_paper")
                });
                cd.load();

                $(window).on("resize.ClassDiagram",function() {
                    cd.setDimensions($(window).width(),$(window).height());
                });
            }
        });
})

    .run(function ($state,$rootScope) {
        $rootScope.$state = $state;
    })