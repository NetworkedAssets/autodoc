angular.module("DoC_Config").directive("docAuiDatePicker",function() {
    return function link(scope,element,attrs) {
        AJS.$(element).datePicker();
    }
});