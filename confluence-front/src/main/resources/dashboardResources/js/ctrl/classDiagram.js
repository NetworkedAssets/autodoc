angular.module("DoC").controller('classDiagramCtrl',function($scope,urlProvider) {
    var cd = new ClassDiagram({
        elem: $("#doc_classDiagram_paper")
    });
    console.log("again");
    cd.load(urlProvider.getRestUrl("/UML/all/"));
});