angular.module("DoC").factory('javadocEntities',function($rootScope) {

    var tree = null;

    var list = {};

    var ready = false;

    return {
        push: function(entity) {
            list[entity.qualified] = entity;
        },
        clear: function() {
            list = {};
        },
        setTree: function(newTree) {
            tree = newTree;
        },
        existsByName: function(name) {
            return typeof list[name] != "undefined";
        },
        isPackage: function(name) {
            console.log(name,list[name]);
            return list[name] && (list[name].type === "package");
        },
        getByName: function(name) {
            return list[name];
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
});