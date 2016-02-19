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
                    text: v.label,
                    data: v
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

            var select2Options = {
                minimumResultsForSearch: attrs.docDisableSearch?(-1):(undefined),
            };

            if (typeof scope.formatResult == "function") {
                select2Options.formatResult = scope.formatResult;
            }
            if (typeof scope.formatSelection == "function") {
                select2Options.formatSelection = scope.formatSelection;
            }

            $timeout(function() {

                AJS.$(select).auiSelect2(select2Options);
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
                    select.prepend('<option value=""></option>');
                }
                if (scope.options) {
                    processOptions(scope.options,select)
                }


                AJS.$(select).auiSelect2(select2Options);
            },true);
            scope.$watch(function() {
                return ngModel.$viewValue;
            },function(newValue,oldValue) {
                //console.log(newValue,oldValue);
                if (1 || newValue !== oldValue) {
                    //console.log("setting",newValue,oldValue);
                    var value = newValue;
                    if (typeof value != "string") {
                        value = "";
                    }
                    AJS.$(select).auiSelect2("val",value);
                }

            });

            //select.attr("ng-model",attrs.ngModel);
            //$compile(element)(scope);

        },
        scope: {
            options: "=docOptions",
            model: "=ngModel",
            formatResult: "=docFormatResult",
            formatSelection: "=docFormatSelection"
        }
    }
})