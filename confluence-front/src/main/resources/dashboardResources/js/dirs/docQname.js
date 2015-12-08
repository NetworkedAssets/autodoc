angular.module("DoC")
    .directive('docQname', function($compile) {
        return {
            link: function(scope,element,attr) {
                //var arr;
                //var qualified;
                //if (typeof scope.source == "string") {
                //    qualified = scope.source;
                //} else if (typeof scope.source == "object" && typeof scope.source.qualified == "string") {
                //    qualified = scope.source.qualified;
                //}
                //if (typeof qualified == "string" && (arr = qualified.split("."))) {
                //    scope.name = arr.pop();
                //} else {
                //    scope.name = qualified;
                //}
                scope.name = qName(scope.source);
                scope.qualified = qName(scope.source,true);
                //console.log(elem);

                var html = '<span title="{{qualified}}"  ui-sref="javadoc.entity({name:qualified})">{{name}}</span>';
                element.html(html);

                $compile(element.contents())(scope);

            },
            scope: {
                source: "=docQname",
                name: "=",
                qualified: "="
            }
        };
    });