angular.module("DoC").controller("JavadocEntityCtrl", function($scope, $http, $state, $sanitize,
                                                               $filter, $stateParams, $rootScope,
                                                               $timeout, $element, urlService,
                                                               javadocEntities, macroParams) {
    var vm = this;
    vm.loading = true;

    vm.classDiagramEnabled = macroParams.get("classDiagram");

    vm.$stateParams = $stateParams;

    var initSpinner = function() {
        AJS.$($element.find(".loadingSpinner")).spin("big");
    };

    vm.toggleDetails = function(method) {
        method.details.visible = !method.details.visible;
    };

    vm.expanded = {
        classDiagram: vm.$stateParams.elementType === "classDiagram",
        fields: true,
        constants: true,
        constructors: true,
        methods: true,
        toggle: function(item) {
            if (typeof vm.expanded[item] != "undefined") {
                vm.expanded[item] = !vm.expanded[item];
            }
        }
    };

    vm.packages = [];

    var parseBreadcrumb = function() {
        vm.packages = [];
        var qualified = "";
        angular.forEach(vm.entity.packageArray, function(pack) {
            if (!qualified) {
                qualified = pack;
            } else {
                qualified += "." + pack;
            }
            vm.packages.push({
                name: pack,
                qualified: qualified
            });
        });
        vm.packages.push({
            name: vm.entity.name,
            qualified: qualified + "." + name
        });
    };

    var init = function() {
        initSpinner();
        if (javadocEntities.isPackage($stateParams.name)) {
            vm.entity = new JavadocEntity(javadocEntities.getCopyByName($stateParams.name));
            parseBreadcrumb();
            vm.loading = false;
        } else {
            vm.loading = true;
            $http.get(urlService.getRestUrl("javadoc", $stateParams.name)).then(function(response) {
                vm.entity = new JavadocEntity(response.data);
                parseBreadcrumb();
                vm.loading = false;
            }, function(response) {
                vm.loading = false;
                if (response.status == 404) {
                    vm.error = 404;
                } else {
                    vm.error = 500;
                }
            });
        }
    };

    if (javadocEntities.isReady()) {
        init();
    } else {
        $rootScope.$on("javadocEntities.ready", function() {
            init();
        });
    }
});