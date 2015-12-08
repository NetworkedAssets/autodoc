angular.module("DoC").controller('classDiagramCtrl',function($scope,restPath) {
    var cd = new ClassDiagram({
        elem: $("#doc_classDiagram_paper")
    });
    cd.load(restPath.get("UML/all/"));

    $(window).on("resize.ClassDiagram",function() {
        cd.setDimensions($(window).width(),$(window).height());
    });
});