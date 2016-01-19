var doc_resourcePath = "";

if (AJS.Data) {
    doc_resourcePath = AJS.Data.get("base-url")+"/" + AJS.Meta.get("dashboardResourcePath");
} else {
    var arr = window.location.pathname.split("/");
    arr.splice(arr.length-1,1);
    doc_resourcePath = window.location.origin+arr.join("/")+"/";
}


angular.module('DoC',['ui.router','ngSanitize','treeControl']);