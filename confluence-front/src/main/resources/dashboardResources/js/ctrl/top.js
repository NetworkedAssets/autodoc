angular.module("DoC").controller("topCtrl",function($scope,$state) {

    $scope.states = {};

    $scope.projects = [
        "Project1",
        "Project2"
    ];

    var i = 0;

    $state.get().forEach(function(state) {

        if (state.name.indexOf('.') === -1 && state.displayName) {

            $scope.states[state.name] = {
                value: state.name,
                label: state.displayName
            };
            if (i === 0) {
                $scope.chosenState = $scope.states[state.name];
            }
            i++;
        }
    });

    $scope.$watch('$state.current.name', function(newValue) {
        var rootState;
        if (newValue && (newValue.indexOf('.') >= 0)) {
            rootState = newValue.split(".")[0];
        } else {
            rootState = newValue;
        }

        if (rootState) {
            $scope.chosenState = $scope.states[rootState];
        }
    },true);

    $scope.$watch("chosenState",function(newValue,oldValue) {
        console.log(newValue);
        if (newValue != oldValue && newValue) {
            $state.go(newValue.value);
        }

    },true);

});