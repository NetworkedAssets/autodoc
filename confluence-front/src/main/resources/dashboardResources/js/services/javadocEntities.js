angular.module("DoC").factory('javadocEntities',function($rootScope) {

    var tree;

    var map;

    var treeToMap = function(obj) {
        if (!obj) {
            map = {};
            obj = tree;
        }
        if (typeof obj.children == "object") {
            angular.forEach(obj.children,function(child) {

                map[child.name] = child;
                treeToMap(child);
            });
        }
    };

    var ready = false;
    var javadocEntities = {
        parse: function(packageList) {
            var packages = {children:{}};
            angular.forEach(packageList,function(pack) {
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
                            children: {}
                        };
                        if (currentPackage.name) {
                            currentPackage.children[value].name = currentPackage.name+'.'+value;
                        } else {
                            currentPackage.children[value].name = value;
                        }

                    }
                    currentPackage = currentPackage.children[value];
                });
                if (typeof currentPackage.children == "undefined") {
                    currentPackage.children = {};
                }
                pack.type = "package";



                pack.indexClass.sort(function(a,b) {
                    /* needed for class/interface nesting below */
                    if (a.name > b.name) {
                        return 1;
                    }
                    if (a.name< b.name) {
                        return -1;
                    }
                    return 0;
                });

                var currentEntity;
                var lastEntity;
                var lastLevel = 1;

                angular.forEach(pack.indexClass,function(cl) {
                    var arr = cl.name.split(".");
                    var name = arr[arr.length-1];
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
                        type: cl.type
                    };

                    lastLevel = arr.length;
                    lastEntity = currentEntity.children[name];
                });
            });

            var rootPackage = null;
            var obj;
            var joinPackages = function(current) {
                var i = 0;

                angular.forEach(current.children,function(value,key) {
                    obj = value;
                    objKey = key;
                    i++;
                });
                if (i == 1) {
                    rootPackage = obj;
                    joinPackages(obj);
                }

            };

            joinPackages(packages);

            //packages = packages.children;

            this.setTree(packages);

            this.setReady(true);

            return tree.children;
        },
        getTree: function() {
            return tree;
        },
        getMap: function() {
            return map;
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
        }
    };
    javadocEntities.clear();
    return javadocEntities;
});