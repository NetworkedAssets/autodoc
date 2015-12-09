angular.module("DoC").factory('javadocEntities',function() {

    var tree = null;

    var list = {};

    var entities = {
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
        getByName: function(name) {
            return list[name];
        }
    };

    return entities;
});