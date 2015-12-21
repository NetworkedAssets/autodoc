angular.module("DoC").controller('classDiagramCtrl',function($scope,urlProvider) {
    var cd = new ClassDiagram({
        elem: $("#doc_classDiagram_paper")
    });
    cd.load(urlProvider.getRestUrl("/UML/all/"));

    //$(window).on("resize.ClassDiagram",function() {
    //    cd.setDimensions($(window).width(),$(window).height());
    //});
});