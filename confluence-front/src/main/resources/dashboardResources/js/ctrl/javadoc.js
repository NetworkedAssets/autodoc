/**
 * Created by Jakub on 24/11/15.
 */

angular.module("DoC")
.controller("javadocCtrl",function($http,$state,restPath) {
    var vm = this;

    var parsePackageList = function(packageList) {
        var packages = {children:{}};
        //packageList = packageList.sort(function(a,b) {
        //    if (a.name < b.name)
        //        return -1;
        //    if (a.name > b.name)
        //        return 1;
        //    return 0;
        //});
        angular.forEach(packageList,function(pack,key) {
            if (!pack.name || pack.name == "") {
                pack.name = "(default)";
                return true;
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
                    if (pack.name === "com.networkedassets.autodoc.transformer.util.javadoc") {
                        console.log(value,currentPackage.children[value],packages);
                    }
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
            });

            angular.forEach(pack.indexInterface,function(interf) {
                currentPackage.children[interf.name] = {
                    name: interf.qualified,
                    type: "interface"
                };
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


        if (rootPackage) {
            var obj = {};
            obj[rootPackage.name] = rootPackage;
            vm.items = obj;
        } else {
            vm.items = packages.children;
        }


    }

    $http.get(restPath.get('JAVADOC/index')).then(function(data) {
        console.log(data.data.indexPackage);
        //vm.items = data.data.entities.package;
        parsePackageList(data.data.indexPackage);
    });
    vm.items = [];
    vm.toggleItem = function(item) {
        item.expanded = !item.expanded;
    };

    vm.expandItem = function(item) {
        item.expanded = true;
    };

    vm.go = function(item) {
        $state.go("javadoc.entity",{
            name: item.name
        });
        vm.expandItem(item);
    }

    vm.getMenuItemPartialPath = function() {
        //console.log("partial requested!",doc_resourcePath+"partials/javadoc.menuitem.html");
        return doc_resourcePath+"partials/javadoc.menuitem.html";
    }

});