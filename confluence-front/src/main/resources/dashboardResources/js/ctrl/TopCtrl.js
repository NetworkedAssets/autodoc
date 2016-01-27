angular.module("DoC").controller("TopCtrl",function($scope,$state,macroParams) {

    $scope.states = {};

    var i = 0;

    $state.get().forEach(function(state) {

        if (state.name.indexOf('.') === -1 && state.displayName && macroParams.get(state.name)) {

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
            if (!$scope.states[rootState]) {
                /* Default state not available, switching to first available */
                angular.forEach($scope.states,function(state) {
                    $state.go(state.value);
                    return false;
                });

            } else {
                $scope.chosenState = $scope.states[rootState];
            }

        }
    },true);

    $scope.$watch("chosenState",function(newValue,oldValue) {
        if (newValue != oldValue && newValue) {
            $state.go(newValue.value);
        }

    },true);

});