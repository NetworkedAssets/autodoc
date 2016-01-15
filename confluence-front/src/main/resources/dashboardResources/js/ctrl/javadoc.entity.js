angular.module("DoC").controller("javadocEntityCtrl",function($scope,$http,$sanitize,$stateParams,$rootScope,$timeout,$element,urlProvider,javadocEntities) {
    var vm = this;
    vm.loading = true;

    /*
     * TODO Entity refactor into 'class'
     * */

    var parseModifiers = function(entity) {
        if (entity.modifier) {
            entity.modifiers = entity.modifier;
        } else {
            entity.modifiers = [];
        }
    };

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




    };

    var parseEntityFromJson = function(entity) {
        parseModifiers(entity);

        var qualified = entity.qualified?entity.qualified:entity.name;
        entity.package = qualified.split(".");

        entity.package.pop();

        /*entity.packages = [];
        entity.package.forEach(function(package) {
            entity.packages.push({
                name: package
            });
        });*/

        entity.elements = [
            {
                type: "constructor",
                elements: (typeof entity.constructor=="object")?entity.constructor:[]
            },
            {
                type: "field",
                elements: entity.field?entity.field:[]
            },
            {
                type: "method",
                elements: entity.method?entity.method:[]
            }
        ];

        if (entity.elements) {
            entity.elements.forEach(function(elementGroup) {
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

        entity.supers = [];
        if (typeof entity.class == "object") {
            entity.supers = [entity.class];
        }
        entity.interfaces = [];
        if (entity.interface) {
            entity.interfaces = entity.interface;
        }

        entity.classes = [];
        if (angular.isArray(entity.indexClass)) {
            entity.classes = entity.indexClass;
        }

        entity.packages = [];
        if (entity.children) {
            angular.forEach(entity.children,function(child) {
                if (child.type == "package") {
                    entity.packages.push(child);
                } else {
                    entity.classes.push(child);
                }
            });
        }

        return entity;
    };

    var initSpinner = function() {
        AJS.$($element.find(".loadingSpinner")).spin("big");
    };

    vm.toggleDetails = function(method) {
        method.details.visible = !method.details.visible;
    };

    var init = function() {
        initSpinner();
        if (javadocEntities.isPackage($stateParams.name)) {
            console.log(javadocEntities.getCopyByName($stateParams.name));
            vm.entity = parseEntityFromJson(javadocEntities.getCopyByName($stateParams.name));
            vm.loading = false;
        } else {
            vm.loading = true;
            $http.get(urlProvider.getRestUrl("/JAVADOC/"+$stateParams.name)).then(function(response) {
                vm.entity = parseEntityFromJson(response.data);
                vm.loading = false;
            });
        }
    };

    if (javadocEntities.isReady()) {
        init();
    } else {
        $rootScope.$on("javadocEntities.ready",function() {
            init();
        });
    }



});