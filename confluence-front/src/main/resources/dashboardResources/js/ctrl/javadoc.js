angular.module("DoC")
.controller("javadocCtrl",function($http,$state,$element,urlProvider,javadocEntities) {
    var vm = this;
    vm.loading = true;

    var initSpinner = function() {
        AJS.$($element.find(".loadingSpinner")).spin("big");
    };

    $http.get(urlProvider.getRestUrl('/JAVADOC/index'),{
        cache: true
    }).then(function(data) {
        vm.items = javadocEntities.parse(data.data.indexPackage);
        vm.loading = false;
    });

    vm.items = [];
    vm.toggleItem = function(item) {
        item.expanded = !item.expanded;
    };

    vm.expandItem = function(item) {
        item.expanded = true;
    };

    vm.isExpanded = function(item) {
        var active = false;
        if (typeof $state.params.name == "string") {
            active = $state.params.name.match(item.name) !== null;
        }

        return (active) || item.expanded;
    };

    vm.go = function(item) {
        vm.expandItem(item);
        if (item.type == "package") {
            return false;
        }
        $state.go("javadoc.entity",{
            name: item.name
        });
    };

    vm.isActive = function(item) {
        return item.name===$state.params.name;
    };

    vm.getMenuItemPartialPath = function() {
        return doc_resourcePath+"partials/javadoc.menuitem.html";
    };

    initSpinner();

});