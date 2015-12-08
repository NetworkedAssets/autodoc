angular.module("DoC").factory('javadocEntities',function() {
    var entities = {
        list: {},
        push: function(entity) {

        },
        clear: {
            //this.list = {};
        },
        exists: function(name) {
            return typeof this.list[name] != "undefined";
        }
    };

    return entities;
});