angular.module("DoC")
.controller("javadocCtrl",function($http,$state,$element,$timeout,urlProvider,javadocEntities) {
    var vm = this;
    vm.loading = true;

    var initSpinner = function() {
        AJS.$($element.find(".loadingSpinner")).spin("big");
    };

    $http.get(urlProvider.getRestUrl('/JAVADOC/index'),{
        cache: true
    }).then(function(data) {
        javadocEntities.parse(data.data.indexPackage);
        vm.items = javadocEntities.getTreeUsingArrays();

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

    vm.tree = {
        q: "",
        expanded: [],
        onSelection: function (node) {
            //console.log(node,vm.tree.expanded);
            vm.go(node);
            this.expanded.push(node);
            $timeout();
        },
        options: {
            nodeChildren: "children",
            dirSelectable: true,
            injectClasses: {
                ul: "a1",
                li: "a2",
                liSelected: "a7",
                iExpanded: "aui-icon aui-icon-small aui-iconfont-arrow-up",
                iCollapsed: "aui-icon aui-icon-small aui-iconfont-arrow-down",
                iLeaf: "a5",
                label: "a6",
                labelSelected: "a8"
            },
            allowDeselect: false
        }
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