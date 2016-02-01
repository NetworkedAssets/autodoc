angular.module("DoC").controller("JavadocEntityCtrl",function($scope,$http,$state,$sanitize,
                                                              $filter,$stateParams,$rootScope,
                                                              $timeout,$element,urlService,javadocEntities) {
    var vm = this;
    vm.loading = true;

    var initSpinner = function() {
        AJS.$($element.find(".loadingSpinner")).spin("big");
    };

    vm.toggleDetails = function(method) {
        method.details.visible = !method.details.visible;
    };

    vm.packages = [];

    var parseBreadcrumb = function() {
        vm.packages = [];
        var qualified = "";
        angular.forEach(vm.entity.packageArray,function(pack) {
            if (!qualified) {
                qualified = pack;
            } else {
                qualified += "."+pack;
            }
            vm.packages.push({
                name: pack,
                qualified: qualified
            });
        });
        vm.packages.push({
            name: vm.entity.name,
            qualified: qualified+"."+name
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
            $http.get(urlService.getRestUrl("/JAVADOC/"+$stateParams.name)).then(function(response) {
                vm.entity = new JavadocEntity(response.data);
                parseBreadcrumb();
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