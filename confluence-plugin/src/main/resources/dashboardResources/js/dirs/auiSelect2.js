angular.module("DoC").directive("auiSelect2",function($compile,$parse,$timeout) {

    return {
        restrict: "A",
        require: 'ngModel',
        link: function(scope,element,attrs,ngModel) {
            var select = $(element);

            scope.$watch(function() {
                return ngModel.$viewValue;
            },function() {
                AJS.$(select).auiSelect2({
                    minimumResultsForSearch: attrs.docDisableSearch?(-1):(undefined)
                });
            });
        },
        scope: {
            model: "=ngModel"
        }
    }
})