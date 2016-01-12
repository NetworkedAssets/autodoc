angular.module("DoC").controller('classDiagramCtrl',function($scope,$http,urlProvider) {
    var cd = new ClassDiagram({
        elem: $("#doc_classDiagram_paper")
    });
    $http.get(urlProvider.getRestUrl("/UML/all/"),{
        cache: true
    })
        .then(function(response) {
            cd.generate(response.data);
        });
});