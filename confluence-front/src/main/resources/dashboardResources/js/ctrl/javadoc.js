angular.module("DoC")
.controller("javadocCtrl",function($http,$state,$stateParams,$element,$timeout,$rootScope,urlProvider,javadocEntities) {
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
        expandedNodes: [],
        onSelection: function (node) {
            vm.go(node);
            this.expandedNodes.push(node);
            $timeout();
        },
        updateSelectedNode: function(node) {
            if (node != vm.tree.selectedNode) {
                vm.tree.selectedNode = node;
                this.updateExpandedRecursively(node);
            }
        },
        updateSelectedNodeByName: function(name) {
            vm.tree.updateSelectedNode(javadocEntities.getByName(name));
        },
        updateExpandedRecursively: function(node) {
            var arr = node.name.split(".");
            arr.pop();
            if (arr.length > 0) {
                this.updateExpandedRecursively(javadocEntities.getByName(arr.join(".")));
            }
            this.expandedNodes.push(node);
            if (this.expandedNodes.length > 16) {
                this.expandedNodes = this.expandedNodes.reduce(function(a,b){if(a.indexOf(b)<0)a.push(b);return a;},[]);
            }
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

    $rootScope.$on('$stateChangeSuccess',
        function(event, toState, toParams){
            $timeout(function() {
                vm.tree.updateSelectedNodeByName(toParams.name);
            },50);
        });


    if ($state.params.name) {
        $rootScope.$on("javadocEntities.ready",function() {
            vm.tree.updateSelectedNodeByName($state.params.name);
        });
    }

    vm.go = function(item) {
        $state.go("javadoc.entity",{
            name: item.name
        });
    };

    vm.isActive = function(item) {
        return item.name===$state.params.name;
    };

    initSpinner();

});