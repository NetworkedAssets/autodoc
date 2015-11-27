/**
 * Created by Jakub on 24/11/15.
 */

angular.module("DoC")
.controller("javadocCtrl",function($http,$state) {
    var vm = this;

    $http.get('list.json?').then(function(data) {
        vm.items = data.data.entities.package;
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

});