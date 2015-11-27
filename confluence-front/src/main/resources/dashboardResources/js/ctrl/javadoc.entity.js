/**
 * Created by Jakub on 24/11/15.
 */

angular.module("DoC").controller("javadocEntityCtrl",function($scope,$http,$sanitize,$stateParams) {

    var vm = this;

    var parseModifiers = function(entity) {
        entity.modifiers = [];

        ["final","volatile","static","abstract","synchronized"].forEach(function(modifier) {
            if (entity[modifier]) {
                entity.modifiers.push(modifier);
            }
        });

        if (entity.scope) {
            entity.modifiers.push(entity.scope);
        }
    }

    var parseEntityFromJson = function(entity) {
        parseModifiers(entity);

        entity.package = entity.qualified.split(".");

        entity.package.pop();
        return entity;
    }

    vm.toggleDetails = function(method) {
        method.detailsVisible = !method.detailsVisible;
    };

    vm.entity = {};

    console.log($stateParams.name);

    $http.get('na_single2.json').then(function(data) {


        vm.entity = parseEntityFromJson(data.data);

        vm.entity.packages = [];
        vm.entity.package.forEach(function(package) {
            vm.entity.packages.push({
                name: package
            });
        });

        vm.entity.elements = [
            {
                type: "constructor",
                elements: vm.entity.constructor
            },
            {
                type: "field",
                elements: vm.entity.field
            },
            {
                type: "method",
                elements: vm.entity.method
            }
        ];

        if (vm.entity.elements) {
            vm.entity.elements.forEach(function(elementGroup) {
                if (elementGroup.elements) {
                    elementGroup.elements.forEach(function(element) {
                        if (element.return) {
                            element.type = element.return;
                        }

                        if (typeof element.parameter == "undefined" && element.signature) {
                            element.parameter = [];
                        }

                        parseModifiers(element);

                        element.detailsSafe = $sanitize(element.details);
                    });
                }
            });
        }

    });
});