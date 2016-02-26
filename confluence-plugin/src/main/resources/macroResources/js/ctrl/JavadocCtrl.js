angular.module("DoC")
    .controller("JavadocCtrl", function($scope, $http, $state,
                                        $stateParams, $element, $timeout,
                                        $rootScope, urlService, javadocEntities) {
        var vm = this;
        vm.loading = true;

        vm.resourcesUrl = urlService.getResourcesUrl();

        var initSpinner = function() {
            AJS.$($element.find(".loadingSpinner")).spin("big");
        };

        javadocEntities
            .fetch()
            .then(function() {
                vm.items = javadocEntities.getTreeUsingArrays().children;
                vm.loading = false;
                vm.error = false;
            }, function() {
                vm.loading = false;
                vm.error = true;
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
            onSelection: function(node) {
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
                if (name) {
                    vm.tree.updateSelectedNode(javadocEntities.getByName(name));
                }
            },
            updateExpandedRecursively: function(node) {
                var arr = node.name.split(".");
                arr.pop();
                if (arr.length > 0) {
                    this.updateExpandedRecursively(javadocEntities.getByName(arr.join(".")));
                }
                this.expandedNodes.push(node);
                if (this.expandedNodes.length > 16) {
                    this.expandedNodes = this.expandedNodes.reduce(function(a, b) {
                        if (a.indexOf(b) < 0)a.push(b);
                        return a;
                    }, []);
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

        var onStateChangeStart = $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
            if (toState.name == "javadoc" && (!toParams || !toParams.name) && fromParams && fromParams.name) {
                /* TODO Rewrite to something less crappy */
                event.preventDefault();
                $state.go("javadoc.entity", fromParams);
            }
        });

        var onStateChangeSuccess = $rootScope.$on('$stateChangeSuccess',
            function(event, toState, toParams) {
                $timeout(function() {
                    vm.tree.updateSelectedNodeByName(toParams.name);
                    window.scrollTo(0, 0);
                }, 50);jira
            });

        $scope.$on('$destroy', function() {
            onStateChangeStart();
            onStateChangeSuccess();
        });

        if ($state.params.name) {
            // TODO is concurring with $stateChangeSuccess
            $rootScope.$on("javadocEntities.ready", function() {
                vm.tree.updateSelectedNodeByName($state.params.name);
            });
        }

        vm.go = function(item) {
            $state.go("javadoc.entity", {
                name: item.name
            });
        };

        vm.isActive = function(item) {
            return item.name === $state.params.name;
        };

        var initSearch = function() {
            var select = $element.find(".javadoc_container input");
            AJS.$(select).auiSelect2({
                id: function(e) {
                    return e;
                },
                ajax: {
                    url: urlService.getRestUrl("javadoc", "search"),
                    dataType: 'json',
                    quietMillis: 250,
                    data: function(term) {
                        return {
                            q: term
                        };
                    },
                    results: function(data) {
                        return data;
                    }
                },
                formatResult: function(result) {
                    return '<span>' + qName(result) + '</span><br><span style="font-size: 14px; opacity:0.5">' + result + '</span>';
                },
                minimumInputLength: 3,
                multiple: true
            });
            select.on("select2-selecting", function(event) {
                $state.go("javadoc.entity", {
                    name: event.val
                });
                select.select2("close");
                event.preventDefault();
            });
        };

        initSearch();

        initSpinner();

    });