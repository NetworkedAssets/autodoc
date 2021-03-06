angular.module("DoC").factory('javadocEntities', function($rootScope, $http, $q, urlService) {

    var tree;

    var map;

    var treeToMap = function(obj) {
        if (!obj) {
            map = {};
            obj = tree;
        }
        if (typeof obj.children == "object") {
            angular.forEach(obj.children, function(child) {
                map[child.name] = child;
                treeToMap(child);
            });
        }
    };

    var convertToArrays = function(tree) {
        var arr = [];
        objectToArray(tree.children, arr);
        return arr;
    };

    var objectToArray = function(obj, arr) {
        angular.forEach(obj, function(value) {
            var innerArr = [];
            if (value.children) {
                objectToArray(value.children, innerArr);
                innerArr.sort(function(a, b) {
                    if (a.type === b.type) {
                        if (a.name === b.name) {
                            return 0;
                        } else if (a.name > b.name) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else {
                        if (a.type === b.type) {
                            return 0;
                        } else if (a.type > b.type) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });
                value.children = innerArr;
            }
            arr.push(value);
        });
    };

    var ready = false;
    var javadocEntities = {
        fetch: function() {
            return $q(function(resolve, reject) {
                if (!angular.isUndefined(tree)) {
                    $http.get(urlService.getRestUrl('javadoc', 'index'), {
                        cache: true
                    }).then(function(data) {
                        javadocEntities.parse(data.data.indexPackage);
                        resolve();
                    }, function() {
                        reject();
                    });
                } else {
                    resolve();
                }
            });
        },
        parse: function(packageList) {
            var packages = {children: {}, level: 0};
            angular.forEach(packageList, function(pack) {
                if (!pack.name || pack.name == "") {
                    pack.name = "(default)";
                    return true;
                }
                if (!pack.qualified) {
                    pack.qualified = pack.name;
                }
                var arr = pack.name.split(".");
                var currentPackage = packages;
                arr.forEach(function(value) {
                    if (typeof currentPackage.children[value] == "undefined") {
                        currentPackage.children[value] = {
                            name: value,
                            type: "package",
                            children: {},
                            level: 0
                        };
                        if (currentPackage.name) {
                            currentPackage.children[value].name = currentPackage.name + '.' + value;
                        } else {
                            currentPackage.children[value].name = value;
                        }
                        currentPackage.children[value].level = currentPackage.level + 1;
                    }
                    currentPackage = currentPackage.children[value];
                });
                if (typeof currentPackage.children == "undefined") {
                    currentPackage.children = {};
                }
                pack.type = "package";


                pack.indexClass.sort(function(a, b) {
                    /* needed for class/interface nesting below */
                    if (a.name > b.name) {
                        return 1;
                    }
                    if (a.name < b.name) {
                        return -1;
                    }
                    return 0;
                });

                var currentEntity;
                var lastEntity;
                var lastLevel = 1;

                angular.forEach(pack.indexClass, function(cl) {
                    var arr = cl.name.split(".");
                    var name = arr[arr.length - 1];
                    if (arr.length === 1) {
                        currentEntity = currentPackage;
                    } else {
                        if (arr.length > lastLevel) {
                            currentEntity = lastEntity;
                        }
                    }
                    if (typeof currentEntity.children == "undefined") {
                        currentEntity.children = {};
                    }
                    currentEntity.children[name] = {
                        name: cl.qualified,
                        type: cl.type,
                        level: currentEntity.level + 1
                    };

                    lastLevel = arr.length;
                    lastEntity = currentEntity.children[name];
                });
            });

            this.setTree({
                name: "root",
                children: convertToArrays(packages)
            });

            this.joinPackages();

            this.setReady(true);
        },
        joinPackages: function() {
            var pack = tree;
            for (var i = 0; i < 10; i++) {
                if (pack.children && pack.children.length === 1) {
                    pack = pack.children[0];
                } else {
                    break;
                }
            }
            tree = pack;
        },
        getTree: function() {
            return tree;
        },
        getTreeUsingArrays: function() {
            return this.getTree();
        },
        push: function(entity) {
            map[entity.qualified] = entity;
        },
        clear: function() {
            map = {};
            tree = {};
        },
        setTree: function(newTree) {
            tree = newTree;
            treeToMap();
        },
        existsByName: function(name) {
            return typeof map[name] != "undefined";
        },
        isPackage: function(name) {
            return map[name] && (map[name].type === "package");
        },
        getByName: function(name) {
            return map[name];
        },
        getCopyByName: function(name) {
            return angular.copy(this.getByName(name));
        },
        setReady: function(bool) {
            if (bool) {
                ready = true;
                $rootScope.$broadcast("javadocEntities.ready");
            } else {
                ready = false;
            }
        },
        isReady: function() {
            return ready;
        },
        asMap: function() {
            return map;
        }
    };
    javadocEntities.clear();
    return javadocEntities;
});