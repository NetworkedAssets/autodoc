/**
 * Created by Jakub on 30/11/15.
 */

var doc_confluencePath = "";

if (AJS.Data) {
    doc_confluencePath = AJS.Data.get("base-url")+"/";
} else {
    var arr = window.location.pathname.split("/");
    arr.splice(arr.length-1,1);
    doc_confluencePath = window.location.origin+arr.join("/")+"/";
}


angular.module("DoC_Config",[]);
angular.element(document).ready(function() {
    $("#doc_config-loading").spin("large");
});