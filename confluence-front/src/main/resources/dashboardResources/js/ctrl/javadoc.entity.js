/**
 * Created by Jakub on 24/11/15.
 */

angular.module("DoC").controller("javadocEntityCtrl",function($scope,$http,$sanitize,$stateParams,restPath,javadocEntities) {

    var vm = this;

    /*
    * TODO Entity refactor into 'class'
    * */

    var parseModifiers = function(entity) {
        if (entity.modifier) {
            entity.modifiers = entity.modifier;
        } else {
            entity.modifiers = [];
        }
    }

    var parseDetails = function(entity) {
        entity.details = {
            tags: [],
            visible: false,
            exist: false
        };

        if (entity.tag) {
            entity.details.tags = entity.tag;
            entity.details.exist = true;
        }




    }

    var parseEntityFromJson = function(entity) {
        parseModifiers(entity);

        entity.package = entity.qualified.split(".");

        entity.package.pop();
        return entity;
    }

    vm.toggleDetails = function(method) {
        method.details.visible = !method.details.visible;
    };

    vm.entity = {};


    $http.get(restPath.get("JAVADOC/"+$stateParams.name)).then(function(data) {


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
                elements: (typeof vm.entity.constructor=="object")?vm.entity.constructor:[]
            },
            {
                type: "field",
                elements: vm.entity.field?vm.entity.field:[]
            },
            {
                type: "method",
                elements: vm.entity.method?vm.entity.method:[]
            }
        ];

        if (vm.entity.elements) {
            vm.entity.elements.forEach(function(elementGroup) {
                if (typeof elementGroup.elements == "object") {
                    elementGroup.elements.forEach(function(element) {
                        if (element.return) {
                            element.type = element.return;
                        }

                        if (typeof element.parameter == "undefined" && element.signature) {
                            element.parameter = [];
                        }

                        parseModifiers(element);
                        parseDetails(element);
                    });
                }
            });
        }

        vm.entity.supers = [];
        if (typeof vm.entity.class == "object") {
            vm.entity.supers = [vm.entity.class];
        }
        vm.entity.interfaces = [];
        if (vm.entity.interface) {
            console.log(vm.entity.interface);
            vm.entity.interfaces = vm.entity.interface;
        }

    });


});