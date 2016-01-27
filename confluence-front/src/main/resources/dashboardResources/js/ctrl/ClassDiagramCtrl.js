angular.module("DoC").controller('ClassDiagramCtrl',function($scope,$http,$element,$timeout,urlProvider) {
    $scope.loading = true;
    var cd = new ClassDiagram({
        elem: $("#doc_classDiagram_paper")
    });

    var initSpinner = function() {
        AJS.$($element.find(".loadingSpinner")).spin("big");
    };

    initSpinner();

    $http.get(urlProvider.getRestUrl("/UML/all/"),{
        cache: true
    })
        .then(function(response) {
            cd.generate(response.data);
            $scope.loading = false;
        });
});