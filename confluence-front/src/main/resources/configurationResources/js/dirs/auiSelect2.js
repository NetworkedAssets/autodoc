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
                var attrs = {
                    val: v.value,
                    text: v.label
                };
                if (v.selected) {
                    attrs.selected = true;
                }
                var option = $('<option>',attrs);

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
                })
                select.on("change",function(e) {
                    //console.log("yep",e);
                    scope.model = e.val;
                    ngModel.$setViewValue(e.val);
                    $timeout();
                })
            });
            scope.$watch("options",function(options) {
                select.empty();
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
                //console.log(newValue,oldValue);
                if (1 || newValue !== oldValue) {
                    //console.log("setting",newValue,oldValue);
                    AJS.$(select).auiSelect2("val",ngModel.$viewValue);
                }

            });

            //select.attr("ng-model",attrs.ngModel);
            //$compile(element)(scope);

        },
        scope: {
            options: "=docOptions",
            model: "=ngModel"
        }
    }
})