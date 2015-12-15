angular.module("DoC_Config").directive("auiSelect2",function($compile,$parse,$timeout) {

    var processOptions = function(data,elem) {
        angular.forEach(data,function(v) {
            if (v.isOptGroup) {
                var optGroup =  $('<optgroup>',{
                    label: v.label
                });
                processOptions(v.options,optGroup);

                optGroup.appendTo(elem);
            } else {
                var option = $('<option>',{
                    val: v.value,
                    text: v.label
                });
                option.appendTo(elem);
            }

        });
        return elem;
    }

    return {
        restrict: "A",
        require: 'ngModel',
        link: function(scope,element,attrs,ngModel) {
            var select = $(element);



            /*ngModel.$render = function() {

            }*/

            $timeout(function() {
                AJS.$(select).auiSelect2({
                    minimumResultsForSearch: attrs.docDisableSearch?(-1):(undefined)
                }).on("select2-selecting",function(e) {
                    //scope.model = e.val;
                    ngModel.$setViewValue(e.val);
                    $timeout();
                })
            });
            scope.$watch("options",function() {
                select.empty();
                console.log(scope.options);
                if (attrs.docAllowEmpty && select.find("option:not([value])").length == 0) {
                    select.prepend("<option/>");
                }
                if (scope.options) {
                    processOptions(scope.options,select)
                }


                AJS.$(select).auiSelect2();
            },true);
            scope.$watch(function() {
                return ngModel.$viewValue;
            },function(newValue,oldValue) {
                if (newValue !== oldValue) {
                    console.log("setting",newValue,oldValue);
                    AJS.$(select).auiSelect2("val",ngModel.$viewValue);
                }

            });
        },
        scope: {
            options: "=docOptions",
            model: "=ngModel"
        }
    }
})