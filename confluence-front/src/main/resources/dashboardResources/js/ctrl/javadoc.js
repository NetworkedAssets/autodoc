/**
 * Created by Jakub on 24/11/15.
 */

angular.module("DoC")
.controller("javadocCtrl",function($http,$state,urlProvider,javadocEntities,$timeout) {
    var vm = this;

    var parsePackageList = function(packageList) {
        var packages = {children:{}};
        angular.forEach(packageList,function(pack,key) {
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

            javadocEntities.push(pack);

            angular.forEach(pack.indexClass,function(cl) {
                //var name = cl.qualified.replace(pack.name);
                //var arr = name.split(".");
                //arr.forEach(function(value) {
                //    if (typeof currentClass[value] == "undefined") {
                //        currentClass[value] = {};
                //    }
                //    currentClass = currentClass[value];
                //});
                currentPackage.children[cl.name] = {
                    name: cl.qualified,
                    type: cl.type
                };
                javadocEntities.push(cl);
            });

            angular.forEach(pack.indexInterface,function(interf) {
                currentPackage.children[interf.name] = {
                    name: interf.qualified,
                    type: "interface"
                };
                javadocEntities.push(interf);
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

        }

        joinPackages(packages);

        //packages = packages.children;

        javadocEntities.setTree(packages);

        if (rootPackage) {
            var obj = {};
            obj[rootPackage.name] = rootPackage;
            vm.items = obj;
        } else {
            vm.items = packages.children;
        }


    }

    $http.get(urlProvider.getRestUrl('/JAVADOC/index'),{
        cache: true
    }).then(function(data) {
        parsePackageList(data.data.indexPackage);
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
    }

    vm.go = function(item) {
        vm.expandItem(item);
        console.log(item);
        if (item.type == "package") {
            return false;
        }
        $state.go("javadoc.entity",{
            name: item.name
        });
    }

    vm.isActive = function(item) {
        return item.name===$state.params.name;
    }

    vm.getMenuItemPartialPath = function() {
        //console.log("partial requested!",doc_resourcePath+"partials/javadoc.menuitem.html");
        return doc_resourcePath+"partials/javadoc.menuitem.html";
    }

});