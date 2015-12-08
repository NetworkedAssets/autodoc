angular.module("DoC").controller("topCtrl",function($scope,$state) {
    //var $scope = this;
    $scope.activeState = {
        name: null,
        displayName: null
    };
    $scope.states = {};

    $scope.projects = [
        "Project1",
        "Project2"
    ];

    var i = 0;
    $state.get().forEach(function(state,index) {

        if (state.name.indexOf('.') === -1 && state.displayName) {

            $scope.states[state.name] = {
                name: state.name,
                displayName: state.displayName
            };
            if (i === 0) {
                $scope.activeState = $scope.states[state.name];
            }
            i++;
        }
    });

    $scope.changeState = function(state) {
        console.log(state);
        $scope.activeState = $scope.states[state];
        $state.go(state);
    };
});