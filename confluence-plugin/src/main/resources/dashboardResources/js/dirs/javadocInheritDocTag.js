angular.module("DoC")
    .directive("javadocInheritDocTag", function($http, $stateParams, $compile, urlService) {
        return {
            link: function(scope, element) {
                var arr = $stateParams.name.split(".");
                arr.pop();
                var parentQualified = arr.join(".");
                element.html('<span class="aui-icon aui-icon-wait">Loading...</span>');
                $http
                    .get(urlService.getRestUrl("javadoc", parentQualified, "comment"))
                    .then(function(response) {
                        response.data.comment = "{@code ha!}";
                        element.html('<div javadoc-tags="\'' + response.data.comment + '\'"></div>');
                        $compile(element.contents())(scope);
                    });
            },
            scope: {}
        };
    });